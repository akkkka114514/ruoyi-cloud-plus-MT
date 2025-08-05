package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-03 12:17
 * @description:
 */
public class DockerComposeYmlRenameStrategy extends YamlRenameStrategy {
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
        return file.getName().endsWith("docker-compose.yml");
    }

    @Override
    public void rename(File file) {

        logger.info("Rename docker-compose.yml:"+file.getAbsolutePath());
        JsonNode rootNode = parse(file);
        renameValue(
                rootNode, "services.mysql.environment.MYSQL_ROOT_PASSWORD", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "services.nacos.image", ruoyi_STRING, MY_PROJECT_NAME);

        renameValue(
                rootNode, "services.minio.environment.MINIO_ROOT_USER", ruoyi_STRING, "admin");
        renameValue(
                rootNode, "services.minio.environment.MINIO_ROOT_PASSWORD", ruoyi_STRING, "admin");

        renameValue(
                rootNode, "services.seata-server.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameTextNodeArray(
                rootNode, "services.seata-server.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameValue(
                rootNode, "services.sentinel.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameTextNodeArray(
                rootNode, "services.sentinel.volumes", ruoyi_STRING, MY_PROJECT_NAME);
        List<String> list = List.of(
                "-monitor","-snailjob-server","-gateway","-auth",
                "-system","-gen","-job","-resource","-workflow");

        list.forEach(moduleName -> {
            renameKey(
                    rootNode, "services.ruoyi"+moduleName, ruoyi_STRING, MY_PROJECT_NAME);
            renameValue(
                    rootNode, "services."+MY_PROJECT_NAME+moduleName+".image", ruoyi_STRING, MY_PROJECT_NAME);
            renameTextNodeArray(
                    rootNode, "services."+MY_PROJECT_NAME+moduleName +".volumes", ruoyi_STRING, MY_PROJECT_NAME);
            renameValue(
                    rootNode,"services."+MY_PROJECT_NAME+moduleName+".container_name",ruoyi_STRING, MY_PROJECT_NAME);
        });


        renameValue(
                rootNode,"services.rabbitmq.environment.RABBITMQ_DEFAULT_USER",ruoyi_STRING,"admin");
        renameValue(
                rootNode,"services.rabbitmq.environment.RABBITMQ_DEFAULT_PASS",ruoyi_STRING,"admin");


        renameValue(
                rootNode, "services.kafka-manager.environment.KAFKA_MANAGER_USERNAME",ruoyi_STRING,"admin");
        renameValue(
                rootNode, "services.kafka-manager.environment.KAFKA_MANAGER_PASSWORD",ruoyi_STRING,"admin");
        writeFile(file, rootNode);

        logger.info("docker-compose.yml处理完成:"+file.getAbsolutePath());
    }
}
