package com.akkkka;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author: akkkka114514
 * @create: 2025-07-28 13:39
 * @description:
 */
public abstract class XmlRenameStrategy implements RenameStrategy{
    protected static final Logger logger = Logger.getLogger(XmlRenameStrategy.class.getName());
    static {
        logger.setLevel(Constants.LOG_LEVEL);
    }
    protected Document parseXml(File file) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            document = saxReader.read(fis);
        } catch (Exception e) {
            logger.info("xml文件:" + file.getAbsolutePath() + "解析失败");
        }
        return document;
    }
    protected void saveXml(File file, Document document) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            XMLWriter writer = new XMLWriter(fos);
            writer.write(document);
            writer.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入pom.xml文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
