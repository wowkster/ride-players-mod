package com.wowkster.rideplayers.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SSetPassengersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void onRemovePassenger(Entity passenger, CallbackInfo ci)
    {
        Entity entity = (Entity) (Object) this;
        if(entity instanceof PlayerEntity && !entity.world.isRemote && passenger instanceof PlayerEntity)
        {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;
            serverPlayer.connection.sendPacket(new SSetPassengersPacket(entity));
        }
    }

    @Inject(method = "setSneaking", at = @At("HEAD"))
    private void onSneak(boolean sneaking, CallbackInfo callbackInfo)
    {
        Entity entity = (Entity) (Object) this;

        if(entity instanceof PlayerEntity && !entity.world.isRemote && !((PlayerEntity) entity).isElytraFlying())
        {
            if(!entity.isPassenger() && entity.isBeingRidden()) entity.getControllingPassenger().dismount();
        }
    }
}