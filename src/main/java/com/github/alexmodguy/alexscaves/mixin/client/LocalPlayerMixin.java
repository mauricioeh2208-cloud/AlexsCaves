package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow
    public Input input;

    private boolean wasUnderAcid;
    private boolean wasUnderPurpleSoda;
    
    @Unique
    private float ac_savedForwardImpulse;
    @Unique
    private float ac_savedLeftImpulse;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "Lnet/minecraft/client/player/LocalPlayer;updateIsUnderwater()Z",
            at = @At("TAIL"))
    private void ac_updateIsUnderwater(CallbackInfoReturnable<Boolean> cir) {
        boolean underAcid = this.getEyeInFluidType().equals(ACFluidRegistry.ACID_FLUID_TYPE.get());
        boolean underPurpleSoda = this.getEyeInFluidType().equals(ACFluidRegistry.PURPLE_SODA_FLUID_TYPE.get());
        if(wasUnderAcid != underAcid){
            if(underAcid){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.ACID_SUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }else{
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.ACID_UNSUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        }
        if(wasUnderPurpleSoda != underPurpleSoda){
            if(underPurpleSoda){
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.PURPLE_SODA_SUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }else{
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), ACSoundRegistry.PURPLE_SODA_UNSUBMERGE.get(), SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }
        }
        wasUnderAcid = underAcid;
        wasUnderPurpleSoda = underPurpleSoda;
    }

    /**
     * Save the original movement input values before vanilla applies the using-item slowdown.
     * This is injected right after the MovementInputUpdateEvent is fired.
     */
    @Inject(method = "aiStep",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/ClientHooks;onMovementInputUpdate(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/client/player/Input;)V",
                    shift = At.Shift.AFTER))
    private void ac_saveMovementInputBeforeSlowdown(CallbackInfo ci) {
        // Save the original input values before vanilla applies the 0.2x slowdown for using items
        ac_savedForwardImpulse = this.input.forwardImpulse;
        ac_savedLeftImpulse = this.input.leftImpulse;
    }

    /**
     * Restore movement input when using Galena Gauntlet to prevent the vanilla using-item slowdown.
     * The Galena Gauntlet is a combat item that shouldn't slow the player while being used.
     * This is injected at the autoJumpTime check, which is right after the slowdown is applied.
     */
    @Inject(method = "aiStep",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/player/LocalPlayer;autoJumpTime:I",
                    ordinal = 0))
    private void ac_restoreMovementInputForGalenaGauntlet(CallbackInfo ci) {
        // If the player is using the Galena Gauntlet, restore the original movement speed
        if (this.isUsingItem() && this.getUseItem().is(ACItemRegistry.GALENA_GAUNTLET.get())) {
            this.input.forwardImpulse = ac_savedForwardImpulse;
            this.input.leftImpulse = ac_savedLeftImpulse;
        }
    }
}
