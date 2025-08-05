package com.akkkka.strategy.yml;

import com.akkkka.Parsable;
import com.akkkka.RenameStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:07
 * @description: yaml文件内容处理
 */
public abstract class YamlRenameStrategy implements RenameStrategy, Parsable<JsonNode> {
    private static final Logger logger;

    private static final YAMLMapper yamlMapper;
    static{
        logger = Logger.getLogger(YamlRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);

        YAMLFactory yamlFactory = new YAMLFactory();
        yamlMapper = new YAMLMapper(yamlFactory);
        yamlMapper.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
    }

    @Override
    public boolean supports(File file) {
        return file.toString().contains("yml");
    }




    protected void renameValue(JsonNode rootNode, String path, String toReplace, String replaceWith){
        String[] pathArray = path.split("\\.");
        String lastKey = pathArray[pathArray.length-1];
        ObjectNode parentNode = getNavigatedNode(rootNode,path);
        if(parentNode.isMissingNode()){
            return;
        }
        String OldValue = parentNode.findValue(lastKey).asText();
        parentNode.set(
                lastKey, new TextNode(OldValue.replaceAll(toReplace, replaceWith)));
    }

    protected void renameKey(JsonNode rootNode, String path, String toReplace, String replaceWith){
        String[] pathArray = path.split("\\.");
        String lastKey = pathArray[pathArray.length-1];
        ObjectNode parentNode = getNavigatedNode(rootNode,path);
        if(parentNode.isMissingNode()){
            return;
        }
        JsonNode jsonNode = parentNode.findValue(lastKey);
        parentNode.remove(lastKey);
        parentNode.set(lastKey.replaceAll(toReplace, replaceWith), jsonNode);
    }
    protected void deleteNode(JsonNode rootNode, String path){
        String[] pathArray = path.split("\\.");
        String lastKey = pathArray[pathArray.length-1];
        ObjectNode parentNode = getNavigatedNode(rootNode,path);
        if(parentNode.isMissingNode()){
            return;
        }
        parentNode.remove(lastKey);
    }

    //经过find path后的arrayNode
    protected ArrayNode getNavigatedArrayNode(JsonNode rootNode, String path){
        String[] pathArray = path.split("\\.");
        String lastKey = pathArray[pathArray.length-1];
        ObjectNode parentNode = getNavigatedNode(rootNode,path);
        if(parentNode.isMissingNode()){
            return null;
        }
        return (ArrayNode)parentNode.findValue(lastKey);
    }
    protected void renameTextNodeArray(JsonNode rootNode, String path, String toReplace, String replaceWith){
        ArrayNode arrayNode = getNavigatedArrayNode(rootNode,path);
        if (arrayNode == null) {
            return;
        }

        // 创建新的 ArrayNode 来存储重命名后的文本节点
        ArrayNode newArrayNode = yamlMapper.createArrayNode();

        // 遍历所有 TextNode 并重命名
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode node = arrayNode.get(i);
            if (node.isTextual()) {
                TextNode textNode = (TextNode) node;
                String oldValue = textNode.asText();
                String newValue = oldValue.replaceAll(toReplace, replaceWith);
                newArrayNode.add(new TextNode(newValue));
            } else {
                // 非文本节点保持原样
                newArrayNode.add(node);
            }
        }

        // 获取父节点并替换整个数组
        String[] pathArray = path.split("\\.");
        String lastKey = pathArray[pathArray.length - 1];
        ObjectNode parentNode = getNavigatedNode(rootNode, path);
        if (!parentNode.isMissingNode()) {
            parentNode.set(lastKey, newArrayNode);
        }
    }

    //经过findpath后的倒数第二个节点
    protected ObjectNode getNavigatedNode(JsonNode rootNode,String path){
        assert rootNode.isObject() && !rootNode.isMissingNode();
        String[] pathArray = path.split("\\.");
        for(int i = 0; i < pathArray.length -1; i++){
            rootNode = rootNode.findPath(pathArray[i]);
        }
        return (ObjectNode) rootNode;
    }

    @Override
    public JsonNode parse(File file) {
        logger.info("Parse yml" + file.getAbsolutePath());
        try {
            return yamlMapper.readTree(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "YAML解析失败: " + file.getAbsolutePath(), e);
        }
        return null;
    }
    public void writeFile(File file, JsonNode rootNode) {
        try{
            yamlMapper.writeValue(file, rootNode);
        }catch (IOException e){
            logger.log(Level.SEVERE, "YAML写入失败: " + file.getAbsolutePath(), e);
        }
    }
}
