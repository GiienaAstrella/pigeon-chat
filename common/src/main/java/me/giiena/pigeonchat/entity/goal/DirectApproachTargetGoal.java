package me.giiena.pigeonchat.entity.goal;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.util.Mth;

/**
 * <p>
 *     Entity goal for moving to the delivery target.
 * </p>
 *
 * <p>
 *     Recommended goals are {@link DirectApproachTargetGoal} and
 *     {@link LaunchTeleportToTargetGoal} in the following order:
 *     <pre>{@code
 *         // Float and look goals.
 *         this.goalSelector.addGoal(2, new DirectApproachTargetGoal(this, 1.0d, 2.0f, 1.0f));
 *         this.goalSelector.addGoal(3, new LaunchTeleportToTargetGoal(this, 1.0d));
 *         // Wandering goals.
 *     }</pre>
 * </p>
 */
public class DirectApproachTargetGoal extends AbstractApproachTargetGoal {
    private final float startDistance;
    private final float stopDistance;

    private int timeToRecalcPath;

    public DirectApproachTargetGoal(final MessengerAnimal messenger,
                                    final double speedModifier,
                                    final float startDistance,
                                    final float stopDistance) {
        super(messenger, speedModifier);
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
    }

    @Override
    protected float minDistance() {
        return this.startDistance;
    }

    @Override
    protected float maxDistance() {
        return this.pathfindingRange();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.target == null || this.navigation.isDone()) return false;

        double distSq = this.messenger.distanceToSqr(this.target);
        return distSq > (double) Mth.square(this.stopDistance) &&
                distSq < (double) Mth.square(this.maxDistance());
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void tickApproach() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.navigation.moveTo(this.target, this.speedModifier);
        }
    }
}
