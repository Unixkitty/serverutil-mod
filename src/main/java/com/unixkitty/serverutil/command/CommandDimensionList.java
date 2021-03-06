package com.unixkitty.serverutil.command;

import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class CommandDimensionList extends CommandBase
{
    @Nonnull
    @Override
    public String getName()
    {
        return "dimensionlist";
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
        for (WorldServer world : server.worlds)
        {
            if (world != null)
            {
                int dimensionId = world.provider.getDimension();
                if (dimensionId == 0)
                {
                    TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.dimensionlist.message.overworld", world.provider.getDimensionType().getName(), dimensionId);
                }
                else
                {
                    TranslationHandler.sendMessage(sender, ServerUtilMod.MODID + ".commands.dimensionlist.message", world.provider.getDimensionType().getName(), dimensionId, world.provider.getSaveFolder());
                }
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}
