package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
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
        renameArrayValue(
                rootNode, "services.seata-server.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameValue(
                rootNode, "services.sentinel.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services.sentinel.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-monitor", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-monitor.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-monitor.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-snailjob-server", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-snailjob-server.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-snailjob-server.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-gateway", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-gateway.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-gateway.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-auth", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-auth.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-auth.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-system", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-system.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-system.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-gen", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-gen.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-gen.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-job", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-job.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-job.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-resource", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-resource.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-resource.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameKey(
                rootNode, "services.ruoyi-workflow", ruoyi_STRING, MY_PROJECT_NAME);
        renameValue(
                rootNode, "services."+MY_PROJECT_NAME+"-workflow.image", ruoyi_STRING, MY_PROJECT_NAME);
        renameArrayValue(
                rootNode, "services."+MY_PROJECT_NAME+"-workflow.volumes", ruoyi_STRING, MY_PROJECT_NAME);

        renameValue(
                rootNode,"services.rabbitmq.environment.RABBITMQ_DEFAULT_USER",ruoyi_STRING,"admin");
        renameValue(
                rootNode,"services.rabbitmq.environment.RABBITMQ_DEFAULT_PASS",ruoyi_STRING,"admin");

        writeFile(file, rootNode);

        logger.info("docker-compose.yml处理完成:"+file.getAbsolutePath());
    }
}
