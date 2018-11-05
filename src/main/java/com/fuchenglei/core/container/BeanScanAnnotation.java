package com.fuchenglei.core.container;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
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
 * 容器依赖扫描器  备注:废弃 现在是扫描出来所有的之后再处理带注解的 好处是更合理 能全方位统计
 *
 * @author 付成垒
 */
@Deprecated
final class BeanScanAnnotation
{

    static List<Class<?>> getClassName(String packageName, boolean isRecursion, Class<? extends Annotation> annotation)
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
                    classes = getClassFromDir(URLDecoder.decode(url.getPath(), "UTF-8"), packageName, isRecursion, annotation);
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
                    classes = getClassFromJar(jarFile.entries(), packageName, isRecursion, annotation);
                }
            }
        }
        else
        {
            classes = getClassFromJars(((URLClassLoader) loader).getURLs(), packageName, isRecursion, annotation);
        }
        return classes;
    }

    static List<Class<?>> getClassFromDir(String filePath, String packageName, boolean isRecursion, Class<? extends Annotation> annotation)
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
                        classes.addAll(getClassFromDir(URLDecoder.decode(childFile.getPath(), "UTF-8"), packageName + "." + childFile.getName(), isRecursion, annotation));
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
                    String className = packageName + "." + fileName.replace(".class", "");
                    if (className.startsWith(packageName))
                    {
                        Class<?> clazz = choseClass(className, annotation);
                        if (clazz != null) classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    static List<Class<?>> getClassFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion, Class<? extends Annotation> annotation)
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
                    if (className.startsWith(packageName))
                    {
                        Class<?> clazz = choseClass(className, annotation);
                        if (clazz != null) classes.add(clazz);
                    }
                }
            }
        }
        return classes;
    }

    static List<Class<?>> getClassFromJars(URL[] urls, String packageName, boolean isRecursion, Class<? extends Annotation> annotation)
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
                classes.addAll(getClassFromJar(jarFile.entries(), packageName, isRecursion, annotation));
            }
        }
        return classes;
    }

    static Class<?> choseClass(String className, Class<? extends Annotation> annotation)
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
        if ((annotation != null && clazz.isAnnotationPresent(annotation)) || annotation == null)
        {
            return clazz;
        }
        return null;
    }

}