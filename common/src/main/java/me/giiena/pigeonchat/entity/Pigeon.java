package me.giiena.pigeonchat.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.component.PigeonChatComponents;
import me.giiena.pigeonchat.entity.goal.DirectApproachTargetGoal;
import me.giiena.pigeonchat.entity.goal.LaunchTeleportToTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.IntFunction;

/**
 * Messenger pigeon.
 */
@SuppressWarnings("unused")
public class Pigeon extends MessengerAnimal {
    public static final int MIN_SPAWN_COUNT = 2;
    public static final int MAX_SPAWN_COUNT = 6;
    public static final int SPAWN_WEIGHT = 8;

    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID =
            SynchedEntityData.defineId(Pigeon.class, EntityDataSerializers.INT);

    public float flap;
    public float flapSpeed;
    public float oFlap;
    public float oFlapSpeed;

    private float flapping = 1.0f;
    private float nextFlap = 1.0f;

    public Pigeon(Level level) {
        this(EntityTypes.PIGEON, level);
    }

    public Pigeon(EntityType<? extends Pigeon> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl<>(this, 10, false);
    }

    @Override
    protected boolean canBeABaby() {
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(2, new DirectApproachTargetGoal(this, 1.0d, 2.0f, 1.0f));
        this.goalSelector.addGoal(3, new LaunchTeleportToTargetGoal(this, 1.0d));
        this.goalSelector.addGoal(4, new PigeonWanderGoal(this, 1.0d));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 5.0d)
                .add(Attributes.FLYING_SPEED, 0.4d)
                .add(Attributes.MOVEMENT_SPEED, 0.2d);
    }

    @Override
    @NonNull
    protected PathNavigation createNavigation(@NonNull Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.calculateFlapping();
    }

    private void calculateFlapping() {
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (float) (!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3f;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0f, 1.0f);
        if (!this.onGround() && this.flapping < 1.0f) {
            this.flapping = 1.0f;
        }

        this.flapping *= 0.9f;
        Vec3 movement = this.getDeltaMovement();
        if (!this.onGround() && movement.y < 0.0d) {
            this.setDeltaMovement(movement.multiply(1.0, 0.6, 1.0));
        }
        this.flap += this.flapping * 0.8f;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS);
    }

    @Override
    protected boolean considersEntityAsAlly(@NonNull Entity entity) {
        if (entity instanceof Player) return true;
        return entity instanceof AbstractVillager;
    }

    @Override
    public boolean isInvulnerable() {
        final boolean invincibleDelivery = PigeonChatConfig.Common.PIGEON_INVINCIBLE_DELIVERY.get();

        if (invincibleDelivery) {
            return this.hasTarget() && (this.isCarrying() || !this.hasSender());
        } else {
            return super.isInvulnerable();
        }
    }

    @Override
    protected void checkFallDamage(double ya,
                                   boolean onGround,
                                   @NonNull BlockState onState,
                                   @NonNull BlockPos pos) {}

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean canMate(@NonNull Animal partner) {
        return false;
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level,
                                                  @NonNull DifficultyInstance difficulty,
                                                  @NonNull EntitySpawnReason spawnReason,
                                                  @Nullable SpawnGroupData groupData) {
        this.variant(Util.getRandom(Variant.values(), level.getRandom()));
        if (groupData == null) {
            groupData = new AgeableMobGroupData(false);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(
            @NonNull ServerLevel serverLevel,
            @NonNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0f;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    protected boolean omnidirectionalAirMover() {
        return true;
    }

    @Override
    public boolean isDeliverable(ItemStack stack) {
        if (!super.isDeliverable(stack)) return false;
        if (!stack.is(me.giiena.pigeonchat.tag.ItemTags.PIGEON_DELIVERABLES)) return false;
        if (stack.is(me.giiena.pigeonchat.tag.ItemTags.WRITABLE_LETTERS)) return this.isLetterDeliverable(stack);
        return false;
    }

    public Variant variant() {
        return Variant.byID(this.entityData.get(DATA_VARIANT_ID));
    }

    private void variant(Variant variant) {
        this.entityData.set(DATA_VARIANT_ID, variant.id());
    }

    @Override
    @Nullable
    public <T> T get(@NonNull DataComponentType<? extends T> type) {
        return (type == PigeonChatComponents.PIGEON_VARIANT) ?
                castComponentValue(type, this.variant()) : super.get(type);
    }

    @Override
    protected void applyImplicitComponents(@NonNull DataComponentGetter components) {
        this.applyImplicitComponentIfPresent(components, PigeonChatComponents.PIGEON_VARIANT);
        super.applyImplicitComponents(components);
    }

    @Override
    protected <T> boolean applyImplicitComponent(@NonNull DataComponentType<T> type,
                                                 @NonNull T value) {
        if (type == PigeonChatComponents.PIGEON_VARIANT) {
            this.variant(castComponentValue(PigeonChatComponents.PIGEON_VARIANT, value));
            return true;
        }
        return super.applyImplicitComponent(type, value);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_VARIANT_ID, Variant.DEFAULT.id());
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("Variant", Variant.LEGACY_CODEC, this.variant());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.variant(input.read("Variant", Variant.LEGACY_CODEC).orElse(Variant.DEFAULT));
    }

    @Override
    protected boolean canFly() {
        return true;
    }

    public static class PigeonWanderGoal extends WaterAvoidingRandomFlyingGoal {
        private final MessengerAnimal messenger;

        public PigeonWanderGoal(Pigeon mob, double speedModifier) {
            super(mob, speedModifier);
            this.messenger = mob;
        }

        @Override
        public boolean canUse() {
            return !this.messenger.hasTarget();
        }
    }

    public enum Variant implements StringRepresentable {
        GRAY(0, "gray"),
        WHITE(1, "white"),
        RED(2, "red"),
        RED_WHITE(3, "red_white");

        public static final Variant DEFAULT = GRAY;
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        public static final Codec<Variant> LEGACY_CODEC;
        public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC;

        private static final IntFunction<Variant> BY_ID =
                ByIdMap.continuous(Variant::id, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);

        static {
            LEGACY_CODEC = Codec.INT.xmap(BY_ID::apply, Variant::id);
            STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::id);
        }

        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static Variant byID(int id) {
            return BY_ID.apply(id);
        }

        public int id() {
            return this.id;
        }

        @Override
        @NonNull
        public String getSerializedName() {
            return this.name;
        }
    }
}
