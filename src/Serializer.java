package com.nativedevelopment.smartgrid;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
    private static MLogManager mLogManager = MLogManager.GetInstance();

    public static byte[] serialize(Object obj) {
        // from stack overflow
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = null;
        try {
            o = new ObjectOutputStream(b);
            o.writeObject(obj);
        } catch (IOException e) {
            mLogManager.Error(e.getMessage(), 0);
        }

        return b.toByteArray();
    }

    public static Object deserialize(byte[] bytes) {
        // from stackoverflow
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = null;
        try {
            o = new ObjectInputStream(b);
            return o.readObject();
        } catch (IOException e) {
            mLogManager.Error(e.getMessage(), 0);
        } catch (ClassNotFoundException e) {
            mLogManager.Error(e.getMessage(), 0);
            mLogManager.Error("This is fatal. Good bye!", 0);
            System.exit(-1);
        }
        return null;
    }
}