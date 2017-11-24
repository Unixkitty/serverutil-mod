package com.unixkitty.serverutil.command;

import com.google.common.base.Charsets;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.config.ModConfig;
import com.unixkitty.serverutil.util.PlayerIDTool;
import com.unixkitty.serverutil.util.TranslationHandler;
import com.unixkitty.serverutil.util.file.TextFile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class CommandMOTD extends CommandBase implements IInformationSender
{
    private List<String> MOTD;
    private final List<String> defaultMOTD;
    private final TextFile motdFile;

    private List<String> optoutList = new ArrayList<>();
    private final TextFile optoutListFile;

    public static final CommandMOTD instance = new CommandMOTD();

    private CommandMOTD()
    {
        this.defaultMOTD = new ArrayList<>();
        this.defaultMOTD.add("Welcome to the server!");
        this.defaultMOTD.add("This is an example greeting message");
        this.motdFile = new TextFile(new File((File) FMLInjectionData.data()[6], ServerUtilMod.MODID + "-" + "motd" + ".txt"), Charsets.UTF_8, defaultMOTD, ServerUtilMod.log);
        reloadMessage();

        this.optoutListFile = new TextFile(new File(ServerUtilMod.instance.getConfigFolder(), "motd_optout.txt"), Charsets.UTF_8, Collections.emptyList(), ServerUtilMod.log);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        if (!ServerUtilMod.proxy.isClient() && !optoutList.contains(player.getUniqueID().toString()))
        {
            String name = player.getName();

            ServerUtilMod.log.info("Player " + name + " is logging in, sending MOTD.");

            sendMessage(player);
        }

        if (ModConfig.showModBugsAsMOTD)
        {
            CommandModBugs.instance.sendMessage(player);
        }
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
        if (args.length >= 1)
        {
            switch (args[0])
            {
                case "reload":
                    if (sender.canUseCommand(4, getName()))
                    {
                        reloadMessage();
                        TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.reload", "motd");
                    }
                    else
                    {
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "disable":
                    if (sender.canUseCommand(3, getName()))
                    {
                        if (args.length >= 2)
                        {
                            PlayerIDTool.PlayerID playerID = PlayerIDTool.getID(server, sender, args[1]);
                            if (!optoutList.contains(playerID.toString()))
                            {
                                optoutList.add(playerID.toString());
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.added", playerID.name());
                                optoutListFile.saveFile(optoutList);
                            }
                            else
                            {
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.added.already", playerID.name());
                            }
                        }
                        else if (sender instanceof EntityPlayerMP)
                        {
                            addSelf(sender);
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else if (sender instanceof EntityPlayerMP && ModConfig.non_ops_motd_can_optout)
                    {
                        addSelf(sender);
                    }
                    else
                    {
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "enable":
                    if (sender.canUseCommand(3, getName()))
                    {
                        if (args.length >= 2)
                        {
                            PlayerIDTool.PlayerID playerID = PlayerIDTool.getID(server, sender, args[1]);
                            if (optoutList.remove(playerID.toString()))
                            {
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.removed", playerID.name());
                                optoutListFile.saveFile(optoutList);
                            }
                            else
                            {
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.removed.already", playerID.name());
                            }
                        }
                        else if (sender instanceof EntityPlayerMP)
                        {
                            removeSelf(sender);
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else if (sender instanceof EntityPlayerMP && ModConfig.non_ops_motd_can_optout)
                    {
                        removeSelf(sender);
                    }
                    break;
                default:
                    throw new WrongUsageException(getUsage(sender));
            }
        }
        else
        {
            sendMessage(sender);
        }
    }

    private void addSelf(ICommandSender sender)
    {
        String id = ((EntityPlayerMP) sender).getUniqueID().toString();
        if (!optoutList.contains(id))
        {
            optoutList.add(id);
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.added", sender.getName());
            optoutListFile.saveFile(optoutList);
        }
        else
        {
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.added.already", sender.getName());
        }
    }

    private void removeSelf(ICommandSender sender)
    {
        String id = ((EntityPlayerMP) sender).getUniqueID().toString();
        if (optoutList.remove(id))
        {
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.removed", sender.getName());
            optoutListFile.saveFile(optoutList);
        }
        else
        {
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.motd.optout.removed.already", sender.getName());
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
