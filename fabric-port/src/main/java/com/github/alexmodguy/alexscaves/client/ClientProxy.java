package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.message.ACClientNetworking;
import com.github.alexmodguy.alexscaves.client.render.entity.BrainiacRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.GammaroachRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.LanternfishRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.NucleeperRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.RaycatRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.ThrownWasteDrumEntityRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.TrilocarisRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.TripodfishRenderer;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.client.particle.AcidDropParticle;
import com.github.alexmodguy.alexscaves.client.particle.GammaroachParticle;
import com.github.alexmodguy.alexscaves.client.particle.HazmatBreatheParticle;
import com.github.alexmodguy.alexscaves.client.particle.JellyBeanEatParticle;
import com.github.alexmodguy.alexscaves.client.particle.ProtonParticle;
import com.github.alexmodguy.alexscaves.client.particle.StunStarParticle;
import com.github.alexmodguy.alexscaves.client.particle.VentSmokeParticle;
import com.github.alexmodguy.alexscaves.client.render.entity.RadgillRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.SeaPigRenderer;
import com.github.alexmodguy.alexscaves.client.sound.SugarRushSound;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ClientProxy extends CommonProxy {
    private static final Map<Integer, AbstractTickableSoundInstance> ENTITY_SOUND_INSTANCE_MAP = new HashMap<>();

    @Override
    public void clientInit() {
        ACClientNetworking.init();
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(ACParticleRegistry.ACID_DROP.get(), AcidDropParticle.Factory::new);
        registry.register(ACParticleRegistry.GAMMAROACH.get(), GammaroachParticle.Factory::new);
        registry.register(ACParticleRegistry.HAZMAT_BREATHE.get(), HazmatBreatheParticle.Factory::new);
        registry.register(ACParticleRegistry.BLUE_HAZMAT_BREATHE.get(), HazmatBreatheParticle.BlueFactory::new);
        registry.register(ACParticleRegistry.PROTON.get(), ProtonParticle.Factory::new);
        registry.register(ACParticleRegistry.STUN_STAR.get(), StunStarParticle.Factory::new);
        registry.register(ACParticleRegistry.BLACK_VENT_SMOKE.get(), VentSmokeParticle.BlackFactory::new);
        registry.register(ACParticleRegistry.WHITE_VENT_SMOKE.get(), VentSmokeParticle.WhiteFactory::new);
        registry.register(ACParticleRegistry.GREEN_VENT_SMOKE.get(), VentSmokeParticle.GreenFactory::new);
        registry.register(ACParticleRegistry.RED_VENT_SMOKE.get(), VentSmokeParticle.RedFactory::new);
        registry.register(ACParticleRegistry.JELLY_BEAN_EAT.get(), new JellyBeanEatParticle.Factory());
        EntityRendererRegistry.register(ACEntityRegistry.BRAINIAC.get(), BrainiacRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GAMMAROACH.get(), GammaroachRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LANTERNFISH.get(), LanternfishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEEPER.get(), NucleeperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RAYCAT.get(), RaycatRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RADGILL.get(), RadgillRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SEA_PIG.get(), SeaPigRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.THROWN_WASTE_DRUM.get(), ThrownWasteDrumEntityRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRILOCARIS.get(), TrilocarisRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRIPODFISH.get(), TripodfishRenderer::new);

        FluidRenderHandlerRegistry.INSTANCE.register(
            ACFluidRegistry.ACID.get(),
            ACFluidRegistry.FLOWING_ACID.get(),
            new SimpleFluidRenderHandler(
                ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_still"),
                ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "block/acid_flowing")
            )
        );
        FluidRenderHandlerRegistry.INSTANCE.setBlockTransparency(ACBlockRegistry.ACID.get(), true);

        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderType.cutout(),
            ACBlockRegistry.GEOTHERMAL_VENT.get(),
            ACBlockRegistry.GEOTHERMAL_VENT_MEDIUM.get(),
            ACBlockRegistry.GEOTHERMAL_VENT_THIN.get(),
            ACBlockRegistry.SULFUR_BUD_SMALL.get(),
            ACBlockRegistry.SULFUR_BUD_MEDIUM.get(),
            ACBlockRegistry.SULFUR_BUD_LARGE.get(),
            ACBlockRegistry.SULFUR_CLUSTER.get(),
            ACBlockRegistry.URANIUM_ROD.get()
        );
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ACFluidRegistry.ACID.get(), ACFluidRegistry.FLOWING_ACID.get());
    }

    @Override
    public void playWorldSound(Object soundEmitter, byte type) {
        if (type == 18 && soundEmitter instanceof LivingEntity livingEntity) {
            AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
            if (!(old instanceof SugarRushSound sugarRushSound) || !sugarRushSound.isSameEntity(livingEntity)) {
                SugarRushSound sound = new SugarRushSound(livingEntity);
                ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
        }
    }

    @Override
    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public boolean isTickRateModificationActive(Level level) {
        return level.tickRateManager().tickrate() != 20.0F
            || Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ACEffectRegistry.SUGAR_RUSH.holder());
    }
}
