package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-05 19:14
 * @description:重命名xxx-monitor.yml中的内容
 */
public class MonitorYmlRenameStrategy extends YamlRenameStrategy{
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
        return file.getName().equals(MY_PROJECT_NAME+"-monitor.yml");
    }
    @Override
    public void rename(File file) {
        logger.info("Rename"+MY_PROJECT_NAME+"-monitor.yml:"+file.getAbsolutePath());
        JsonNode rootNode = parse(file);
        renameValue(
                rootNode,"spring.security.user.name",ruoyi_STRING,"admin");
        renameValue(
                rootNode,"spring.boot.admin.ui.title",RUOYI_PROJECT_NAME,MY_PROJECT_NAME);
        renameValue(
                rootNode, "spring.boot.admin.discovery.ignored-services", ruoyi_STRING, MY_PROJECT_NAME);
    }
}
