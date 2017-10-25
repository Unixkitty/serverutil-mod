package com.unixkitty.serverutil.command.util;

import net.minecraft.command.ICommandSender;

import java.util.List;

public interface IInformationSender
{
    List buildMessage();

    default void reloadProperties()
    {
        this.buildMessage();
    }

    void sendMessage(ICommandSender sender);
}
