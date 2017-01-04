package com.paulhoang;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by paul on 04/01/2017.
 */
public class JsonService {

    private Gson gson;
    private static JsonService jsonService;

    private JsonService() {
        JsonSerializer<Date> ser = (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.getTime());
        JsonDeserializer<Date> deser = (json, typeOfT, context) -> json == null ? null : new Date(json.getAsLong());

        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser).create();
    }

    public static JsonService getInstance() {
        if(jsonService == null)
        {
            jsonService = new JsonService();
        }

        return jsonService;
    }

    public Gson getGson(){
        return this.gson;
    }

}
