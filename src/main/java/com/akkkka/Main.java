package com.akkkka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.akkkka.RenameConfig.*;

/**
* @author: akkkka114514
* @date: 13:02:40 2025-07-24
*/
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static final String XPATH_NAMESPACE_PREFIX = "//xmlns:";
    public static final String POM_XML_FILE_STRING = "pom.xml";
    public static final String APP_FILENAME_SUFFIX = "Application.java";
    public static final String RUOYI_TOP_DOMAIN = "org";
    public static final String RUOYI_COMPANY_NAME = "Dromara";
    public static final String RUOYI_PROJECT_NAME = "RuoYi-Cloud-Plus";
    public static final String RUOYI_GROUP_ID = "org.dromara";
    public static final String RuoYi_STRING = "RuoYi";
    public static final String ruoyi_STRING= "ruoyi";
    public static final Boolean DO_CREATE_DEST_DIR = true;
    public static final String TEMP_MARK = "TEMP_MARK";

    public static String rootName;
    public static Map<String, String> namespace = new HashMap<>();

    static {
        namespace.put("xmlns", "http://maven.apache.org/POM/4.0.0");
        namespace.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespace.put("xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
    }


    public static void main(String[] args) throws IOException {
        if(!destDir.endsWith("\\")){
            destDir = destDir+"\\";
        }
        if(new File(destDir+"\\"+MY_PROJECT_NAME).exists()){
            logger.log(Level.SEVERE, "目标目录已存在");
            return;
        }
        String rootName = unzip(ZIP_PATH, destDir);
        logger.info("解压完成:"+rootName);
        File file = new File(destDir+rootName);
        fileBatchRename(file);
        logger.info("重命名完成");
    }
    // 解压 ZIP 文件到目标目录
    public static String unzip(String zipPath, String destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                File outFile = new File(destDir, entryName);
                if (entry.isDirectory() && entryName.split("/").length == 1) {
                    rootName = entryName.replace("/", "");
                }
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
        return rootName;
    }
    public static void fileBatchRename(File rootDir) throws IOException {
        Files.walk(rootDir.toPath())
            .sorted(
                Comparator
                    .comparingInt(path -> path.toString().split("\\\\").length).reversed())
            .forEach(path -> {
                if(path.endsWith(POM_XML_FILE_STRING)){
                    renameInPom(path.toFile());
                }else if(path.toString().contains(APP_FILENAME_SUFFIX)){
                    renameInJava(path.toFile());
                }else if(path.toString().contains("yml")){
                    renameInYml(path.toFile());
                }
                renameDirAndFileName(path.toFile());
            });
    }

    public static void renameDirAndFileName(File file) {
        assert file.getParentFile().exists();
        //logger.info("重命名:"+file.getAbsolutePath());
        String filename = file.getName(), originName = file.getName();
        if(file.getName().contains(RUOYI_PROJECT_NAME)) {
            filename = MY_PROJECT_NAME;
        } else if (file.getName().contains(APP_FILENAME_SUFFIX)) {
            filename = file.getName().replace(RuoYi_STRING, MY_APP_NAME);
        }else{
            filename = filename.replace(ruoyi_STRING, MY_PROJECT_NAME)
                .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME)
                .replace(RUOYI_TOP_DOMAIN,MY_TOP_DOMAIN);

        }
        File target = new File(file.getParentFile(), filename);
        if(!filename.equals(originName)) {
            try {
                if (file.isDirectory()) {
                    FileUtils.moveDirectory(file,target);
                } else {
                    FileUtils.moveFile(file, new File(file.getParentFile(), filename));
                }
                //logger.info("重命名文件夹后: " +  file.getParentFile()+"\\"+filename);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "重命名失败: " + file.getAbsolutePath()+ Arrays.toString(e.getStackTrace()));
            }
        }
    }
    //修改pom.xml文件里的字段
    public static void renameInPom(File file){
        Document document = parseXml(file);
        assert document != null;
        renameGroupIdInPom(document);
        renameArtifactIdInPom(document);
        renameModuleInPom(document);
        renameRootNameInPom(document);
        renameDescriptionInPom(document);

        //logger.info("重命名:"+file.getAbsolutePath()+":"+file.getName());
        // 写回文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XMLWriter writer = new XMLWriter(fos);
            writer.write(document);
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入文件失败: " + file.getAbsolutePath(), e);
            logger.log(Level.SEVERE,"exception", e.getStackTrace());
        }
    }

    //修改groupId
    public static void renameGroupIdInPom(Document document){
        //修改groupId
        getNodes(XPATH_NAMESPACE_PREFIX+"groupId", document)
                .stream()
                .filter(node -> node.getText().contains(RUOYI_GROUP_ID))
                .forEach(node -> node.setText(MY_GROUP_ID));
    }

    //修改artifactId
    public static void renameArtifactIdInPom(Document document){
        //修改artifactId
        getNodes(XPATH_NAMESPACE_PREFIX+"artifactId", document)
                .stream()
                .filter(node -> node.getText().contains(ruoyi_STRING))
                .forEach(node -> node.setText(node.getText().replace("ruoyi-cloud-plus", MY_PROJECT_NAME).replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }

    //修改module
    public static void renameModuleInPom(Document document){
        getNodes(XPATH_NAMESPACE_PREFIX+"module", document)
                .forEach(node -> node
                        .setText(node.getText().replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }
    //修改根节点里的项目name
    public static void renameRootNameInPom(Document document){
        getNodes(XPATH_NAMESPACE_PREFIX+"name", document)
                .stream()
                .filter(node -> node.getText().contains(RuoYi_STRING))
                .forEach(node -> node.setText(node.getText().replace(RUOYI_PROJECT_NAME, MY_PROJECT_NAME).replace(RuoYi_STRING, MY_PROJECT_NAME)));
    }
    //修改项目description
    public static void renameDescriptionInPom(Document document){
        getNodes(XPATH_NAMESPACE_PREFIX+"description", document)
                //描述中的标志性字段
                .forEach(node -> node.setText(node.getText().replace("Dromara RuoYi-Cloud-Plus", MY_PROJECT_NAME).replace(ruoyi_STRING, MY_PROJECT_NAME)));
    }

    //根据传入node name获取节点，封装设置namespace的步骤
    public static List<Node> getNodes(String nodeName, Document document){
        XPath xPath= document.createXPath(nodeName);
        xPath.setNamespaceURIs(namespace);
        return xPath.selectNodes(document);
    }
    //修改java文件里的字段
    public static void renameInJava(File file){
        try {
            // 解析 Java 文件
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file));
            renameAppClassNameInJava(cu);
            renameImportExprInJava(cu);
            renameObjCreationInJava(cu);
            renamePackageExprInJava(cu);
            // 将修改后的代码写回文件
            Files.write(Paths.get(file.getAbsolutePath()), cu.toString().getBytes());

            //logger.info("成功修改Java文件: " + file.getAbsolutePath());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "解析或修改Java文件失败: " + file.getAbsolutePath(), e);
        }
    }


    // 解析XML文件
    public static Document parseXml(File file){
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try(FileInputStream fis = new FileInputStream(file)){
            document=saxReader.read(fis);
        }catch (Exception e){
            logger.info("文件:"+file.getAbsolutePath()+"解析失败");
        }
        return document;
    }

    public static void renameImportExprInJava(CompilationUnit cu){
        cu.getImports().forEach(importDeclaration -> {
            String oldName = importDeclaration.getNameAsString();
            importDeclaration.setName(oldName.replace(RUOYI_GROUP_ID, MY_GROUP_ID));
        });
    }

    public static void renamePackageExprInJava(CompilationUnit cu){
        // 修改包名
        cu.getPackageDeclaration().ifPresent(pd -> {
            String packageName = pd.getNameAsString();
            packageName = packageName.replace(RUOYI_GROUP_ID, MY_GROUP_ID)
                    .replace(ruoyi_STRING, MY_PROJECT_NAME)
                    .replace(RUOYI_TOP_DOMAIN, MY_TOP_DOMAIN)
                    .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME);
            pd.setName(packageName);
        });
    }

    public static void renameAppClassNameInJava(CompilationUnit cu){
        // 修改类名（如果类名包含 ruoyi 或 RuoYi）
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cid -> {
            String className = cid.getNameAsString();
            if (className.contains(RuoYi_STRING)) {
                className = className.replace(RuoYi_STRING, MY_APP_NAME);
                cid.setName(className);
            }
        });
    }

    //修改语句SpringApplication application = new SpringApplication(RuoYiDemoApplication.class);
    public static void renameObjCreationInJava(CompilationUnit cu){
        cu.findAll(ObjectCreationExpr.class).forEach(oc -> {
            if (oc.getType().getNameAsString().equals("SpringApplication")) {
                oc.getArguments().forEach(arg -> {
                    if (arg instanceof ClassExpr) {
                        ClassExpr classExpr = (ClassExpr) arg;
                        String className = classExpr.getType().asString();
                        if (className.contains(RuoYi_STRING)) {
                            String newClassName = className.replace(RuoYi_STRING, MY_APP_NAME);
                            // 重新构造 ClassExpr
                            try {
                                classExpr.setType(StaticJavaParser.parseType(newClassName));
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "无法解析新的类名: " + newClassName, e);
                            }
                        }
                    }
                });
            }
        });
    }
    //由于yaml文件中有nacos特有的占位符，yaml框架会解析失败，所以只能手动修改
    public static void renameInYml(File file) {
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLMapper yamlMapper = new YAMLMapper(yamlFactory);
            // 关键配置：最小化引号使用
            yamlMapper.enable(com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature.MINIMIZE_QUOTES);

            // 读取YAML
            JsonNode rootNode = yamlMapper.readTree(preprocessYaml(file));

            if (!rootNode.isMissingNode() && rootNode.isObject()) {
                // 处理节点
                JsonNode toEditNode = rootNode
                        .findPath("spring")
                        .findPath("application");
                if(!toEditNode.isMissingNode()){
                    ((ObjectNode)toEditNode).set("name", new TextNode(MY_PROJECT_NAME));
                }
                // 写回文件
                yamlMapper.writeValue(file, rootNode);
                logger.info("成功处理YAML文件: " + file.getAbsolutePath());
            }

            postProcessYaml(file);
        } catch (Exception e) {
            logger.log(Level.WARNING, "YAML处理失败: " + file.getAbsolutePath(), e);
        }
    }
    //预处理yaml文件，把@字符换为临时字符，避免yaml框架解析失败
    private static String preprocessYaml(File file){
        String content = null;
        try {
            content = FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "读取文件失败: " + file.getAbsolutePath(), e);
        }

        assert content != null;
        return content.replaceAll("@", TEMP_MARK);
    }

    //把预处理修改的@字符换回来
    public static void postProcessYaml(File file){
        try {
            String content = FileUtils.readFileToString(file, "UTF-8");
            content = content.replaceAll(TEMP_MARK,"@");
            FileUtils.write(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}