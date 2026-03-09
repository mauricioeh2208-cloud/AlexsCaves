package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.BrainiacBackBarrelLayer;
import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class BrainiacRenderer extends HumanoidMobRenderer<BrainiacEntity, HumanoidModel<BrainiacEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/brainiac.png");

    public BrainiacRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        this.addLayer(new BrainiacBackBarrelLayer(this));
        this.addLayer(new BrainiacGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BrainiacEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(BrainiacEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.15F, 1.15F, 1.15F);
    }

    private static class BrainiacGlowLayer extends EyesLayer<BrainiacEntity, HumanoidModel<BrainiacEntity>> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/brainiac_glow.png"));

        private BrainiacGlowLayer(RenderLayerParent<BrainiacEntity, HumanoidModel<BrainiacEntity>> renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
