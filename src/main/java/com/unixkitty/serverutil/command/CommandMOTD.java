package com.unixkitty.serverutil.command;

import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.util.PropertyManagerCustom;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class CommandMOTD extends CommandBase implements IInformationSender
{
    private List<String> MOTD;
    private final PropertyManagerCustom propertyManager;

    public static final CommandMOTD instance = new CommandMOTD();

    private CommandMOTD()
    {
        this.propertyManager = new PropertyManagerCustom(new File((File) FMLInjectionData.data()[6], ServerUtilMod.MODID + "-" + "motd" + ".properties"), ServerUtilMod.NAME + " motd");
        this.MOTD = this.buildMessage();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        String name = player.getName();

        ServerUtilMod.log.info("Player " + name + " is logging in, sending MOTD.");

        sendMessage(player);
    }

    @Override
    public List<String> buildMessage()
    {
        List<String> message = new ArrayList<>();

        message.add(propertyManager.getStringProperty("welcome_message", "Welcome to the server!"));
        message.add(propertyManager.getStringProperty("motd.text", "This is an example greeting message."));

        return message;
    }

    @Override
    public void reloadProperties()
    {
        propertyManager.loadFile();
        this.MOTD = this.buildMessage();
    }

    private void sendMessage(EntityPlayer player)
    {
        for (String line : this.MOTD)
        {
            informPlayer(player, line);
        }
    }

    @Override
    public void sendMessage(ICommandSender sender)
    {
        for (String line : this.MOTD)
        {
            sender.sendMessage(new TextComponentString(line));
        }
    }

    private void informPlayer(EntityPlayer player, String message)
    {
        player.sendMessage(new TextComponentString(message));
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "motd";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return "";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].equals("reload") && sender.canUseCommand(4, this.getName()))
        {
            this.reloadProperties();
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.reload", "motd");
        }
        else
        {
            this.sendMessage(sender);
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
