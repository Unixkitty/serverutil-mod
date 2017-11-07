package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.command.util.ModBugStore;
import com.unixkitty.serverutil.util.PlayerIDTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandModBugs extends CommandBase implements IInformationSender
{
    private final List<String> aliases;
    private List<TextComponentString> bugListMessage;

    public static final CommandModBugs instance = new CommandModBugs();

    private CommandModBugs()
    {
        aliases = Lists.newArrayList("modbugs", "mod_bugs", "known_bugs", "bugs");

        ModBugStore.load();

        this.buildMessage();
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "mod_bugs";
    }

    @Nonnull
    @Override
    public List<String> getAliases()
    {
        return this.aliases;
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return ServerUtilMod.MODID + ".commands.mod_bugs.usage";
    }

    @Override
    public List<TextComponentString> buildMessage()
    {
        this.bugListMessage = ModBugStore.getBugList();
        return this.bugListMessage;
    }

    @Override
    public void sendMessage(ICommandSender sender)
    {
        if (bugListMessage.equals(Collections.emptyList()))
        {
            sender.sendMessage(new TextComponentTranslation(ServerUtilMod.MODID + ".commands.mod_bugs.listempty"));
        }
        else
        {
            for (TextComponentString line : bugListMessage)
            {
                sender.sendMessage(line);
            }
        }
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            //TODO
            sendMessage(sender);
        }
        else
        {
            switch (args[0])
            {
                case "reload":
                    if (sender.canUseCommand(4, getName()))
                    {
                        sender.sendMessage(new TextComponentString("Reloading mod_bugs file."));
                        ModBugStore.load();
                    }
                    else
                    {
                        nope();
                    }
                    break;
                case "add":
                    if (sender.canUseCommand(2, getName()))
                    {
                        if (args.length >= 3)
                        {
                            ModBugStore.addBug(args[1], UUIDTypeAdapter.fromString(PlayerIDTool.getIDFromCommand(server, sender, args[2]).id()), args[3]);
                            messageSuccess(sender, args[1]);
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        nope();
                    }
                    break;
                case "update":
                    if (sender.canUseCommand(2, getName()))
                    {
                        if (args.length >= 3)
                        {
                            ModBugStore.BUG_STATUS status = null;
                            try
                            {
                                status = ModBugStore.BUG_STATUS.valueOf(args[2].toUpperCase());

                                ModBugStore.updateBug(args[1], status);
                                messageSuccess(sender, args[1]);
                            }
                            catch (IllegalArgumentException e)
                            {

                            }

                            if (status == null)
                            {
                                ModBugStore.updateBug(args[1], args[2]);
                                messageSuccess(sender, args[1]);
                            }
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        nope();
                    }
                    break;
                case "remove":
                    if (sender.canUseCommand(3, getName()))
                    {
                        if (args.length >= 2)
                        {
                            ModBugStore.removeBug(args[1]);
                            sender.sendMessage(new TextComponentTranslation(ServerUtilMod.MODID + ".commands.mod_bugs.bugremoved", args[1]));
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        nope();
                    }
                    break;
                default:
                    throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    private void messageSuccess(ICommandSender sender, String s)
    {
        sender.sendMessage(new TextComponentTranslation(ServerUtilMod.MODID + ".commands.mod_bugs.changesuccess", s));
    }

    private void nope() throws CommandException
    {
        throw new CommandException("commands.generic.permission");
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (isUsernameIndex(args, 1))
        {
            return getListOfStringsMatchingLastWord(args, server.getPlayerProfileCache().getUsernames());
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length >= 3 && args[0].equals("add") && index == 1;
    }
}
