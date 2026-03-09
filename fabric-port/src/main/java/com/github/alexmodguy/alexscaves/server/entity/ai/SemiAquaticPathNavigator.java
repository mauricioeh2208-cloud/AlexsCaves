package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SemiAquaticPathNavigator extends WaterBoundPathNavigation {
    public SemiAquaticPathNavigator(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
        return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }

    @Override
    protected boolean canMoveDirectly(Vec3 from, Vec3 to) {
        Vec3 clippedTarget = new Vec3(to.x, to.y + this.mob.getBbHeight() * 0.5D, to.z);
        return this.level.clip(new ClipContext(from, clippedTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.isInWaterOrBubble()
            ? super.getTempMobPos()
            : new Vec3(this.mob.getX(), Math.floor(this.mob.getY() + 0.5D), this.mob.getZ());
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return !this.level.getBlockState(pos.below()).isAir();
    }

    @Override
    public void setCanFloat(boolean canFloat) {
    }
}
