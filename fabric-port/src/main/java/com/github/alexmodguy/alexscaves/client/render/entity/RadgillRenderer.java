package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.RadgillEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RadgillRenderer extends MobRenderer<RadgillEntity, CodModel<RadgillEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/radgill.png");

    public RadgillRenderer(EntityRendererProvider.Context context) {
        super(context, new CodModel<>(context.bakeLayer(ModelLayers.COD)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(RadgillEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(RadgillEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        float sway = 4.3F * Mth.sin(0.6F * ageInTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(sway));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
