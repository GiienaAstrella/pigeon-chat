package me.giiena.pigeonchat.entity;

import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.component.Sealed;
import me.giiena.pigeonchat.inventory.MenuProviders;
import me.giiena.pigeonchat.item.LetterItem;
import me.giiena.pigeonchat.tag.ItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Type of {@link Animal} that can perform delivery jobs.
 * Extend this class to implement a specific entity type.
 * See {@link Pigeon} for an example.
 */
public abstract class MessengerAnimal extends Animal {
    private static final EntityDataAccessor<ItemStack> CARRYING = SynchedEntityData.defineId(
            MessengerAnimal.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> TARGET =
            SynchedEntityData.defineId(
                    MessengerAnimal.class,
                    EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> SENDER =
            SynchedEntityData.defineId(
                    MessengerAnimal.class,
                    EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    protected MessengerAnimal(EntityType<? extends MessengerAnimal> type, Level level) {
        super(type, level);
    }

    /**
     * Returns true if {@code stack} is a deliverable item.
     * Typically, this means that {@code stack} is in the {@code pigeonchat:deliverables} item tag.
     * Further restriction can be implemented by creating a
     * {@code pigeonchat:deliverables/entity_type} subtag and overriding this method.
     * See {@link Pigeon} for an example.
     */
    public boolean isDeliverable(ItemStack stack) {
        return stack.is(ItemTags.DELIVERABLES);
    }

    /**
     * Returns true if {@code stack} is a letter (i.e. in the
     * {@code pigeonchat:writables/letter} item tag) and is deliverable (i.e. has the
     * {@link Sealed} data component).
     */
    public boolean isLetterDeliverable(ItemStack stack) {
        if (stack.is(ItemTags.WRITABLE_LETTERS)) {
            if (stack.getItem() instanceof LetterItem && Sealed.isSealed(stack)) {
                return true;
            }
            return !(stack.getItem() instanceof LetterItem);
        }
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(CARRYING, ItemStack.EMPTY);
        entityData.define(TARGET, Optional.empty());
        entityData.define(SENDER, Optional.empty());
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        ValueOutput messenger = output.child("Messenger");
        if (this.isCarrying()) {
            messenger.store("Carrying", ItemStack.CODEC, this.carrying());
        }
        EntityReference.store(this.targetReference(), messenger, "Target");
        EntityReference.store(this.senderReference(), messenger, "Sender");
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        ValueInput messenger = input.childOrEmpty("Messenger");
        this.carrying(messenger.read("Carrying", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        this.entityData.set(TARGET,
                Optional.ofNullable(EntityReference.read(messenger, "Target")));
        this.entityData.set(SENDER,
                Optional.ofNullable(EntityReference.read(messenger, "Sender")));
    }

    @Override
    protected void dropCustomDeathLoot(@NonNull ServerLevel level,
                                       @NonNull DamageSource source,
                                       boolean killedByPlayer) {
        super.dropCustomDeathLoot(level, source, killedByPlayer);
        if (this.isCarrying()) this.spawnAtLocation(level, this.carrying());
    }

    @Override
    @NonNull
    public InteractionResult mobInteract(Player player, @NonNull InteractionHand hand) {
        final boolean allowReturn = PigeonChatConfig.COMMON.
                getOrDefault(PigeonChatConfig.Key.PIGEON_ALLOW_RETURN,
                        PigeonChatConfig.Default.PIGEON_ALLOW_RETURN);

        ItemStack held = player.getItemInHand(hand);
        if (this.hasTarget()) {
            if (this.isCarrying()) {
                ItemStack carried = this.carrying();
                if (held.isEmpty()) {
                    player.setItemInHand(hand, carried);
                } else if (!player.getInventory().add(carried)) {
                    this.drop(carried, false, false);
                }
                this.carrying(ItemStack.EMPTY);
                if (!allowReturn || !this.hasSender()) {
                    this.target(null);
                    this.sender(null);
                }
                return InteractionResult.SUCCESS;
            } else if (!this.hasSender()) {
                this.target(null);
                this.sender(null);
                return InteractionResult.SUCCESS;
            }

            if (held.isEmpty() || this.isDeliverable(held)) {
                ItemStack carrying = held.copyWithCount(1);
                this.shrinkPlayerItem(player, hand, held);
                this.carrying(carrying);
                this.target(this.sender());
                this.sender(null);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        } else if (this.isDeliverable(held)) {
            if (player instanceof ServerPlayer serverPlayer) {
                MenuProviders.openMessenger(serverPlayer, this, hand);
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    /**
     * Shrinks {@code stack} by 1 regardless of whether {@code player} has infinite materials.
     */
    protected void shrinkPlayerItem(final Player player,
                                    final InteractionHand hand,
                                    final ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.shrink(1);
        player.setItemInHand(hand, newStack);
    }

    /**
     * Returns true if this entity is carrying an item.
     */
    public boolean isCarrying() {
        return !this.entityData.get(CARRYING).isEmpty();
    }

    /**
     * Returns the {@link ItemStack} carried by this entity or an {@link ItemStack#EMPTY}.
     */
    public ItemStack carrying() {
        return this.entityData.get(CARRYING);
    }

    /**
     * Sets {@code stack} to be the item carried by this entity.
     */
    public void carrying(ItemStack stack) {
        if (stack == null) stack = ItemStack.EMPTY;
        this.entityData.set(CARRYING, stack);
    }

    /**
     * Returns true if this entity has a delivery target.
     */
    public boolean hasTarget() {
        return this.entityData.get(TARGET).isPresent();
    }

    /**
     * Returns the reference to the delivery target.
     */
    @Nullable
    protected EntityReference<LivingEntity> targetReference() {
        return this.entityData.get(TARGET).orElse(null);
    }

    /**
     * Returns the delivery target.
     */
    @Nullable
    public LivingEntity target() {
        return EntityReference.getLivingEntity(this.targetReference(), this.level());
    }

    /**
     * Sets the delivery target of this entity.
     */
    public void target(@Nullable LivingEntity entity) {
        this.entityData.set(TARGET, Optional.ofNullable(entity).map(EntityReference::of));
    }

    /**
     * Returns true if this entity has a sender.
     * When performing a return delivery, target is set to the original sender and the sender is
     * unset.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasSender() {
        return this.entityData.get(SENDER).isPresent();
    }

    /**
     * Returns the reference to the delivery sender.
     * When performing a return delivery, target is set to the original sender and the sender is
     * unset.
     */
    @Nullable
    protected EntityReference<LivingEntity> senderReference() {
        return this.entityData.get(SENDER).orElse(null);
    }

    /**
     * Returns the delivery sender.
     * When performing a return delivery, target is set to the original sender and the sender is
     * unset.
     */
    @Nullable
    public LivingEntity sender() {
        return EntityReference.getLivingEntity(this.senderReference(), this.level());
    }

    /**
     * Sets the delivery sender.
     * When performing a return delivery, target is set to the original sender and the sender is
     * unset.
     */
    public void sender(LivingEntity entity) {
        this.entityData.set(SENDER, Optional.ofNullable(entity).map(EntityReference::of));
    }

    /**
     * Returns the name or ID of this entity for logging purposes.
     * If this entity has a custom name, that will be returned.
     * Otherwise, its {@link java.util.UUID} will be returned instead.
     */
    public String getReportableName() {
        String name;
        Component customName = this.getCustomName();
        if (customName != null) {
            name = customName.getString();
        } else {
            name = this.getStringUUID();
        }
        return name;
    }

    /**
     * Attempts to teleport to the delivery target.
     */
    public void tryTeleportToTarget() {
        LivingEntity entity = this.target();
        if (entity != null) {
            this.teleportToAroundBlockPos(entity.blockPosition());
        }
    }

    /**
     * Returns true if this entity should attempt to teleport to the delivery target.
     */
    public boolean shouldTryTeleportToTarget() {
        LivingEntity entity = this.target();
        return entity != null && this.distanceToSqr(entity) >= 144.0d;
    }

    private void teleportToAroundBlockPos(BlockPos target) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = this.random.nextIntBetweenInclusive(-3, 3);
            int dz = this.random.nextIntBetweenInclusive(-3, 3);
            if (Math.abs(dx) >= 2 || Math.abs(dz) >= 2) {
                int dy = this.random.nextIntBetweenInclusive(-1, 1);
                if (this.maybeTeleportTo(
                        target.getX() + dx,
                        target.getY() + dy,
                        target.getZ() + dz)) {
                    return;
                }
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.snapTo(
                    (double) x + 0.5d,
                    y,
                    (double) z + 0.5d,
                    this.getYRot(),
                    this.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathType type = WalkNodeEvaluator.getPathTypeStatic(this, pos);
        if (type != PathType.WALKABLE) {
            return false;
        } else {
            BlockState state = this.level().getBlockState(pos.below());
            if (!this.canFly() && state.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos delta = pos.subtract(this.blockPosition());
                return this.level().noCollision(this, this.getBoundingBox().move(delta));
            }
        }
    }

    protected boolean canFly() {
        return true;
    }
}
