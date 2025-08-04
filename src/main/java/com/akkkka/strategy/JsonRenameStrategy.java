package com.akkkka.strategy;

import com.akkkka.Parsable;
import com.akkkka.RenameStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;

import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 14:19
 * @description:
 */
public class JsonRenameStrategy implements RenameStrategy, Parsable<JsonObject> {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JsonRenameStrategy.class.getName());

    private static final Gson gson = new Gson();
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".json");
    }

    @Override
    public void rename(File file) {
        JsonObject jo = parse(file);
        if(file.getName().equals("sentinel-"+MY_PROJECT_NAME+"-gateway.json")){
            jo.getAsJsonArray().forEach(jsonElement -> {
                String newValue = jsonElement.getAsJsonObject().
                        get("resource").getAsString().replace(ruoyi_STRING, MY_PROJECT_NAME);
                jo.addProperty("resource", newValue);
            });
        }
    }


    @Override
    public JsonObject parse(File file) {
        try(FileReader fileReader = new FileReader(file)){
            return gson.fromJson(fileReader, JsonObject.class);
        }catch (IOException e){
            logger.log(Level.SEVERE, "解析JSON文件失败: " + file.getAbsolutePath(), e);
        }
        return null;
    }
}
