package com.akkkka.strategy.xml;

import com.akkkka.Constants;
import org.dom4j.Document;
import org.dom4j.Node;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.MY_GROUP_ID;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 20:58
 * @description:
 */
public class PomXmlRenameStrategy extends XmlRenameStrategy {
    private static final Logger logger;

    private static final Map<String, String> namespace;
    //xml namespace,不设置的话查询xml节点就会为null
    static {
        logger = Logger.getLogger(PomXmlRenameStrategy.class.getName());
        logger.setLevel(Constants.LOG_LEVEL);

        namespace = new HashMap<>();
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
        Document document = parse(file);
        setNamespace(namespace);

        if (document != null) {
            renameGroupIdInPom(document);
            renameArtifactIdInPom(document);
            renameModuleInPom(document);
            renameRootNameInPom(document);
            renameDescriptionInPom(document);
            renameCommentInPom(document);
            deleteUrlNode(document);

            saveXml(file, document);
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
                .forEach(node -> {
                    String text = node.getText();
                        if(text.equals(RUOYI_PROJECT_NAME)){
                            node.setText(MY_PROJECT_NAME);
                        }else if(text.equals(RUOYI_PROJECT_NAME.toLowerCase(Locale.ROOT))){
                            node.setText(MY_PROJECT_NAME);
                        }else {
                            node.setText(
                                text.replace(RUOYI_PROJECT_NAME, MY_PROJECT_NAME)
                                    .replace(ruoyi_STRING, MY_PROJECT_NAME));
                        }
                });
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

    private void deleteUrlNode(Document document){
        getNodes(XPATH_NAMESPACE_PREFIX+"url", document)
                .forEach(Node::detach);
    }

    private void renameCommentInPom(Document document){
        getNodes("//comment()", document).forEach(
                node -> {
                    String text = node.getText();
                    if(text.contains(RuoYi_STRING)){
                        node.setText(text.replace(RuoYi_STRING,MY_PROJECT_NAME));
                    }
                    if(text.contains(ruoyi_STRING)){
                        node.setText(text.replace(ruoyi_STRING,MY_PROJECT_NAME));
                    }
                });
    }

}
