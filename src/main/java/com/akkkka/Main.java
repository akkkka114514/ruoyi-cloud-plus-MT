package com.akkkka;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
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
                }
                renameDirAndFileName(path.toFile());
            });
    }

    public static void renameDirAndFileName(File file) {
        assert file.getParentFile().exists();
        logger.info("重命名:"+file.getAbsolutePath());
        String filename = getString(file);

        if(file.renameTo(new File(file.getParentFile(),filename))){
                logger.info("重命名文件夹后: " +  file.getParentFile()+"\\"+filename);
            }else{
                logger.log(Level.SEVERE, "重命名失败: " + file.getAbsolutePath());
            }
        }

    private static String getString(File file) {
        String filename = file.getName();
        if(file.getName().contains(RUOYI_PROJECT_NAME)) {
            filename = MY_PROJECT_NAME;
        } else if (file.getName().contains(APP_FILENAME_SUFFIX)) {
            filename = file.getName().replace(RuoYi_STRING, MY_APP_NAME);
        }else{
            filename = filename.replace(ruoyi_STRING, MY_APP_NAME)
                .replace(RuoYi_STRING, MY_APP_NAME)
                .replace(RUOYI_TOP_DOMAIN,MY_TOP_DOMAIN)
                .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME);
            }
        return filename;
    }

    //修改pom.xml文件里的字段
    public static void renameInPom(File file){
        Document document = parseXml(file);
        assert document != null;


        //修改artifactId
        getNodes(XPATH_NAMESPACE_PREFIX+"artifactId", document)
                .stream()
                .filter(node -> node.getText().contains(ruoyi_STRING))
                .forEach(node -> node.setText(node.getText().replace("ruoyi", MY_PROJECT_NAME)));
        //修改module
        getNodes(XPATH_NAMESPACE_PREFIX+"module", document)
                .forEach(node -> node
                        .setText(node.getText().replace(ruoyi_STRING, MY_PROJECT_NAME)));
        //修改项目name
        getNodes(XPATH_NAMESPACE_PREFIX+"name", document)
                .stream()
                .filter(node -> node.getText().contains(RuoYi_STRING))
                .forEach(node -> node.setText(node.getText().replace(RuoYi_STRING, MY_PROJECT_NAME)));
        //修改项目description
        getNodes(XPATH_NAMESPACE_PREFIX+"description", document)
                //描述中的标志性字段
                .forEach(node -> node.setText(node.getText().replace("Dromara RuoYi-Cloud-Plus", MY_PROJECT_NAME)));
        logger.info("重命名:"+file.getAbsolutePath()+":"+file.getName());
        // 写回文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XMLWriter writer = new XMLWriter(fos);
            writer.write(document);
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入文件失败: " + file.getAbsolutePath(), e);
        }
    }

    public static void renameGroupIdInPom(Document document){
        //修改groupId
        getNodes(XPATH_NAMESPACE_PREFIX+"groupId", document)
                .stream()
                .filter(node -> node.getText().contains(RUOYI_GROUP_ID))
                .forEach(node -> node.setText(MY_GROUP_ID));
    }
    public static List<Node> getNodes(String str, Document document){
        XPath xPath= document.createXPath(str);
        xPath.setNamespaceURIs(namespace);
        return xPath.selectNodes(document);
    }
    //修改java文件里的字段
    public static void renameInJava(File file){
        try {
            // 解析 Java 文件
            CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(file));

            // 修改包名
            cu.getPackageDeclaration().ifPresent(pd -> {
                String packageName = pd.getNameAsString();
                packageName = packageName.replace(RUOYI_GROUP_ID, MY_GROUP_ID)
                        .replace(ruoyi_STRING, MY_PROJECT_NAME)
                        .replace(RUOYI_TOP_DOMAIN, MY_TOP_DOMAIN)
                        .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME);
                pd.setName(packageName);
            });

            // 修改类名（如果类名包含 ruoyi 或 RuoYi）
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cid -> {
                String className = cid.getNameAsString();
                if (className.contains(RuoYi_STRING)) {
                    className = className.replace(RuoYi_STRING, MY_APP_NAME);
                    cid.setName(className);
                }
            });

            cu.findAll(ObjectCreationExpr.class).forEach(oc -> {
                if (oc.getType().getNameAsString().equals("SpringApplication")) {
                    oc.getArguments().forEach(arg -> {
                        if (arg instanceof com.github.javaparser.ast.expr.ClassExpr) {
                            com.github.javaparser.ast.expr.ClassExpr classExpr = (ClassExpr) arg;
                            String className = classExpr.getType().asString();
                            if (className.contains(RuoYi_STRING)) {
                                String newClassName = className.replace("RuoYi", MY_APP_NAME);
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
            // 将修改后的代码写回文件
            Files.write(Paths.get(file.getAbsolutePath()), cu.toString().getBytes());

            logger.info("成功修改Java文件: " + file.getAbsolutePath());

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

}