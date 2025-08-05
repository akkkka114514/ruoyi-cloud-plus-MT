package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_GROUP_ID;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-03 12:15
 * @description:
 */
public class ApplicationCommonYmlRenameStrategy extends YamlRenameStrategy {
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
        return file.getName().equals("application-common.yml");
    }

    @Override
    public void rename(File file) {
        logger.info("Rename application-common.yml:" + file.getAbsolutePath());
        JsonNode rootNode = parse(file);
        renameValue(
                rootNode, "spring.cloud.nacos.discovery.metadata.username", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "spring.cloud.sentinel.transport.server-name", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "spring.cloud.bus.base-packages", RUOYI_GROUP_ID, MY_GROUP_ID);
        renameValue(
                rootNode, "spring.rabbitmq.username", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "spring.rabbitmq.password", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "spring.data.redis.password", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "springdoc.info.title", RUOYI_PROJECT_NAME, MY_PROJECT_NAME);
        renameValue(
                rootNode, "seata.registry.nacos.application",ruoyi_STRING, MY_PROJECT_NAME);
        deleteNode(rootNode, "springdoc.info.contact");

        writeFile(file, rootNode);
        logger.info("Rename application-common.yml done:" + file.getAbsolutePath());
    }
}
