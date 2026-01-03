package ch.supsi.fscli.backend.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Serializer {
    private final Gson gson;
    private final Class<? extends Serializable> clazz;

    /**
     * Constructor for inheritance
     */
    public Serializer(RuntimeTypeAdapterFactory<?> typeAdapterFactory, Class<? extends Serializable> clazz) {
        this.gson = new GsonBuilder().registerTypeAdapterFactory(typeAdapterFactory).create();
        this.clazz = clazz;
    }

    /**
     * Normal constructor
     */
    public Serializer(Class<? extends Serializable> clazz) {
        this.gson = new GsonBuilder().create();
        this.clazz = clazz;
    }

    public void write(Serializable obj) throws IOException {
        if(obj.getFileFullPathName() == null)
            throw new FileNotFoundException("Path is empty");
        try (Writer writer = new FileWriter(obj.getFileFullPathName())) {
            gson.toJson(obj, writer);
            writer.flush();
        }
    }

    public Serializable read(File path) throws IOException {
        try (Reader reader = new FileReader(path)) {
            return gson.fromJson(reader, clazz);
        }
    }
}
