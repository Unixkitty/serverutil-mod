package com.unixkitty.serverutil.command;

import com.google.common.collect.Lists;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.command.util.IInformationSender;
import com.unixkitty.serverutil.command.util.ModBugStore;
import com.unixkitty.serverutil.util.PlayerUUIDTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
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
        /*if (sender.canUseCommand(2, this.getName()))
        {
            if (args.length > 1)
            {
                if (args[0].equals("add"))
                {
                    if (args.length >= 3)
                    {
                        String description = "";
                        String personUUID;
                        EntityPlayerMP player;

                        try
                        {
                            
                        }
                        catch (NullPointerException e)
                        {

                        }

                        if (args.length >= 4)
                        {
                            description = args[2];
                        }

                        ModBugStore.addBug(args[1], );
                    }
                }
            }
            else
            {
                throw new WrongUsageException(ServerUtilMod.MODID + ".commands.mod_bugs.usage");
            }
        }
        else
        {
            this.sendMessage(sender);
        }*/
    }
}
