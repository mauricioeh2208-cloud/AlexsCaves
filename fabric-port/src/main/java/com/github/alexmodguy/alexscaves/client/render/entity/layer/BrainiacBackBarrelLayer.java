package com.github.alexmodguy.alexscaves.client.render.entity.layer;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.BrainiacEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class BrainiacBackBarrelLayer extends RenderLayer<BrainiacEntity, HumanoidModel<BrainiacEntity>> {
    public BrainiacBackBarrelLayer(RenderLayerParent<BrainiacEntity, HumanoidModel<BrainiacEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, BrainiacEntity brainiac, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!brainiac.hasBarrel()) {
            return;
        }
        poseStack.pushPose();
        if (brainiac.isHoldingBarrelInHands()) {
            this.getParentModel().rightArm.translateAndRotate(poseStack);
            poseStack.translate(0.2F, 0.65F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        } else {
            this.getParentModel().body.translateAndRotate(poseStack);
            poseStack.translate(0.0F, 0.35F, 0.32F);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        }
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ACBlockRegistry.WASTE_DRUM.get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
