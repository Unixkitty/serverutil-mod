package com.unixkitty.serverutil.command.util;

import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.PlayerIDTool;
import com.unixkitty.serverutil.util.TranslationHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
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
    private static LinkedTreeMap<String, Bug> bugs = null;
    private static final Charset charset = StandardCharsets.UTF_8;
    private static final File storageFile = new File(ServerUtilMod.instance.getConfigFolder(), "mod_bugs.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static String nosuchbug = ServerUtilMod.MODID + ".commands.mod_bugs.notexists";

    private ModBugStore()
    {
    }

    public static Bug getBug(ICommandSender sender, String name) throws CommandException
    {
        if (bugs != null)
        {
            if (bugs.containsKey(name))
            {
                return bugs.get(name);
            }
            else
            {
                throw new CommandException(TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.mod_bugs.notexists").getText());
            }
        }
        else
        {
            throw new CommandException(TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.mod_bugs.listempty").getText());
        }
    }

    public static List<TextComponentString> getBugList() throws IOException, PlayerNotFoundException
    {
        List<TextComponentString> list;

        if (bugs == null)
        {
            list = Collections.emptyList();
        }
        else
        {
            list = new ArrayList<>();
            int i = 0;
            for (Bug bug : bugs.values())
            {
                list.add(new TextComponentString(++i + ". " + bug.name + " (" + getPlayerAsText(bug.player) + ")" + " [" + bug.status.getFormatted() + "]: " + bug.description));
            }
        }

        return list;
    }

    //TODO
    private static String getPlayerAsText(UUID player) throws IOException, PlayerNotFoundException
    {
        /*PlayerIDTool.PlayerID playerID = PlayerIDTool.getID(player.toString());
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        ResourceLocation resourceLocation = EntityList.getKey(EntityPlayer.class);
        ITextComponent iTextComponent = new TextComponentString(playerID.name());

        nbttagcompound.setString("id", playerID.id());
        if (resourceLocation != null)
        {
            nbttagcompound.setString("type", resourceLocation.toString());
        }
        nbttagcompound.setString("name", playerID.name());
        iTextComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + playerID.name() + " "));
        iTextComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(nbttagcompound.toString())));
        iTextComponent.getStyle().setInsertion(playerID.name());
        iTextComponent.getStyle().setColor(TextFormatting.AQUA);

        return iTextComponent.getFormattedText();*/
        return TextFormatting.AQUA + PlayerIDTool.getID(player.toString()).name() + TextFormatting.RESET;
    }

    public static void addBug(ICommandSender sender, String name, UUID player, String description) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            throw new CommandException(TranslationHandler.translate(sender, ServerUtilMod.MODID + ".commands.mod_bugs.exists", name).getText());
        }
        else
        {
            bugs.put(name, new Bug(name, player, description));
            save();
        }
    }

    public static void updateBug(ICommandSender sender, String name, String description) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            bugs.put(name, getBug(sender, name)).updateDescription(description);
            save();
        }
        else
        {
            throw new CommandException(TranslationHandler.translate(sender, nosuchbug).getText());
        }
    }

    public static void updateBug(ICommandSender sender, String name, BUG_STATUS status) throws CommandException
    {
        if (bugs.containsKey(name))
        {
            bugs.put(name, getBug(sender, name).updateStatus(status));
            save();
        }
        else
        {
            throw new CommandException(TranslationHandler.translate(sender, nosuchbug).getText());
        }
    }

    public static void removeBug(ICommandSender sender, String name) throws CommandException
    {
        if (bugs.remove(name) != null)
        {
            save();
        }
        else
        {
            throw new CommandException(TranslationHandler.translate(sender, nosuchbug).getText());
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
            bugs = new LinkedTreeMap<>();
        }

        if (!storageFile.exists()) return;

        try
        {
            String json = Files.toString(storageFile, charset);
            Type type = new TypeToken<Map<String, Bug>>()
            {
                private static final long serialVersionUID = 1L;
            }.getType();

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
                bugs = new LinkedTreeMap<>();
            }
        }
    }

    /**
     * Used for saving the {@link com.google.gson.Gson#toJson(Object) Gson}
     * representation of the cache to disk
     * <p>
     * copy from {@link net.minecraftforge.common.UsernameCache}
     */
    private static class SaveThread extends Thread
    {

        /**
         * The data that will be saved to disk
         */
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

        public UUID getPlayer()
        {
            return this.player;
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

        public String getFormatted()
        {
            return this.formatting + this.name + TextFormatting.RESET;
        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }
}
