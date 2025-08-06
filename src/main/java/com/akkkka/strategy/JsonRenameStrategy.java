package com.akkkka.strategy;

import com.akkkka.Parsable;
import com.akkkka.RenameStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 14:19
 * @description:
 */
public class JsonRenameStrategy implements RenameStrategy, Parsable<JsonElement> {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JsonRenameStrategy.class.getName());

    private static final Gson gson = new Gson();
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".json");
    }

    @Override
    public void rename(File file) {
        JsonElement je = parse(file);
        String filename = file.getName();
        if(filename.equals("sentinel-"+MY_PROJECT_NAME+"-gateway.json")){
            je.getAsJsonArray().forEach(jsonElement -> {
                JsonObject item = jsonElement.getAsJsonObject();
                String newValue = jsonElement.getAsJsonObject().
                        get("resource").getAsString().replace(ruoyi_STRING, MY_PROJECT_NAME);
                item.addProperty("resource", newValue);
            });
        }
        if(filename.equals("SLS JVM监控大盘.json")){
            JsonObject parent = je.getAsJsonObject()
                .get("templating").getAsJsonObject()
                .get("list").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("current").getAsJsonObject();

            String newValue = parent.get("text").getAsString().replace(ruoyi_STRING, MY_PROJECT_NAME);
            parent.addProperty("text", newValue);
            newValue = parent.get("value").getAsString().replace(ruoyi_STRING, MY_PROJECT_NAME);
            parent.addProperty("value", newValue);
        }
        if(filename.equals())

        writeFile(file, je);
    }


    @Override
    public JsonElement parse(File file) {
        logger.info("解析JSON文件:"+file.getAbsolutePath());
        try(FileReader fileReader = new FileReader(file)){
            return gson.fromJson(fileReader, JsonElement.class);
        }catch (IOException e){
            logger.log(Level.SEVERE, "解析JSON文件失败: " + file.getAbsolutePath(), e);
        }
        return null;
    }

    public void writeFile(File file, JsonElement je) {
        try(JsonWriter jw = gson.newJsonWriter(new FileWriter(file));){
            gson.toJson(je, jw);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入JSON文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
