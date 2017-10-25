package com.unixkitty.serverutil.command.util;

import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class ModBugStore
{
    private static LinkedHashMap<String, Bug> bugs = new LinkedHashMap<>();
    private static String errorNotExists = ServerUtilMod.MODID + ".commands.mod_bugs.notexists";

    private ModBugStore(){}

    public static Bug getBug(String name)
    {
        return bugs.get(name);
    }

    public static List<TextComponentTranslation> getBugList()
    {
        List<TextComponentTranslation> list = new ArrayList<>();

        for (Bug bug : bugs.values())
        {
            list.add(new TextComponentTranslation(ServerUtilMod.MODID + ".commands.mod_bugs.print_bugs", bug.name, bug.person, bug.description));
        }

        return list;
    }

    public static void addBug(String name, String personUUID, String description) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            throw new CommandException(ServerUtilMod.MODID + ".commands.mod_bugs.exists", name);
        }
        else
        {
            bugs.put(name, new Bug(name, personUUID, description));
        }
    }

    public static void updateBug(String name, String description) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            bugs.put(name, getBug(name).updateDescription(description));
        }
        else
        {
            throw new CommandException(errorNotExists);
        }
    }

    public static void updateBug(String name, BUG_STATUS status) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            bugs.put(name, getBug(name).updateStatus(status));
        }
        else
        {
            throw new CommandException(errorNotExists);
        }
    }

    public static void removeBug(String name) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            bugs.remove(name);
        }
        else
        {
            throw new CommandException(errorNotExists);
        }
    }

    private static class Bug
    {
        private final String name;
        private final String person;
        private String description;
        private BUG_STATUS status;

        private Bug(String name, String personUUID, String description)
        {
            this.name = name;
            this.person = personUUID;
            this.description = description;

            this.status = BUG_STATUS.RELEVANT;
        }

        private Bug updateDescription(String description)
        {
            this.description = description;
            return this;
        }

        private Bug updateStatus(BUG_STATUS status)
        {
            this.status = status;
            return this;
        }
    }

    private enum BUG_STATUS
    {
        RELEVANT(TextFormatting.RED),
        FIXED(TextFormatting.GREEN);

        private TextFormatting formatting;

        BUG_STATUS(TextFormatting formatting)
        {
            this.formatting = formatting;
        }

        public TextFormatting color()
        {
            return formatting;
        }
    }
}
