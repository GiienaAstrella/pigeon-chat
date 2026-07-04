package me.giiena.pigeonchat.entity.goal;

import com.google.common.base.Preconditions;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Entity goal for following delivery target.
 * This is similar to {@link net.minecraft.world.entity.TamableAnimal} but for
 * {@link MessengerAnimal} instead.
 */
public class FollowTargetGoal extends Goal {
    private final MessengerAnimal messenger;
    private final double speedModifier;
    private final PathNavigation navigation;
    private final float startDistance;
    private final float stopDistance;

    private @Nullable LivingEntity target;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public FollowTargetGoal(
            final MessengerAnimal animal,
            final double speedModifier,
            final float startDistance,
            final float stopDistance) {
        this.messenger = animal;
        this.speedModifier = speedModifier;
        this.navigation = animal.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        Preconditions.checkArgument(
                this.navigation instanceof GroundPathNavigation ||
                        this.navigation instanceof FlyingPathNavigation,
                "Unsupported mob type for FollowTargetGoal");
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.messenger.target();
        if (target == null) {
            return false;
        } else if (this.messenger.distanceToSqr(target) <
                (double) Mth.square(this.startDistance)) {
            return false;
        } else {
            this.target = target;
            return true;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null) {
            return false;
        } else if (this.navigation.isDone()) {
            return false;
        } else {
            return this.messenger.distanceToSqr(this.target) >
                    (double) Mth.square(this.stopDistance);
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.messenger.getPathfindingMalus(PathType.WATER);
        this.messenger.setPathfindingMalus(PathType.WATER, 0.0f);
    }

    @Override
    public void stop() {
        this.target = null;
        this.navigation.stop();
        this.messenger.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        boolean isTargetFarAway = this.messenger.shouldTryTeleportToTarget();
        if (!isTargetFarAway) {
            this.messenger.getLookControl().setLookAt(
                    this.target,
                    10.0f,
                    (float) this.messenger.getMaxHeadXRot());
        }

        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (isTargetFarAway) {
                this.messenger.tryTeleportToTarget();
            } else {
                this.navigation.moveTo(this.target, this.speedModifier);
            }
        }
    }
}
