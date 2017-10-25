package com.unixkitty.serverutil.command;

import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.ServerMOTD;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CommandMOTD extends CommandBase
{
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
        return "";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        ServerMOTD motd = ServerUtilMod.instance.getMotdHandler();

        if (args.length >= 1 && args[0].equals("reload") && sender.canUseCommand(4, this.getName()))
        {
            motd.reloadProperties();
            sender.sendMessage(new TextComponentString("Reloading motd file."));
        }
        else
        {
            motd.sendGreeting(sender);
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
