package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HazmatArmorItem extends ArmorItem {

    public HazmatArmorItem(Holder<ArmorMaterial> armorMaterial, Type slot, Properties properties) {
        super(armorMaterial, slot, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        super.inventoryTick(stack, level, entity, slotId, selected);
        if (!(entity instanceof Player player)
            || !stack.is(ACItemRegistry.HAZMAT_MASK.get())
            || player.getItemBySlot(EquipmentSlot.HEAD) != stack
            || Math.cos(player.tickCount * 0.05F) < 0.9F) {
            return;
        }

        Vec3 eyes = player.getEyePosition();
        if (level.random.nextBoolean()) {
            Vec3 leftOffset = new Vec3(0.25D, -0.3D, 0.25D)
                .xRot((float) Math.toRadians(-player.getXRot()))
                .yRot((float) Math.toRadians(-player.getYHeadRot()));
            spawnParticle(level, eyes.add(leftOffset));
        }
        if (level.random.nextBoolean()) {
            Vec3 rightOffset = new Vec3(-0.25D, -0.3D, 0.25D)
                .xRot((float) Math.toRadians(-player.getXRot()))
                .yRot((float) Math.toRadians(-player.getYHeadRot()));
            spawnParticle(level, eyes.add(rightOffset));
        }
    }

    private static void spawnParticle(Level level, Vec3 position) {
        SimpleParticleType particleType = ACParticleRegistry.HAZMAT_BREATHE.get();
        double motionX = (level.random.nextFloat() - 0.5F) * 0.1F;
        double motionY = (level.random.nextFloat() - 0.5F) * 0.1F;
        double motionZ = (level.random.nextFloat() - 0.5F) * 0.1F;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleType, position.x, position.y, position.z, 1, motionX, motionY, motionZ, 0.0D);
        } else {
            level.addParticle(particleType, position.x, position.y, position.z, motionX, motionY, motionZ);
        }
    }
}
