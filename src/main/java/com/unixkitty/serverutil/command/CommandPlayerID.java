package com.unixkitty.serverutil.command;

import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.PlayerIDTool;
import com.unixkitty.serverutil.util.TranslationHandler;
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
import java.util.Collections;
import java.util.List;

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
        return TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.playerid.usage").getText();
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
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
        return args.length == 1 && index == 1;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length == 0 && sender instanceof EntityPlayerMP)
        {
            sender.sendMessage(new TextComponentString(getCommandSenderAsPlayer(sender).getUniqueID().toString()));
        }
        else if (args.length > 0)
        {
            PlayerIDTool.PlayerID playerID = PlayerIDTool.getIDFromCommand(server, sender, args[0]);
            sender.sendMessage(new TextComponentString(playerID.name() + ", " + UUIDTypeAdapter.fromString(playerID.id())));
        }
        else
        {
            throw new WrongUsageException(getUsage(sender));
        }
    }

}
