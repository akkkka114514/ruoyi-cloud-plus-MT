package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-03 12:19
 * @description:
 */
public class PrometheusYmlRenameStrategy extends YamlRenameStrategy {    private static final Logger logger;

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
        return file.getName().endsWith("prometheus.yml");
    }
    @Override
    public void rename(File file) {
        logger.info("Rename prometheus.yml:"+file.getAbsolutePath());
        JsonNode rootNode = parse(file);
        Objects.requireNonNull(getNavigatedArrayNode(rootNode, "scrape_configs"))
                .forEach(node -> {
                    if(node.findValue("job_name").asText().equals("Nacos")){
                        renameValue(node, "basic_auth.username", ruoyi_STRING, "admin");
                    }
                    if(node.findValue("job_name").asText().equals(RUOYI_PROJECT_NAME)){
                        ((ObjectNode)node).set("job_name", new TextNode(MY_PROJECT_NAME));
                        renameValue(node, "basic_auth.username", ruoyi_STRING, "admin");
                        Objects.requireNonNull(getNavigatedArrayNode(node, "http_sd_configs")).forEach(
                                anotherNode -> renameValue(anotherNode, "basic_auth.username", ruoyi_STRING, "admin"));
                    }
                });
        writeFile(file, rootNode);
        logger.info("Rename prometheus.yml done:"+file.getAbsolutePath());
    }
}
