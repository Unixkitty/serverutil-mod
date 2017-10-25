package com.unixkitty.serverutil.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
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
                String worldName;
                int dimensionId = world.provider.getDimension();
                switch (dimensionId)
                {
                    case 0: worldName = "Overworld";
                    break;
                    case 1: worldName = "The Nether";
                    break;
                    case -1: worldName = "The End";
                    break;
                    default: worldName = world.provider.getSaveFolder();
                }
                sender.sendMessage(new TextComponentString("\"" + worldName + "\", " + "id: " + dimensionId));
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}
