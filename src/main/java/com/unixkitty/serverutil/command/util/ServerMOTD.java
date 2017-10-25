package com.unixkitty.serverutil.command.util;

import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.PropertyManagerCustom;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.unixkitty.serverutil.ServerUtilMod.MODID;
import static com.unixkitty.serverutil.ServerUtilMod.NAME;

@Mod.EventBusSubscriber
public class ServerMOTD
{
    private List<String> MOTD;
    private final PropertyManagerCustom propertyManager = new PropertyManagerCustom(new File(MODID + "-" + "motd" + ".properties"), NAME);

    public ServerMOTD()
    {
        this.buildGreeting();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        String name = player.getName();

        ServerUtilMod.log.info("Player " + name + " is logging in, sending MOTD.");

        sendGreeting(player);
    }

    private void buildGreeting()
    {
        this.MOTD = new ArrayList<>();

        this.MOTD.add(propertyManager.getStringProperty("welcome_message", "Welcome to the server!"));
        this.MOTD.add(propertyManager.getStringProperty("motd.text", "This is an example greeting message."));
    }

    public void reloadProperties()
    {
        propertyManager.loadFile();
        this.buildGreeting();
    }

    public void sendGreeting(EntityPlayer player)
    {
        for (String line : this.MOTD)
        {
            inform(player, line);
        }
    }

    public void sendGreeting(ICommandSender sender)
    {
        for (String line : this.MOTD)
        {
            sender.sendMessage(new TextComponentString(line));
        }
    }

    private void inform(EntityPlayer player, String message)
    {
        player.sendMessage(new TextComponentString(message));
    }
}
