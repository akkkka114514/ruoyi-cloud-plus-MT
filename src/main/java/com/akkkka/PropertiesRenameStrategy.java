package com.akkkka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-30 16:20
 * @description:
 */
public class PropertiesRenameStrategy implements RenameStrategy{

    private static final Logger logger;
    static {
        logger = Logger.getLogger(PropertiesRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".properties");
    }

    @Override
    public void rename(File file) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        }catch (IOException e){
            logger.log(Level.SEVERE, "加载properties配置文件失败");
        }
        String springAppName = props.getProperty("spring.application.name");
        props.setProperty(
                "spring.application.name", springAppName.replace(ruoyi_STRING, MY_PROJECT_NAME));

        props.setProperty("spring.boot.admin.client.username", "admin");

        String nacosCoreAuthServerIdentityKey =props.getProperty("nacos.core.auth.server.identity.key");

        props.setProperty("nacos.core.auth.server.identity.key", nacosCoreAuthServerIdentityKey.replace(ruoyi_STRING, MY_PROJECT_NAME));

        String nacosCoreAuthServerIdentityValue =props.getProperty("nacos.core.auth.server.identity.value");

        props.setProperty("nacos.core.auth.server.identity.value", nacosCoreAuthServerIdentityValue.replace(ruoyi_STRING, MY_PROJECT_NAME));
    }
}
