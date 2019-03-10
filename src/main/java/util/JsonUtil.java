package util;

import com.google.gson.Gson;

import java.util.Map;

public class JsonUtil {

    public static String toJson(Map map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static Map toMap(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, Map.class);
    }
}
