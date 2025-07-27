package com.akkkka;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.Main.*;
import static com.akkkka.RenameConfig.MY_GROUP_ID;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 20:58
 * @description:
 */
public class PomRenameStrategy implements RenameStrategy{
    private static final Logger logger = Logger.getLogger(PomRenameStrategy.class.getName());

    private static final Map<String, String> namespace = new HashMap<>();
    //xml namespace,不设置的话查询xml节点就会为null
    static {
        namespace.put("xmlns", "http://maven.apache.org/POM/4.0.0");
        namespace.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespace.put("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
    }

    @Override
    public boolean supports(File file) {
        return file.getName().equals("pom.xml");
    }

    @Override
    public void rename(File file) {
        Document document = parseXml(file);
        if (document != null) {
            renameGroupIdInPom(document);
            renameArtifactIdInPom(document);
            renameModuleInPom(document);
            renameRootNameInPom(document);
            renameDescriptionInPom(document);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                XMLWriter writer = new XMLWriter(fos);
                writer.write(document);
                writer.flush();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "写入pom.xml文件失败: " + file.getAbsolutePath(), e);
            }
            logger.info("pom.xml文件:" + file.getAbsolutePath() + "重命名成功");
        }
    }

    private void renameGroupIdInPom(Document document) {
        getNodes(XPATH_NAMESPACE_PREFIX+"groupId", document)
                .stream()
                .filter(node -> node.getText().contains(RUOYI_GROUP_ID))
                .forEach(node -> node.setText(MY_GROUP_ID));
    }

    private void renameArtifactIdInPom(Document document) {
        getNodes(XPATH_NAMESPACE_PREFIX+"artifactId", document)
                .stream()
                .filter(node -> node.getText().contains(ruoyi_STRING))
                .forEach(node -> node.setText(node.getText().replace(RUOYI_PROJECT_NAME, MY_PROJECT_NAME).replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }

    private void renameModuleInPom(Document document) {
        getNodes(XPATH_NAMESPACE_PREFIX+"module", document)
                .forEach(node -> node.setText(node.getText().replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }

    private void renameRootNameInPom(Document document) {
        getNodes(XPATH_NAMESPACE_PREFIX+"name", document)
                .stream()
                .filter(node -> node.getText().contains(RuoYi_STRING))
                .forEach(node -> node.setText(node.getText().replace(RUOYI_PROJECT_NAME, MY_PROJECT_NAME).replace(RuoYi_STRING, MY_PROJECT_NAME)));
    }

    private void renameDescriptionInPom(Document document) {
        getNodes(XPATH_NAMESPACE_PREFIX+"description", document)
                .forEach(node -> node.setText(node.getText().replace(RUOYI_DESC_IDENTITY, MY_PROJECT_NAME).replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }

    private List<Node> getNodes(String nodeName, Document document) {
        XPath xPath = document.createXPath(nodeName);
        xPath.setNamespaceURIs(namespace);
        return xPath.selectNodes(document);
    }

    private Document parseXml(File file) {
        org.dom4j.io.SAXReader saxReader = new org.dom4j.io.SAXReader();
        Document document = null;
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
            document = saxReader.read(fis);
        } catch (Exception e) {
            logger.info("xml文件:" + file.getAbsolutePath() + "解析失败");
        }
        return document;
    }
}
