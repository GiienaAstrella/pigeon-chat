package me.giiena.pigeonchat.block;

import me.giiena.pigeonchat.Constants;
import me.giiena.pigeonchat.PigeonChatCommon;
import me.giiena.pigeonchat.component.CagedMessenger;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import me.giiena.pigeonchat.inventory.MessengerMenuSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class BirdCageEntity extends BlockEntity implements MessengerMenuSource {
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(-1);

    @Nullable
    private MessengerAnimal cachedMessenger;

    public BirdCageEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntities.BIRD_CAGE, worldPosition, blockState);
    }

    public static BirdCageEntity resolve(Inventory inventory, BlockPos pos) {
        Level level = inventory.player.level();
        if (level.getBlockEntity(pos) instanceof BirdCageEntity cage) {
            return cage;
        }
        throw new IllegalStateException("no bird cage block entity at " + pos);
    }

    private static CompoundTag captureMessenger(MessengerAnimal messenger) {
        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(messenger.problemPath(), Constants.LOG)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter,
                    messenger.registryAccess());
            output.putString("id",
                    BuiltInRegistries.ENTITY_TYPE.getKey(messenger.getType()).toString());
            messenger.saveWithoutId(output);
            return output.buildResult();
        }
    }

    @Nullable
    private CompoundTag messengerTag() {
        CagedMessenger caged = this.components().get(PigeonChatComponents.CAGED_MESSENGER);
        return (caged != null) ? caged.messenger() : null;
    }

    @Nullable
    private MessengerAnimal reconstructMessenger(CompoundTag tag) {
        if (this.level == null) return null;

        try (ProblemReporter.ScopedCollector reporter =
                new ProblemReporter.ScopedCollector(this.problemPath(), Constants.LOG)) {
            ValueInput input = TagValueInput.create(reporter, this.level.registryAccess(), tag);
            return input.getString("id")
                    .flatMap(id ->
                            BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.tryParse(id)))
                    .map(type -> {
                        Entity entity = type.create(this.level, EntitySpawnReason.LOAD);
                        if (entity instanceof MessengerAnimal animal) {
                            animal.load(input);
                            animal.setId(ID_COUNTER.getAndDecrement());
                            return animal;
                        }
                        return null;
                    })
                    .orElse(null);
        }
    }

    @Override
    @NonNull
    public CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level == null) return;

        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }

    @Nullable
    public MessengerAnimal messenger() {
        CompoundTag tag = this.messengerTag();
        if (tag == null) {
            this.cachedMessenger = null;
            return null;
        }
        if (this.cachedMessenger == null && this.level != null) {
            this.cachedMessenger = this.reconstructMessenger(tag);
        }
        return this.cachedMessenger;
    }

    public void messenger(MessengerAnimal messenger) {
        CagedMessenger caged = (messenger != null) ?
                new CagedMessenger(captureMessenger(messenger)) : null;
        this.setComponents(DataComponentMap.builder()
                .addAll(this.components())
                .set(PigeonChatComponents.CAGED_MESSENGER, caged)
                .build());
        this.cachedMessenger = null;
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        MessengerAnimal messenger = this.messenger();
        return messenger != null && !messenger.isCarrying() &&
                player.isWithinBlockInteractionRange(this.worldPosition, 4.0d);
    }

    @Override
    public void assignMessenger(Player sender, InteractionHand hand, ServerPlayer target) {
        if (this.level == null || this.level.isClientSide()) return;

        ItemStack held = sender.getItemInHand(hand);
        MessengerAnimal messenger = this.messenger();
        if (messenger == null || messenger.isCarrying() || held.isEmpty() ||
                !messenger.isDeliverable(held)) {
            return;
        }

        Direction facing = this.getBlockState().getValue(BirdCage.FACING);
        BlockPos front = this.getBlockPos().relative(facing);

        if (!this.level.getBlockState(front).isAir()) {
            sender.sendOverlayMessage(Component.translatable(PigeonChatCommon.langKey("cage",
                    "overlay",
                    "blocked")).withColor(TextColor.RED));
            return;
        }

        if (!this.spawnMessenger(messenger, facing, front)) {
            Constants.LOG.warn("Cannot summon messenger!");
            return;
        }

        this.level.playSound(null,
                this.worldPosition,
                SoundEvents.IRON_DOOR_OPEN,
                SoundSource.BLOCKS,
                1.0f,
                1.0f);
        this.level.playSound(null,
                this.worldPosition,
                SoundEvents.IRON_DOOR_CLOSE,
                SoundSource.BLOCKS,
                1.0f,
                1.0f);

        ItemStack carrying = held.split(1);
        messenger.carrying(carrying);
        messenger.target(target);
        messenger.sender(sender);
        Constants.LOG.info("{} assigned delivery job (bound to {}) to {}",
                sender.getName().getString(),
                target.getName().getString(),
                messenger.getReportableName());
    }

    public boolean spawnMessenger(MessengerAnimal messenger, Direction facing, BlockPos pos) {
        if (this.level == null || this.level.isClientSide()) return false;

        messenger.setId(this.level.getNextEntityId());
        messenger.snapTo(pos.getX() + 0.5d,
                pos.getY(),
                pos.getZ() + 0.5d,
                facing.toYRot(),
                0.0f);
        boolean spawned = this.level.addFreshEntity(messenger);
        // Clear data component
        this.messenger(null);
        // Set cache again
        this.cachedMessenger = messenger;
        return spawned;
    }
}
