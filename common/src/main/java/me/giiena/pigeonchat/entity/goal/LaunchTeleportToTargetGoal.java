package me.giiena.pigeonchat.entity.goal;

import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * <p>
 *     Entity goal for moving toward the delivery target before teleporting to within pathfinding
 *     range of the target.
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
public class LaunchTeleportToTargetGoal extends AbstractApproachTargetGoal {
    private boolean teleported;
    private int gaveUpTicks;

    public LaunchTeleportToTargetGoal(final MessengerAnimal messenger,
                                      final double speedModifier) {
        super(messenger, speedModifier);
    }

    @Override
    protected float minDistance() {
        return this.pathfindingRange();
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && !this.teleported && this.gaveUpTicks > 0 &&
                !this.navigation.isDone();
    }

    @Override
    public void start() {
        this.teleported = false;
        this.gaveUpTicks = this.adjustedTickDelay(100);
        Vec3 launchPos = this.pickLaunchPos();
        this.navigation.moveTo(launchPos.x(), launchPos.y(), launchPos.z(), this.speedModifier);
    }

    @Override
    protected void tickApproach() {
        if (--this.gaveUpTicks <= 0 || this.navigation.isDone()) {
            this.messenger.tryTeleportNearTarget(this.pathfindingRange());
            this.teleported = true;
        }
    }

    private Vec3 pickLaunchPos() {
        assert this.target != null;

        float launchDist = this.pathfindingRange();
        Vec3 self = this.messenger.position();

        Vec3 target = this.target.position().subtract(self);
        if (target.lengthSqr() < 1.0e-4) {
            target = new Vec3(this.messenger.getRandom().nextDouble() - 0.5d,
                    0.0d,
                    this.messenger.getRandom().nextDouble() - 0.5d);
        }
        target = target.normalize();

        double spread = Math.PI / 3.0d;
        double angle = (this.messenger.getRandom().nextDouble() - 0.5d) * spread;
        double cos = Mth.cos(angle);
        double sin = Mth.sin(angle);
        double x = target.x() * cos - target.z() * sin;
        double z = target.x() * sin + target.z() * cos;

        double dy = (this.messenger.getRandom().nextDouble() - 0.3d) * (launchDist * 0.3d);

        return self.add(x * launchDist, dy, z * launchDist);
    }
}
