package com.unixkitty.serverutil.command.util;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraft.command.CommandException;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ModBugStore
{
    private static LinkedHashMap<String, Bug> bugs = null;
    private static final Charset charset = StandardCharsets.UTF_8;
    private static final File storageFile = new File(ServerUtilMod.instance.getConfigFolder(), "mod_bugs.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String nosuchbug = ServerUtilMod.MODID + ".commands.mod_bugs.notexists";

    private ModBugStore(){}

    public static Bug getBug(String name)
    {
        return bugs.get(name);
    }

    public static List<TextComponentString> getBugList()
    {
        List<TextComponentString> list;

        if (bugs == null)
        {
            list = Collections.emptyList();
        }
        else
        {
            list = new ArrayList<>();
            for (Bug bug : bugs.values())
            {
                list.add(new TextComponentString(bug.name + " (" + TextFormatting.AQUA + bug.player + TextFormatting.RESET + ")" + " [" + bug.status + "]: " + bug.description));
            }
        }

        return list;
    }

    public static void addBug(String name, UUID player, String description) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            throw new CommandException(ServerUtilMod.MODID + ".commands.mod_bugs.exists", name);
        }
        else
        {
            bugs.put(name, new Bug(name, player, description));
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
            throw new CommandException(nosuchbug);
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
            throw new CommandException(nosuchbug);
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
            throw new CommandException(nosuchbug);
        }
    }

    private static void save()
    {
        new SaveThread(gson.toJson(bugs)).start();
    }

    public static void load()
    {
        if (bugs == null)
        {
            bugs = Maps.newLinkedHashMap();
        }

        if (!storageFile.exists()) return;

        try
        {
            String json = Files.toString(storageFile, charset);
            Type type = new TypeToken<Map<String, Bug>>() { private static final long serialVersionUID = 1L; }.getType();

            bugs = gson.fromJson(json, type);
        }
        catch (JsonSyntaxException e)
        {
            ServerUtilMod.log.error("Could not parse mod_bugs.json as valid json", e);
        }
        catch (IOException e)
        {
            ServerUtilMod.log.error("Failed to read mod_bugs.json from disk", e);
        }
        finally
        {
            if (bugs == null)
            {
                bugs = Maps.newLinkedHashMap();
            }
        }
    }

    /**
     * Used for saving the {@link com.google.gson.Gson#toJson(Object) Gson}
     * representation of the cache to disk
     *
     * copy from {@link net.minecraftforge.common.UsernameCache}
     */
    private static class SaveThread extends Thread {

        /** The data that will be saved to disk */
        private final String data;

        public SaveThread(String data)
        {
            this.data = data;
        }

        @Override
        public void run()
        {
            try
            {
                // Make sure we don't save when another thread is still saving
                synchronized (storageFile)
                {
                    Files.write(data, storageFile, charset);
                }
            }
            catch (IOException e)
            {
                ServerUtilMod.log.error("Failed to save username cache to file!", e);
            }
        }
    }

    public static class Bug
    {
        private final String name;
        private final UUID player;
        private String description;
        private BUG_STATUS status;

        Bug(String name, UUID personUUID, String description)
        {
            this.name = name;
            this.player = personUUID;
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

    public enum BUG_STATUS
    {
        RELEVANT("relevant", TextFormatting.RED),
        FIXED("fixed", TextFormatting.GREEN);

        TextFormatting formatting;
        String name;

        BUG_STATUS(String name, TextFormatting formatting)
        {
            this.name = name;
            this.formatting = formatting;
        }

        @Override
        public String toString()
        {
            return this.formatting + this.name + TextFormatting.RESET;
        }
    }
}
