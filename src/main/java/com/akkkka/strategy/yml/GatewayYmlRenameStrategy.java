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
 * @create: 2025-08-03 13:59
 * @description:
 */
public class GatewayYmlRenameStrategy extends YamlRenameStrategy{
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
        return file.getName().equals(MY_PROJECT_NAME+"-gateway.yml");
    }
    @Override
    public void rename(File file) {
        JsonNode rootNode = parse(file);
        getNavigatedArrayNode(rootNode, "spring.cloud.gateway.routes").forEach(node ->{
                    renameValue(node, "id", ruoyi_STRING, MY_PROJECT_NAME);
                    renameValue(node, "uri", ruoyi_STRING, MY_PROJECT_NAME);
                }
        );
        writeFile(file, rootNode);
    }
}
