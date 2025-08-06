package com.akkkka.strategy.yml;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;

import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-06 12:10
 * @description:
 */
public class SnailjobServerYmlRenameStrategy extends YamlRenameStrategy{
    @Override
    public boolean supports(File file) {
        return file.getName().equals(MY_PROJECT_NAME+"-snailjob-server.yml");
    }
    @Override
    public void rename(File file) {
        JsonNode rootNode = parse(file);
        renameValue(rootNode, "spring.cloud.nacos.discovery.metadata.username", ruoyi_STRING, "admin");
        writeFile(file, rootNode);
    }
}
