package me.giiena.pigeonchat.entity;

import me.giiena.pigeonchat.PigeonChatConfig;
import me.giiena.pigeonchat.entity.goal.FollowTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Messenger pigeon.
 */
@SuppressWarnings("unused")
public class Pigeon extends MessengerAnimal {
    public static final int MIN_SPAWN_COUNT = 2;
    public static final int MAX_SPAWN_COUNT = 6;
    public static final int SPAWN_WEIGHT = 8;

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
        this.goalSelector.addGoal(2, new FollowTargetGoal(this, 1.0d, 2.0f, 1.0f));
        this.goalSelector.addGoal(3, new PigeonWanderGoal(this, 1.0d));
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
        this.flap += this.flapping * 2.0f;
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
        final boolean invincibleDelivery = PigeonChatConfig.COMMON.getOrDefault(
                PigeonChatConfig.Key.PIGEON_INVINCIBLE_DELIVERY,
                PigeonChatConfig.Default.PIGEON_INVINCIBLE_DELIVERY);

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
}
