package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-06 12:10
 * @description:
 */
public class SnailjobServerYmlRenameStrategy extends YamlRenameStrategy{
    private static final Logger logger;
    static{
        logger = Logger.getLogger(YamlRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals(MY_PROJECT_NAME+"-snailjob-server.yml");
    }
    @Override
    public void rename(File file) {
        logger.info("正在处理snailjob-server.yml文件: " + file.getAbsolutePath());
        JsonNode rootNode = parse(file);
        renameValue(rootNode, "spring.cloud.nacos.discovery.metadata.username", ruoyi_STRING, "admin");
        writeFile(file, rootNode);
        logger.info("处理完成snailjob-server.yml文件: " + file.getAbsolutePath());
    }
}
