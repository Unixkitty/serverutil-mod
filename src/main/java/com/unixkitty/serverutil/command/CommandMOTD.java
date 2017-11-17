package com.unixkitty.serverutil.command;

import com.google.common.base.Charsets;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.util.TextFile;
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
    private final List<String> defaultMOTD;
    private final TextFile motdFile;

    public static final CommandMOTD instance = new CommandMOTD();

    private CommandMOTD()
    {
        this.defaultMOTD = new ArrayList<>();
        this.defaultMOTD.add("Welcome to the server!");
        this.defaultMOTD.add("This is an example greeting message");
        this.motdFile = new TextFile(new File((File) FMLInjectionData.data()[6], ServerUtilMod.MODID + "-" + "motd" + ".txt"), Charsets.UTF_8, defaultMOTD, ServerUtilMod.log);
        reloadMessage();
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
        return this.motdFile.readFile();
    }

    @Override
    public void reloadMessage()
    {
        this.MOTD = buildMessage();
    }

    @Override
    public void sendMessage(ICommandSender sender)
    {
        for (String line : this.MOTD)
        {
            sender.sendMessage(new TextComponentString(line));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
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
        return TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.motd.usage").getText();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length >= 1 && args[0].equals("reload") && sender.canUseCommand(4, this.getName()))
        {
            reloadMessage();
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.reload", "motd");
        }
        else
        {
            sendMessage(sender);
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
