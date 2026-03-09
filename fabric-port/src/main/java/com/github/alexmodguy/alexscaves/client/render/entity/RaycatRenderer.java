package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.RaycatEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class RaycatRenderer extends MobRenderer<RaycatEntity, OcelotModel<RaycatEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raycat.png");

    public RaycatRenderer(EntityRendererProvider.Context context) {
        super(context, new OcelotModel<>(context.bakeLayer(ModelLayers.OCELOT)), 0.4F);
        this.addLayer(new RaycatEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(RaycatEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(RaycatEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.95F, 0.95F, 0.95F);
    }

    private static class RaycatEyesLayer extends EyesLayer<RaycatEntity, OcelotModel<RaycatEntity>> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/raycat_eyes.png"));

        private RaycatEyesLayer(RenderLayerParent<RaycatEntity, OcelotModel<RaycatEntity>> renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
