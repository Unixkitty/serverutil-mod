package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.command.util.ModBugStore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class CommandModBugs extends CommandBase implements IInformationSender
{
    private final List<String> aliases;
    private List<TextComponentTranslation> bugListMessage;

    private File file = new File(ServerUtilMod.MODID + "-modbugs" + ".properties");

    public CommandModBugs()
    {
        aliases = Lists.newArrayList("modbugs", "mod_bugs", "known_bugs", "bugs");

        this.bugListMessage = this.buildMessage();
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
    public List<TextComponentTranslation> buildMessage()
    {
        return ModBugStore.getBugList();
    }

    @Override
    public void sendMessage(ICommandSender sender)
    {
        for (TextComponentTranslation line : bugListMessage)
        {
            sender.sendMessage(line);
        }
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {

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
