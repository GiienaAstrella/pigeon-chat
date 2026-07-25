package me.giiena.pigeonchat.entity.goal;

import com.google.common.base.Preconditions;
import me.giiena.pigeonchat.entity.MessengerAnimal;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * <p>
 *     Abstract goal for approaching delivery target.
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
public abstract class AbstractApproachTargetGoal extends Goal {
    protected final MessengerAnimal messenger;
    protected final double speedModifier;
    protected final PathNavigation navigation;

    @Nullable
    protected LivingEntity target;

    public AbstractApproachTargetGoal(MessengerAnimal messenger, double speedModifier) {
        this.messenger = messenger;
        this.speedModifier = speedModifier;
        this.navigation = messenger.getNavigation();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        Preconditions.checkArgument(this.navigation instanceof GroundPathNavigation ||
                        this.navigation instanceof FlyingPathNavigation,
                "Unsupported mob type for %s",
                this.getClass().getSimpleName());
    }

    protected float minDistance() {
        return 0.0f;
    }

    protected float maxDistance() {
        return Float.POSITIVE_INFINITY;
    }

    protected float pathfindingRange() {
        return (float) this.messenger.getAttributeValue(Attributes.FOLLOW_RANGE);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.messenger.target();
        if (target == null) return false;

        double distSq = this.messenger.distanceToSqr(target);
        if (distSq < (double) Mth.square(this.minDistance()) ||
                distSq >= (double) Mth.square(this.maxDistance())) {
            return false;
        }

        this.target = target;
        return true;
    }

    @Override
    public void stop() {
        this.target = null;
        this.navigation.stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;
        this.messenger.getLookControl().setLookAt(this.target,
                10.0f,
                (float) this.messenger.getMaxHeadXRot());
        this.tickApproach();
    }

    protected abstract void tickApproach();
}
