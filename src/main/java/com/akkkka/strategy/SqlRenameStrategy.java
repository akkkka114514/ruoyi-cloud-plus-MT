package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-06 16:18
 * @description:
 */
public class SqlRenameStrategy implements RenameStrategy {
    private static final Logger logger;

    static {
        logger = Logger.getLogger(SqlRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }

    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".sql");
    }

    @Override
    public void rename(File file) {
        logger.info("开始处理SQL文件: " + file.getAbsolutePath());
        LinkedList<String> newLines = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            br.lines()
            .forEachOrdered(line -> {
                if(line.contains(ruoyi_STRING)){
                    line = line.replaceAll(ruoyi_STRING, MY_PROJECT_NAME);
                } else if (line.contains(RUOYI_PROJECT_NAME)) {
                    line = "/n";
                }
                newLines.add(line);
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "解析SQL文件失败: " + file.getAbsolutePath(), e);
        }
        writeFile(file, newLines);
        logger.info("处理完成SQL文件: " + file.getAbsolutePath());
    }


    private void writeFile(File file , List<String> newLines){
        try {
            Files.write(Paths.get(file.getAbsolutePath()), newLines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入SQL文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
