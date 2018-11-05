package com.fuchenglei.core.container;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 容器依赖扫描器
 *
 * @author 付成垒
 */
final class BeanScan
{

    static List<Class<?>> getClassName(String packageName, boolean isRecursion, List<String> packages)
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        URL url = loader.getResource(packagePath);
        if (url != null)
        {
            String protocol = url.getProtocol();
            if (protocol.equals("file"))
            {
                try
                {
                    int j = 0;
                    classes.addAll(getClassFromDir(URLDecoder.decode(url.getPath(), "UTF-8"), packageName, isRecursion, packages));
                    if (!URLDecoder.decode(url.getPath(), "UTF-8").endsWith("classes"))
                    {
                        List<Class<?>> cl = getClassFromDir(URLDecoder.decode(url.getPath(), "UTF-8").substring(0, URLDecoder.decode(url.getPath(), "UTF-8").lastIndexOf("/", URLDecoder.decode(url.getPath(), "UTF-8").lastIndexOf("/") - 1)) + "/classes/", packageName, isRecursion, packages);
                        for (j = 0; j < cl.size(); j++)
                        {
                            if (!classes.contains(cl.get(j)))
                                classes.add(cl.get(j));
                        }
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
            else if (protocol.equals("jar"))
            {
                JarFile jarFile = null;
                try
                {
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if (jarFile != null)
                {
                    classes = getClassFromJar(jarFile.entries(), packageName, isRecursion, packages);
                }
            }
        }
        else
        {
            classes = getClassFromJars(((URLClassLoader) loader).getURLs(), packageName, isRecursion, packages);
        }
        return classes;
    }

    static List<Class<?>> getClassFromDir(String filePath, String packageName, boolean isRecursion, List<String> packages)
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File childFile : files)
        {
            if (childFile.isDirectory())
            {
                if (isRecursion)
                {
                    try
                    {
                        classes.addAll(getClassFromDir(URLDecoder.decode(childFile.getPath(), "UTF-8"), packageName + ((packageName == null || "".equals(packageName)) ? "" : ".") + childFile.getName(), isRecursion, packages));
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                String fileName = childFile.getName();
                if (fileName.endsWith(".class"))
                {
                    String className = ((packageName == null || "".equals(packageName.trim())) ? "" : (packageName + ".")) + fileName.replace(".class", "");
                    if (classIScanner(className, packages))
                    {
                        Class<?> clazz = choseClass(className);
                        if (clazz != null)
                            classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    static List<Class<?>> getClassFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion, List<String> packages)
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        while (jarEntries.hasMoreElements())
        {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory())
            {
                String jarEntryName = jarEntry.getName();
                if (jarEntryName.endsWith(".class"))
                {
                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                    if (classIScanner(className, packages))
                    {
                        Class<?> clazz = choseClass(className);
                        if (clazz != null)
                            classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    static List<Class<?>> getClassFromJars(URL[] urls, String packageName, boolean isRecursion, List<String> packages)
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (int i = 0; i < urls.length; i++)
        {
            String classPath = null;
            try
            {
                classPath = URLDecoder.decode(urls[i].getPath(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            if (classPath.endsWith("classes/"))
            {
                continue;
            }
            JarFile jarFile = null;
            try
            {
                jarFile = new JarFile(classPath.substring(classPath.indexOf("/")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if (jarFile != null)
            {
                classes.addAll(getClassFromJar(jarFile.entries(), packageName, isRecursion, packages));
            }
        }
        return classes;
    }

    static Class<?> choseClass(String className)
    {
        Class<?> clazz = null;
        try
        {
            clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return clazz;
    }

    private static boolean classIScanner(String packageName, List<String> packages)
    {
        for (String str : packages)
        {
            if (packageName.startsWith(str))
                return true;
        }
        return false;
    }

}