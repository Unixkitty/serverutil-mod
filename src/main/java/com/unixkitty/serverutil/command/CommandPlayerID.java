package com.unixkitty.serverutil.command;

import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.PlayerUUIDTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandPlayerID extends CommandBase
{
    @Nonnull
    @Override
    public String getName()
    {
        return "playerid";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return ServerUtilMod.MODID + ".commands.playerid.usage";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
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
        return args.length == 2 && args[0].equals("id") && index == 1;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            if (args.length == 1 && !(sender instanceof EntityPlayerMP))
            {
                throw new WrongUsageException(getUsage(sender));
            }
            if (args[0].contains("name"))
            {
                try
                {
                    UUID uuid = UUIDTypeAdapter.fromString(args[1]);
                    sender.sendMessage(new TextComponentString(PlayerUUIDTool.getUsername(UUIDTypeAdapter.fromUUID(uuid)) + ", " + uuid));
                }
                catch (IOException e)
                {
                    throw new CommandException("Error: " + e.getMessage());
                }
            }
            else if (args[0].contains("id"))
            {
                try
                {
                    sender.sendMessage(new TextComponentString(args[1] + ", " + PlayerUUIDTool.getID(args[1])));
                }
                catch (IOException e)
                {
                    throw new CommandException("Error: " + e.getMessage());
                }
            }
        }
        else if (sender instanceof EntityPlayerMP)
        {
            sender.sendMessage(new TextComponentString(UUIDTypeAdapter.fromUUID(getCommandSenderAsPlayer(sender).getUniqueID())));
        }
        else
        {
            throw new WrongUsageException(getUsage(sender));
        }
    }

}
