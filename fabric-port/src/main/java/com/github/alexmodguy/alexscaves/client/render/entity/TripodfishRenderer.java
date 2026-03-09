package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.TripodfishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TripodfishRenderer extends MobRenderer<TripodfishEntity, SalmonModel<TripodfishEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/tripodfish.png");

    public TripodfishRenderer(EntityRendererProvider.Context context) {
        super(context, new SalmonModel<>(context.bakeLayer(ModelLayers.SALMON)), 0.45F);
    }

    @Override
    public ResourceLocation getTextureLocation(TripodfishEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(TripodfishEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        float stand = entity.getStandProgress(partialTick);
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getFishPitch(partialTick)));
        poseStack.mulPose(Axis.YP.rotationDegrees((1.0F - stand) * 4.0F * Mth.sin(0.35F * ageInTicks)));
        if (stand > 0.0F) {
            poseStack.translate(0.0F, 0.05F * stand, 0.0F);
        }
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.15F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    @Override
    protected void scale(TripodfishEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.25F, 1.0F, 1.25F);
    }
}
