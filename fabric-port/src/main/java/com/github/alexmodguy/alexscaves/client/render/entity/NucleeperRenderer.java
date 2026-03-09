package com.github.alexmodguy.alexscaves.client.render.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.living.NucleeperEntity;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NucleeperRenderer extends CreeperRenderer {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/entity/nucleeper/nucleeper.png");

    public NucleeperRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Creeper entity) {
        return TEXTURE;
    }

    public ResourceLocation getTextureLocation(NucleeperEntity entity) {
        return TEXTURE;
    }
}
