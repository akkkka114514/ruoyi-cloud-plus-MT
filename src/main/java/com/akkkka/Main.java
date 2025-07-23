package com.akkkka;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static final String groupId = "com.example";
    public static final String topDomain = "com";
    public static final String companyName = "example";
    public static final String projectName = "my-example-project";
    public static final String applicationName = "MyExampleProject";
    public static final String zipPath = "C:\\Users\\Admin\\Downloads\\RuoYi-Cloud-Plus.zip";
    public static String destDir = "D:\\ideaWorkspace";
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
        if(new File(destDir+"\\"+projectName).exists()){
            logger.log(Level.SEVERE, "目标目录已存在:"+destDir+projectName);
            return;
        }
        String rootName = unzip(zipPath, destDir);
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
                if(path.endsWith("pom.xml")){
                    renameInPom(path.toFile());
                }else{
                    renameOther(path.toFile());
                }
            });

    }

    public static void renameOther(File file) {
        assert file.getParentFile().exists();
        logger.info("重命名:"+file.getAbsolutePath());
        String filename = file.getName();
        if(file.getName().contains("RuoYi-Cloud-Plus")) {
            filename = projectName;
        } else if (file.getName().contains("Application.java")) {
            filename = applicationName+"Application.java";
        }else{
                filename = filename.replace("ruoyi", projectName)
                    .replace("RuoYi", projectName)
                    .replace("org", topDomain)
                    .replace("dromara", companyName);
            }

            if(file.renameTo(new File(file.getParentFile(),filename))){
                logger.info("重命名文件夹后: " +  file.getParentFile()+"\\"+filename);
            }else{
                logger.log(Level.SEVERE, "重命名失败: " + file.getAbsolutePath());
            }
        }

    public static void renameInPom(File file){
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try(FileInputStream fis = new FileInputStream(file)){
            document=saxReader.read(fis);
        }catch (Exception e){
            logger.info("文件:"+file.getAbsolutePath()+"解析失败");
        }
        assert document != null;

        //修改groupId
        getNodes("//xmlns:groupId", document)
                .stream()
                .filter(node -> node.getText().contains("org.dromara"))
                .forEach(node -> node.setText(groupId));
        //修改artifactId
        getNodes("//xmlns:artifactId", document)
                .stream()
                .filter(node -> node.getText().contains("ruoyi"))
                .forEach(node -> node.setText(node.getText().replace("ruoyi", projectName)));
        //修改module
        getNodes("//xmlns:module", document)
                .forEach(node -> node
                        .setText(node.getText().replace("ruoyi", projectName)));
        //修改项目name
        getNodes("//xmlns:name", document)
                .stream()
                .filter(node -> node.getText().contains("RuoYi"))
                .forEach(node -> node.setText(node.getText().replace("RuoYi", projectName)));
        //修改项目description
        getNodes("//xmlns:description", document)
                .forEach(node -> node.setText(node.getText().replace("Dromara RuoYi-Cloud-Plus", projectName)));
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

    public static List<Node> getNodes(String str, Document document){
        XPath xPath= document.createXPath(str);
        xPath.setNamespaceURIs(namespace);
        return xPath.selectNodes(document);
    }
}