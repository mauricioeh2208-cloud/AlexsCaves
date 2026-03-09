package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

public class RageEffect extends MobEffect {
    private static final ResourceLocation RAGE_ATTACK_DAMAGE_ID =
        ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rage_attack_boost");

    protected RageEffect() {
        super(MobEffectCategory.NEUTRAL, 0XBA2E2E);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int level) {
        AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            float levelScale = (1 + level) * 2.5F;
            float damageBonus = (1F - (entity.getHealth() / entity.getMaxHealth())) * levelScale;
            removeRageModifier(entity);
            attribute.addTransientModifier(new AttributeModifier(RAGE_ATTACK_DAMAGE_ID, damageBonus, AttributeModifier.Operation.ADD_VALUE));
        }
        if (!entity.level().isClientSide && entity instanceof Mob mob && mob.getTarget() == null && entity.tickCount % 10 == 0 && entity.getRandom().nextInt(2) == 0) {
            AABB aabb = mob.getBoundingBox().inflate(80);
            LivingEntity randomTarget = null;
            for (LivingEntity living : mob.level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE)) {
                if ((randomTarget == null || randomTarget.distanceTo(mob) > living.distanceTo(mob) && mob.getRandom().nextInt(2) == 0) && !mob.is(living)) {
                    if (!mob.isAlliedTo(living) && !living.isAlliedTo(mob) && mob.canAttack(living)) {
                        randomTarget = living;
                    }
                }
            }
            if (randomTarget != null && !randomTarget.is(mob)) {
                mob.setLastHurtByMob(randomTarget);
                mob.setTarget(randomTarget);
                for (int i = 0; i < 3 + mob.getRandom().nextInt(3); i++) {
                    ((ServerLevel) entity.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, mob.getRandomX(0.5F), mob.getEyeY() + mob.getRandom().nextFloat() * 0.2F, mob.getRandomZ(0.5F), 0, 0, 0, 0, 1.0D);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration > 0;
    }

    @Override
    public void onMobRemoved(LivingEntity entity, int amplifier, net.minecraft.world.entity.Entity.RemovalReason reason) {
        removeRageModifier(entity);
        super.onMobRemoved(entity, amplifier, reason);
    }

    private void removeRageModifier(LivingEntity entity) {
        AttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null && attribute.getModifier(RAGE_ATTACK_DAMAGE_ID) != null) {
            attribute.removeModifier(RAGE_ATTACK_DAMAGE_ID);
        }
    }
}
