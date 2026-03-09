package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.TrilocarisEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TrilocarisRenderer extends MobRenderer<TrilocarisEntity, SilverfishModel<TrilocarisEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/trilocaris.png");

    public TrilocarisRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(TrilocarisEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(TrilocarisEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        float crawl = entity.getGroundProgress(partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(6.0F * Mth.sin(0.35F * ageInTicks)));
        if (crawl > 0.0F) {
            poseStack.translate(0.0F, 0.02F * crawl, 0.0F);
        }
        if (!entity.isInWater()) {
            poseStack.translate(0.05F, 0.1F, -0.05F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    @Override
    protected void scale(TrilocarisEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.0F, 1.2F);
    }
}
