package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ThrownWasteDrumEntityRenderer extends EntityRenderer<ThrownWasteDrumEntity> {
    public ThrownWasteDrumEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(ThrownWasteDrumEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        float progress = (entity.getOnGroundFor() + partialTicks) / (float) ThrownWasteDrumEntity.MAX_TIME;
        if (progress > 1.0F) {
            return;
        }
        float ageInTicks = entity.tickCount + partialTicks;
        float expandScale = 1.0F + Mth.sin(progress * progress * Mth.PI) * 0.5F;
        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F - progress * 0.03F, 1.0F);
        poseStack.pushPose();
        poseStack.scale(expandScale, expandScale - progress * 0.3F, expandScale);
        poseStack.translate(0.0D, 0.5D, 0.0D);
        if (entity.onGround()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(ageInTicks * 25.0F));
        }
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ACBlockRegistry.WASTE_DRUM.get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownWasteDrumEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
