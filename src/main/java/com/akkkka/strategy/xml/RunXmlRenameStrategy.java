package com.akkkka.strategy.xml;

import com.akkkka.Constants;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-29 15:17
 * @description:修改.run.xml文件的内容
 */
public class RunXmlRenameStrategy extends XmlRenameStrategy {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(RunXmlRenameStrategy.class.getName());
        logger.setLevel(Constants.LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".run.xml");
    }

    @Override
    public void rename(File file) {
        Document document = parse(file);
        if (document != null) {
            renameConfigurationNode(document);
            renameOptionNode(document);

            saveXml(file, document);
            logger.info("run.xml文件:" + file.getAbsolutePath() + "重命名成功");
        }
    }
    public void renameConfigurationNode(Document document){
        getNodes("//configuration", document).forEach(node -> {
            Element element = (Element) node;
            String configurationName = element.attribute("name").getValue();
            String newConfigurationName = configurationName.replace(ruoyi_STRING, MY_PROJECT_NAME);
            element.addAttribute("name", newConfigurationName);
        });
    }
    public void renameOptionNode(Document document){
        getNodes("//option", document).forEach(node -> {
            Element element = (Element) node;
            String value = element.attribute("value").getValue();
            if(value.contains(ruoyi_STRING)){
                String newValue = value.replace(ruoyi_STRING, MY_PROJECT_NAME);
                element.addAttribute("value", newValue);
            }
        });
    }
}
