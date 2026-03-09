package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.LanternfishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class LanternfishRenderer extends MobRenderer<LanternfishEntity, CodModel<LanternfishEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/lanternfish.png");

    public LanternfishRenderer(EntityRendererProvider.Context context) {
        super(context, new CodModel<>(context.bakeLayer(ModelLayers.COD)), 0.25F);
        this.addLayer(new LanternfishGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(LanternfishEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(LanternfishEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick, scale);
        float sway = 4.0F * Mth.sin(0.5F * ageInTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(sway));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    private static class LanternfishGlowLayer extends EyesLayer<LanternfishEntity, CodModel<LanternfishEntity>> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/lanternfish_glow.png"));

        private LanternfishGlowLayer(RenderLayerParent<LanternfishEntity, CodModel<LanternfishEntity>> renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
