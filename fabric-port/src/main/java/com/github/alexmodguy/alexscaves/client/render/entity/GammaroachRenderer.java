package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.GammaroachEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SilverfishModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class GammaroachRenderer extends MobRenderer<GammaroachEntity, SilverfishModel<GammaroachEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gammaroach.png");

    public GammaroachRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishModel<>(context.bakeLayer(ModelLayers.SILVERFISH)), 0.5F);
        this.addLayer(new GammaroachEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GammaroachEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(GammaroachEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
    }

    private static class GammaroachEyesLayer extends EyesLayer<GammaroachEntity, SilverfishModel<GammaroachEntity>> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/gammaroach_eyes.png"));

        private GammaroachEyesLayer(RenderLayerParent<GammaroachEntity, SilverfishModel<GammaroachEntity>> renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
