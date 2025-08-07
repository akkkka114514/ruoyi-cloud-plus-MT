package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
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
public class PropertiesRenameStrategy implements RenameStrategy {

    private static final Logger logger;
    static {
        logger = Logger.getLogger(PropertiesRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals("application.properties") && file.getAbsolutePath().contains("nacos")||
                file.getName().equals("seata-server.properties");
    }

    @Override
    public void rename(File file) {
        Properties props = new Properties();
        String filename = file.getName();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }catch (IOException e){
            logger.log(Level.SEVERE, "加载properties配置文件失败");
        }

        if(filename.equals("application.properties") && file.getAbsolutePath().contains("nacos")) {
            String springAppName = props.get("spring.application.name").toString();
            props.setProperty(
                    "spring.application.name", springAppName.replace(ruoyi_STRING, MY_PROJECT_NAME));

            props.setProperty("spring.boot.admin.client.username", "admin");

            String nacosCoreAuthServerIdentityKey = props.getProperty("nacos.core.auth.server.identity.key");

            props.setProperty("nacos.core.auth.server.identity.key", nacosCoreAuthServerIdentityKey.replace(ruoyi_STRING, MY_PROJECT_NAME));

            String nacosCoreAuthServerIdentityValue = props.getProperty("nacos.core.auth.server.identity.value");

            props.setProperty("nacos.core.auth.server.identity.value", nacosCoreAuthServerIdentityValue.replace(ruoyi_STRING, MY_PROJECT_NAME));
        }
        if(filename.equals("seata-server.properties")){
            List<String> list = List.of("auth","system","resource","workflow");
            list.forEach(moduleName -> {
                String key = "service.vgroupMapping.ruoyi-"+moduleName+"-group";
                String newKey = "service.vgroupMapping."+MY_PROJECT_NAME+"-"+moduleName+"-group";
                String value = props.get(key).toString();
                props.remove( key);
                props.put(newKey, value);
            });
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, null);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "保存properties配置文件失败");
        }
    }
}
