package com.github.alexmodguy.alexscaves.server.entity.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class VerticalSwimmingMoveControl extends MoveControl {
    private final Mob mob;
    private final float secondSpeedModifier;
    private final float maxRotChange;

    public VerticalSwimmingMoveControl(Mob mob, float secondSpeedModifier, float maxRotChange) {
        super(mob);
        this.mob = mob;
        this.secondSpeedModifier = secondSpeedModifier;
        this.maxRotChange = maxRotChange;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double deltaX = this.wantedX - this.mob.getX();
            double deltaY = this.wantedY - this.mob.getY();
            double deltaZ = this.wantedZ - this.mob.getZ();
            double distance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
            double horizontalDistance = Mth.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
            if (distance < 1.0E-6D) {
                this.mob.setSpeed(0.0F);
                return;
            }
            deltaY /= distance;
            this.mob.yBodyRot = this.mob.getYRot();
            float speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.secondSpeedModifier);
            float rotBy = this.maxRotChange;
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, speed * deltaY * 0.4D, 0.0D));
            if (horizontalDistance < this.mob.getBbWidth() + 1.4F) {
                speed *= 0.7F;
                rotBy = horizontalDistance < 0.3F ? 0.0F : Math.max(40.0F, this.maxRotChange);
            }
            float yaw = (float) (Mth.atan2(deltaZ, deltaX) * (180.0D / Math.PI)) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, rotBy));
            this.mob.setSpeed(distance > 0.3D ? speed : 0.0F);
            return;
        }
        this.mob.setSpeed(0.0F);
    }
}
