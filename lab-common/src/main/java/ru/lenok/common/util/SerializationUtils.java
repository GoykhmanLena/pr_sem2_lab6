package ru.lenok.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SerializationUtils {
    public static final int BUFFER_SIZE = 65000;
    public static final int MAX_CHUNK_SIZE = 40000;
    public static final SerializationUtils INSTANCE = new SerializationUtils();

    private SerializationUtils() {
    }

    public Object deserialize(byte[] data) {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object obj = ois.readObject();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] serialize(Object obj) {
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

    public List<byte[]> serializeAndSplitToChunks(Object obj) {
        byte[] bytes = serialize(obj);
        return splitToChunks(bytes);
    }

    private List<byte[]> splitToChunks(byte[] data) {
        List<byte[]> chunks = new ArrayList<>();
        int start = 0;
        while (start < data.length) {
            int end = Math.min(data.length, start + MAX_CHUNK_SIZE);
            byte[] chunk = Arrays.copyOfRange(data, start, end);
            chunks.add(chunk);
            start = end;
        }
        return chunks;
    }

    public Object deserializeFromChunks(List<byte[]> chunks) {
        byte[] bytes = mergeChunks(chunks);
        return deserialize(bytes);
    }

    private byte[] mergeChunks(List<byte[]> chunks) {
        int totalLength = 0;
        for (byte[] chunk : chunks) {
            totalLength += chunk.length;
        }

        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] chunk : chunks) {
            System.arraycopy(chunk, 0, result, offset, chunk.length);
            offset += chunk.length;
        }

        return result;
    }

}
