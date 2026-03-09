package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.SeaPigEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SeaPigRenderer extends MobRenderer<SeaPigEntity, SalmonModel<SeaPigEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/sea_pig.png");

    public SeaPigRenderer(EntityRendererProvider.Context context) {
        super(context, new SalmonModel<>(context.bakeLayer(ModelLayers.SALMON)), 0.35F);
    }

    @Override
    public ResourceLocation getTextureLocation(SeaPigEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(SeaPigEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        float sway = 3.5F * Mth.sin(0.3F * ageInTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(sway));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.15F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    @Override
    protected void scale(SeaPigEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.0F, 1.2F);
    }
}
