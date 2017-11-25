package com.unixkitty.serverutil.util.file;

import com.unixkitty.serverutil.ServerUtilMod;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * list resources available from the classpath @ *
 */
public class ResourceList
{

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(final Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
        for (final String element : classPathElements)
        {
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    public static Collection<String> getResources(final String pkg, final Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<>();
        final File file = new File(pkg);
        if (file.isDirectory())
        {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        }
        else
        {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<>();

        String jarLocation;
        try
        {
            jarLocation = Paths.get(new URL(file.toString().split("!")[0]).toURI()).toString();
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            throw new Error(e);
        }
        String pkg = FilenameUtils.separatorsToUnix(file.toString().split("!")[1]).replaceFirst("/", "");

        JarFile jarFile;
        try
        {
            jarFile = new JarFile(jarLocation);
        }
        catch (final IOException e)
        {
            throw new Error(e);
        }
        final Enumeration e = jarFile.entries();
        while (e.hasMoreElements())
        {
            final JarEntry entry = (JarEntry) e.nextElement();
            final String fileName = entry.getName();
            if (fileName.startsWith(pkg) && pattern.matcher(fileName).matches())
            {
                retval.add("jar:file:" + jarLocation + "!" + "/" + fileName);
            }
        }
        try
        {
            jarFile.close();
        }
        catch (final IOException e1)
        {
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern)
    {
        final ArrayList<String> retval = new ArrayList<>();
        final File[] fileList = directory.listFiles();
        for (final File file : fileList)
        {
            if (file.isDirectory())
            {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            }
            else
            {
                try
                {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept)
                    {
                        retval.add(fileName);
                    }
                }
                catch (final IOException e)
                {
                    throw new Error(e);
                }
            }
        }
        return retval;
    }
}  