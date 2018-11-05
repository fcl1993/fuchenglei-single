package com.fuchenglei.util;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * 数据序列化
 *
 * @author 付成垒
 */
public class SerializableUtil<T>
{

    /**
     * 对象转数组
     */
    public byte[] toByteArray(Object obj)
    {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     */
    public T toObject(byte[] bytes)
    {
        Object obj = null;
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        return (T) obj;
    }

    public String toJson(T object)
    {
        return JSONObject.toJSONString(object);
    }

    public T toObject(Class clazz, String str)
    {
        return (T) JSONObject.parseObject(str, clazz);
    }

}