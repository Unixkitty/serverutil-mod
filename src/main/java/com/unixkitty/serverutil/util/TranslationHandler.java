package com.unixkitty.serverutil.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.unixkitty.serverutil.ServerUtilMod;
import com.unixkitty.serverutil.util.file.ResourceList;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TranslationHandler
{
    private static final String DEFAULT_LANGUAGE = "en_us";
    private static final Splitter splitter = Splitter.on("=").limit(2);

    private static final Map<String, Map<String, String>> languages = new HashMap<>();

    private static final String extension = ".lang";
    private static final Pattern pattern = Pattern.compile(".*" + extension);

    private TranslationHandler()
    {
    }

    public static void loadTranslations()
    {
        //TODO test on Windows
        String clazz = TranslationHandler.class.getName().replace(".", "/");
        URL location = TranslationHandler.class.getClassLoader().getResource(clazz + ".class");
        if (location != null)
        {
            String pkg = "assets/" + ServerUtilMod.MODID + "/lang";
            File directory = new File(location.getFile().substring(0, location.getFile().length() - ".class".length() - clazz.length()) + pkg);

            Collection<String> list = ResourceList.getResources(directory.toString(), pattern);
            if (list.size() > 0)
            {
                Map<String, String> translations;
                String[] translation;
                BufferedReader reader;
                InputStream is;
                String locale;
                String line;

                for (String file : list)
                {
                    try
                    {
                        translations = new HashMap<>();
                        locale = new File(file).getName().replace(extension, "");

                        if (file.startsWith("jar:"))
                        {
                            file = file.split("!")[1];
                            is = TranslationHandler.class.getResourceAsStream(file);
                        }
                        else
                        {
                            is = new FileInputStream(file);
                        }

                        reader = new BufferedReader(new InputStreamReader(is));

                        while ((line = reader.readLine()) != null)
                        {
                            translation = Iterables.toArray(splitter.split(line), String.class);
                            if (translation.length == 2)
                            {
                                translations.put(translation[0], translation[1]);
                            }
                        }

                        is.close();
                        reader.close();

                        languages.put(locale, translations);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                ServerUtilMod.log.info("The following languages were loaded successfully: " + languages.keySet());
            }
        }
    }

    public static void sendMessage(ICommandSender sender, String key, Object... args)
    {
        sender.sendMessage(translate(sender, key, args));
    }

    private static String getSenderLocale(ICommandSender sender)
    {
        String locale;

        if (sender instanceof EntityPlayerMP)
        {
            //field_71148_cg
            locale = ObfuscationReflectionHelper.getPrivateValue(EntityPlayerMP.class, (EntityPlayerMP) sender, "language", "field_71148_cg");
        }
        else
        {
            locale = ServerUtilMod.proxy.getClientLocale();
            if (locale == null)
            {
                return DEFAULT_LANGUAGE;
            }
        }

        return locale.toLowerCase();
    }

    public static TextComponentString translate(ICommandSender sender, String key, Object... args)
    {
        return translate(getSenderLocale(sender), key, args);
    }

    public static TextComponentString translate(String locale, String key, Object... args)
    {
        if (languages.containsKey(locale))
        {
            if (languages.get(locale).containsKey(key))
            {
                return translation(locale, key, args);
            }
        }
        else if (languages.containsKey(DEFAULT_LANGUAGE))
        {
            if (languages.get(DEFAULT_LANGUAGE).containsKey(key))
            {
                return translation(DEFAULT_LANGUAGE, key, args);
            }
        }

        return new TextComponentString(key);
    }

    private static TextComponentString translation(String locale, String key, Object... args)
    {
        return new TextComponentString(String.format(languages.get(locale).get(key), args));
    }
}
