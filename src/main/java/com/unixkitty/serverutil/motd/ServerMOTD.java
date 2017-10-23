package com.unixkitty.serverutil.motd;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerMOTD
{
    private List<String> MOTD = new ArrayList<>();

    /*@SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        String name = player.getName();

        ServerUtilMod.log.info("Player " + name + " is logging in, sending MOTD.");

        inform(player, ServerUtilMod.propHandler.getProperty("welcome_message"));

        sendGreeting(player);
    }*/

    public void addTextToGreeting(String text)
    {
        MOTD.add(text);
    }

    private void sendGreeting(EntityPlayer player)
    {
        for (String line : MOTD)
        {
            inform(player, line);
        }
    }

    private void inform(EntityPlayer player, String message)
    {
        player.sendMessage(new TextComponentString(message));
    }
}
