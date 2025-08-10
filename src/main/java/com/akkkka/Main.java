package com.akkkka;

import com.akkkka.strategy.DirAndFileRenameStrategy;
import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
    private static final Options options = new Options();
    static {
        logger = Logger.getLogger(Main.class.getName());
        logger.setLevel(LOG_LEVEL);

        options.addOption("g", "group-id", true, "The group id of the project");
        options.addOption("p", "project-name", true, "The project name");
        options.addOption("a", "app-name", true, "The app name");
        options.addOption("d", "dest-dir", true, "The destination directory");
        options.addOption("z", "zip-path", true, "The zip path");
        options.addOption("t", "thread-num", true, "The thread number");
    }
    public static String rootName;
    private static final RenameStrategyManager strategyManager = new RenameStrategyManager();

    public static void main(String[] args) throws IOException {
        parseArgs(args);
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

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.warning("线程池未能正常关闭");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

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
                    boolean i = outFile.mkdirs();
                    assert i;
                } else {
                    boolean i = outFile.getParentFile().mkdirs();
                    assert i;
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
    public static void fileBatchRename(File rootDir) {
        //为了防止路径混乱，先重命名dirname和filename
        DirAndFileRenameStrategy dirAndFileRenameStrategy = new DirAndFileRenameStrategy();
        try(Stream<Path> paths = Files.walk(rootDir.toPath())){
            paths.sorted(
                    Comparator.comparingInt(
                            path -> path.toString().split("\\\\").length).reversed())
            .forEach(path -> {
                File file = path.toFile();
                dirAndFileRenameStrategy.rename(file);
            });
        }catch (IOException | SecurityException e){
            logger.log(Level.SEVERE,"处理文件失败: " + rootDir.getAbsolutePath(),e);
        }
        rootDir = new File(destDir+MY_PROJECT_NAME);
        try(Stream<Path> paths = Files.walk(rootDir.toPath())){
            paths.filter(path ->
                    path.toFile().isFile()&&strategyManager.supports(path.toFile()))
                    .forEach(path ->
                            executor.execute(() -> strategyManager.renameFile(path.toFile())));
        }catch (IOException | SecurityException e){
            logger.log(Level.SEVERE,"处理文件失败: " + rootDir.getAbsolutePath(),e);
        }
    }
    private static void parseArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            MY_GROUP_ID = cmd.getOptionValue("g");
            MY_PROJECT_NAME = cmd.getOptionValue("p");
            MY_APP_NAME = cmd.getOptionValue("a");
            destDir = cmd.getOptionValue("d");
            ZIP_PATH = cmd.getOptionValue("z");
            THREAD_NUM = Integer.parseInt(cmd.getOptionValue("t").trim());
        }catch (ParseException | NumberFormatException e){
            logger.log(Level.SEVERE,"参数解析失败",e);
            System.exit(1);
        }

    }
}