package com.akkkka;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.RenameConfig.*;

/**
* @author: akkkka114514
* @date: 13:02:40 2025-07-24
*/
public class Main {
    private static final Logger logger;

    static {
        logger = Logger.getLogger(Main.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    public static String rootName;
    private static final RenameStrategyManager strategyManager = new RenameStrategyManager();

    public static void main(String[] args) throws IOException {
        Long startTime = System.currentTimeMillis();
        if(!destDir.endsWith("\\")){
            destDir = destDir+"\\";
        }
        if(new File(destDir+"\\"+MY_PROJECT_NAME).exists()){
            logger.log(Level.SEVERE, "目标目录已存在");
            return;
        }
        if(!new File(ZIP_PATH).exists()){
            logger.log(Level.SEVERE, "ZIP文件不存在");
            return;
        }
        logger.info("开始解压");
        String rootName = unzip(ZIP_PATH, destDir);
        logger.info("解压完成:"+rootName);
        File file = new File(destDir+rootName);
        logger.info("开始重命名文件");
        fileBatchRename(file);
        logger.info("所有文件重命名完成");
        Long endTime = System.currentTimeMillis();
        logger.info("耗时:"+(endTime-startTime)+"ms");
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
    // 修改 fileBatchRename 方法
    public static void fileBatchRename(File rootDir) throws IOException {
        Files.walk(rootDir.toPath())
            .sorted(Comparator.comparingInt(path -> path.toString().split("\\\\").length).reversed())
            .forEach(path -> strategyManager.renameFile(path.toFile()));
    }

}