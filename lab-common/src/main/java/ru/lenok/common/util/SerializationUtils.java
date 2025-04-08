package ru.lenok.common.util;

import java.io.*;

public class SerializationUtils {
    public static final int BUFFER_SIZE = 65535;
    public static final SerializationUtils INSTANCE = new SerializationUtils();
    private SerializationUtils(){}
    public Object deserialize(byte[] data){
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            // System.out.println("Объект десериализован: " + obj);
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] serialize(Object obj){
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_SIZE);
             ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream)) {
            oos.writeObject(obj);
            oos.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
