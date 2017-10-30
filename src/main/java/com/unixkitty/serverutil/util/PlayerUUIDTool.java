package com.unixkitty.serverutil.util;

import com.google.gson.Gson;
import com.mojang.util.UUIDTypeAdapter;
import com.unixkitty.serverutil.ServerUtilMod;
import net.minecraftforge.common.UsernameCache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class PlayerUUIDTool
{
    private static final String URL_RETURNS_UUID = "https://api.mojang.com/users/profiles/minecraft";
    private static final String URL_RETURNS_NAME = "https://sessionserver.mojang.com/session/minecraft/profile";
    private static PlayerID result = null;

    private PlayerUUIDTool(){}

    public static UUID getID(String username) throws IOException
    {
        if (result != null && result.name.equals(username))
        {
            ServerUtilMod.log.debug("Got uuid from last result in memory.");
            return UUIDTypeAdapter.fromString(result.id);
        }
        else
        {
            Map<UUID, String> cache = UsernameCache.getMap();

            if (cache.containsValue(username))
            {
                for (Map.Entry entry : cache.entrySet())
                {
                    if (entry.getValue().equals(username))
                    {
                        ServerUtilMod.log.debug("Got uuid from username cache on disk.");
                        return UUIDTypeAdapter.fromString(entry.getKey().toString());
                    }
                }
            }
            else
            {
                //Online fetch
                result = new Gson().fromJson(readUrl(URL_RETURNS_UUID + "/" + username), PlayerID.class);

                ServerUtilMod.log.debug("Got uuid from mojang api online.");
                return UUIDTypeAdapter.fromString(result.id);
            }
        }
        return null;
    }

    public static String getUsername(String playerUUID) throws IOException
    {
        if (result != null && result.id.equals(playerUUID))
        {
            ServerUtilMod.log.debug("Got username from last result in memory.");
            return result.name;
        }
        else
        {
            //All the conversion is to make sure it's of correct format
            String resultUsername = UsernameCache.getLastKnownUsername(UUIDTypeAdapter.fromString(playerUUID));

            //Online fetch
            if (resultUsername == null)
            {
                result = new Gson().fromJson(readUrl(URL_RETURNS_NAME + "/" + playerUUID), PlayerID.class);

                ServerUtilMod.log.debug("Got username from mojang api online.");
                return result.name;
            }
            else
            {
                ServerUtilMod.log.debug("Got username from cache on disk.");
                return resultUsername;
            }
        }
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

    class PlayerID
    {
        String id;
        String name;
    }
}
