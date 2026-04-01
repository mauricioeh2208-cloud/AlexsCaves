package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticDataAccessors;
import com.github.alexthe666.citadel.CitadelConstants;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "defineSynchedData")
    private void ac_registerMagnetData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MagneticDataAccessors.ITEM_MAGNET_DELTA_X, 0F);
        builder.define(MagneticDataAccessors.ITEM_MAGNET_DELTA_Y, 0F);
        builder.define(MagneticDataAccessors.ITEM_MAGNET_DELTA_Z, 0F);
        builder.define(MagneticDataAccessors.ITEM_MAGNET_ATTACHMENT_DIRECTION, Direction.DOWN);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/item/ItemEntity;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        if (MagnetUtil.isPulledByMagnets(this)) {
            MagnetUtil.tickMagnetism(this);
        }
    }
}
