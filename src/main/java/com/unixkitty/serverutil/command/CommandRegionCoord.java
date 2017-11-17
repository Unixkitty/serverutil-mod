package com.unixkitty.serverutil.command;

import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.CoordinateSet;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class CommandRegionCoord extends CommandBase
{
    @Nonnull
    @Override
    public String getName()
    {
        return "regioncoord";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.regioncoord.usage").getText();
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

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            if (sender instanceof EntityPlayerMP)
            {
                EntityPlayerMP player = (EntityPlayerMP) sender;

                sendCoordMessage(sender, player.getServerWorld(), player.posX, player.posZ);
            }
            else
            {
                throw new WrongUsageException(getUsage(sender));
            }
        }
        else if (args.length == 2)
        {
            sendCoordMessage(sender, server.getWorld(0), args[0], args[1]);
        }
        else
        {
            throw new WrongUsageException(getUsage(sender));
        }
    }

    private void sendCoordMessage(ICommandSender sender, WorldServer worldServer, double arg1, double arg2) throws CommandException
    {
        sendCoordMessage(sender, worldServer, String.valueOf(arg1), String.valueOf(arg2));
    }

    private void sendCoordMessage(ICommandSender sender, WorldServer worldServer, String arg1, String arg2) throws CommandException
    {
        CoordinateSet coords = CoordinateSet.parseCoordinates(0, 0, 0, arg1, "64", arg2);
        Chunk chunk = worldServer.getChunkFromBlockCoords(new BlockPos(coords.getX(), coords.getY(), coords.getZ()));

        TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.regioncoord.message",
                "r." + (chunk.x >> 5) + "." + (chunk.z >> 5) + ".mca", chunk.x, chunk.z);
    }
}
