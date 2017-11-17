package com.unixkitty.serverutil.command.util;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.List;

public interface IInformationSender
{
    List buildMessage() throws CommandException;

    default void reloadMessage() throws CommandException
    {
        this.buildMessage();
    }

    void sendMessage(ICommandSender sender);
}
