package com.wowkster.rideplayers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("rideplayers")
public class Rideplayers {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Rideplayers() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerClick(final PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof PlayerEntity) || event.getWorld().isRemote) return;

        PlayerEntity target = (PlayerEntity) event.getTarget();
        ServerPlayerEntity clicker = (ServerPlayerEntity) event.getPlayer();

        ServerPlayerEntity targetPlayer = event.getWorld().getServer().getPlayerList().getPlayerByUUID(target.getUniqueID());

        if(target.getDistance(clicker) >= 4 || event.getHand() != Hand.MAIN_HAND || clicker.getHeldItemMainhand() != ItemStack.EMPTY) return;

        clicker.connection.sendPacket(new SSetPassengersPacket(clicker));
        clicker.connection.sendPacket(new SSetPassengersPacket(target));

        assert targetPlayer != null;
        targetPlayer.connection.sendPacket(new SSetPassengersPacket(clicker));
        targetPlayer.connection.sendPacket(new SSetPassengersPacket(target));

        clicker.startRiding(target);

        clicker.connection.sendPacket(new SSetPassengersPacket(clicker));
        clicker.connection.sendPacket(new SSetPassengersPacket(target));

        targetPlayer.connection.sendPacket(new SSetPassengersPacket(clicker));
        targetPlayer.connection.sendPacket(new SSetPassengersPacket(target));
    }
}
