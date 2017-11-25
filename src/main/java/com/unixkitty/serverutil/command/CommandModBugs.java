package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.command.util.ModBugStore;
import com.unixkitty.serverutil.config.ModConfig;
import com.unixkitty.serverutil.util.PlayerIDTool;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
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

        try
        {
            reloadMessage();
        }
        catch (CommandException e)
        {
            this.bugListMessage = Collections.emptyList();
        }
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

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.mod_bugs.usage").getText();
    }

    @Override
    public List<TextComponentString> buildMessage() throws CommandException
    {
        try
        {
            this.bugListMessage = ModBugStore.getBugList();
        }
        catch (IOException e)
        {
            throw new CommandException(e.getMessage());
        }
        return this.bugListMessage;
    }

    @Override
    public void reloadMessage() throws CommandException
    {
        ModBugStore.load();
        buildMessage();
    }

    @Override
    public void sendMessage(ICommandSender sender)
    {
        if (bugListMessage.equals(Collections.emptyList()))
        {
            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.listempty");
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
            sendMessage(sender);
        }
        else
        {
            switch (args[0])
            {
                case "help":
                    if (args.length >= 2)
                    {
                        switch (args[1])
                        {
                            case "add":
                            case "person":
                            case "remove":
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.usage." + args[1]);
                                break;
                            case "update":
                            case "status":
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.usage.update");
                                TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.usage.status");
                                break;
                            default:
                                throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        throw new WrongUsageException(getUsage(sender));
                    }
                    break;
                case "reload":
                    if (sender.canUseCommand(4, getName()))
                    {
                        TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.reload", "mod_bugs");
                        reloadMessage();
                    }
                    else
                    {
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "add":
                    if (ModConfig.non_ops_mod_bugs_add || sender.canUseCommand(2, getName()))
                    {
                        if (args.length >= 3)
                        {
                            ModBugStore.addBug(sender, args[1], PlayerIDTool.getID(server, sender, args[2]).id(), joinDescription(args, 3));
                            buildMessage();
                            messageSuccess(sender, args[1]);
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "update":
                    if (ModConfig.non_ops_mod_bugs_update || sender.canUseCommand(2, getName()))
                    {
                        if (args.length >= 3)
                        {
                            ModBugStore.BUG_STATUS status = null;
                            try
                            {
                                status = ModBugStore.BUG_STATUS.valueOf(args[2].toUpperCase());

                                ModBugStore.updateBug(sender, args[1], status);
                                buildMessage();
                                messageSuccess(sender, args[1]);
                            }
                            catch (IllegalArgumentException e)
                            {

                            }

                            if (status == null)
                            {
                                ModBugStore.updateBug(sender, args[1], joinDescription(args, 2));
                                buildMessage();
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
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "remove":
                    if (ModConfig.non_ops_mod_bugs_remove || sender.canUseCommand(3, getName()))
                    {
                        if (args.length >= 2)
                        {
                            ModBugStore.removeBug(sender, args[1]);
                            buildMessage();
                            TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.bugremoved", args[1]);
                        }
                        else
                        {
                            throw new WrongUsageException(getUsage(sender));
                        }
                    }
                    else
                    {
                        ServerUtilMod.NOPE();
                    }
                    break;
                case "person":
                    if (args.length >= 2)
                    {
                        PlayerIDTool.PlayerID playerID = PlayerIDTool.getID(server, sender, ModBugStore.getBug(sender, args[1]).getPlayer().toString());
                        sender.sendMessage(new TextComponentString(playerID.name() + ", " + playerID));
                    }
                    else
                    {
                        throw new WrongUsageException(getUsage(sender));
                    }
                    break;
                default:
                    throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    private void messageSuccess(ICommandSender sender, String s)
    {
        TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.mod_bugs.changesuccess", s);
    }

    private String joinDescription(@Nonnull String[] args, int beginFrom)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++)
        {
            if (i >= beginFrom)
            {
                sb.append(args[i]);
                if (i + 1 != args.length)
                {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
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
