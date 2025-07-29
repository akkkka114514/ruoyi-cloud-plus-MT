package com.akkkka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.TEMP_MARK;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:07
 * @description: yaml文件内容处理
 */
public class YamlRenameStrategy implements RenameStrategy{
    private static final Logger logger;

    private static final YAMLMapper yamlMapper;
    static{
        logger = Logger.getLogger(YamlRenameStrategy.class.getName());
        logger.setLevel(Constants.LOG_LEVEL);

        YAMLFactory yamlFactory = new YAMLFactory();
        yamlMapper = new YAMLMapper(yamlFactory);
        yamlMapper.enable(com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.MINIMIZE_QUOTES);
    }

    @Override
    public boolean supports(File file) {
        return file.toString().contains("yml");
    }

    @Override
    public void rename(File file) {
        try {
            JsonNode rootNode = yamlMapper.readTree(preprocessYaml(file));

            if (!rootNode.isMissingNode() && rootNode.isObject()) {
                JsonNode toEditNode = rootNode
                        .findPath("spring")
                        .findPath("application");
                if (!toEditNode.isMissingNode()) {
                    ((ObjectNode) toEditNode).set("name", new TextNode(MY_PROJECT_NAME));
                }
                yamlMapper.writeValue(file, rootNode);
            }

            postProcessYaml(file);
        } catch (Exception e) {
            logger.log(Level.WARNING, "YAML处理失败: " + file.getAbsolutePath(), e);
        }
    }

    //由于框架解析@字符会出错，所以进行预处理，将所有@@换成临时的占位符
    private String preprocessYaml(File file) {
        logger.log(Level.INFO, "预处理yaml文件: " + file.getAbsolutePath());
        String content = null;
        try {
            content = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "读取文件失败: " + file.getAbsolutePath(), e);
        }
        assert content != null;
        return content.replaceAll("@", TEMP_MARK);
    }

    // 将临时的占位符再换回@字符
    private void postProcessYaml(File file) {
        logger.info("yaml文件后续处理");
        try {
            String content = FileUtils.readFileToString(file, "UTF-8");
            content = content.replaceAll(TEMP_MARK, "@");
            FileUtils.write(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
