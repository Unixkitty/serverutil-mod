package com.unixkitty.serverutil.util;

import com.google.gson.Gson;
import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class PlayerIDTool
{
    private static final String URL_RETURNS_UUID = "https://api.mojang.com/users/profiles/minecraft";
    private static final String URL_RETURNS_NAME = "https://sessionserver.mojang.com/session/minecraft/profile";
    private static Map<UUID, String> usercache;
    private static PlayerID resultCache = null;
    private static PlayerID result = null;

    private PlayerIDTool(){}

    public static PlayerID getIDFromCommand(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String arg) throws CommandException
    {
        EntityPlayerMP player = null;

        try
        {
            player = CommandBase.getPlayer(server, sender, arg);
        }
        catch (CommandException e)
        {

        }

        if (player != null)
        {
            return cacheResult(new PlayerID(player.getUniqueID(), player.getGameProfile().getName()));
        }
        else
        {
            try
            {
                result = getID(arg);
            }
            catch (IOException e)
            {
                throw new CommandException(e.getMessage());
            }
            return cacheResult(result);
        }
    }

    /**
     *
     * @param input Can be username or uuid
     * @return player id object with two fields
     * @throws IOException if url fetching failed
     * @throws PlayerNotFoundException
     */
    public static PlayerID getID(String input) throws IOException, PlayerNotFoundException
    {
        UUID uuid = null;
        try
        {
            uuid = UUID.fromString(input);
        }
        catch (IllegalArgumentException e)
        {
            try
            {
                uuid = UUIDTypeAdapter.fromString(input);
            }
            catch (IllegalArgumentException e1)
            {

            }
        }

        if (resultCache != null)
        {
            if ((uuid != null && resultCache.id.equals(UUIDTypeAdapter.fromUUID(uuid))) || resultCache.name.equals(input))
            {
                ServerUtilMod.log.debug("Got playerid result from memory.");
                return resultCache;
            }
        }

        if (uuid != null)
        {
            String resultUsername = UsernameCache.getLastKnownUsername(uuid);

            //Online fetch
            if (resultUsername != null)
            {
                ServerUtilMod.log.debug("Got username from cache on disk.");
                return cacheResult(new PlayerID(uuid, resultUsername));
            }
            else
            {
                result = new Gson().fromJson(readUrl(URL_RETURNS_NAME + "/" + uuid.toString()), PlayerID.class);

                ServerUtilMod.log.debug("Made request for username of uuid " + uuid.toString() + " to mojang online api.");
                if (result != null)
                {
                    return cacheResult(result);
                }
            }
        }
        else
        {
            usercache = UsernameCache.getMap();

            if (usercache.containsValue(input))
            {
                for (Map.Entry entry : usercache.entrySet())
                {
                    if (entry.getValue().equals(input))
                    {
                        ServerUtilMod.log.debug("Got uuid from username cache on disk.");
                        return cacheResult(new PlayerID(UUIDTypeAdapter.fromString(entry.getKey().toString()), input));
                    }
                }
            }
            else
            {
                //Online fetch
                result = new Gson().fromJson(readUrl(URL_RETURNS_UUID + "/" + input), PlayerID.class);

                ServerUtilMod.log.debug("Made request for uuid of username " + input + " to mojang online api.");
                if (result != null)
                {
                    return cacheResult(result);
                }
            }
        }
        throw new PlayerNotFoundException("commands.generic.player.notFound", input);
    }

    private static PlayerID cacheResult(PlayerID playerID)
    {
        String methodName = "setUsername";
        try
        {
            Method setUsername = ReflectionHelper.findMethod(UsernameCache.class, methodName, null, UUID.class, String.class);
            setUsername.invoke(UsernameCache.class, UUIDTypeAdapter.fromString(playerID.id), playerID.name);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            ServerUtilMod.log.error("Error: could not save cache to disk, unable to access method \"" + methodName + "\".");
        }

        resultCache = playerID;
        return resultCache;
    }

    private static String readUrl(String url) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
            {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public static class PlayerID
    {
        String id;
        String name;

        PlayerID(UUID id, String name)
        {
            //A string without dashes is stored
            this.id = UUIDTypeAdapter.fromUUID(id);
            this.name = name;
        }

        public String id()
        {
            return id;
        }

        public String name()
        {
            return name;
        }
    }
}
