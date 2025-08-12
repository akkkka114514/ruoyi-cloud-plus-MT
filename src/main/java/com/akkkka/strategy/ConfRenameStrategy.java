package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 14:19
 * @description:
 */
public class ConfRenameStrategy implements RenameStrategy {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(ConfRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }

    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".conf");
    }

    @Override
    public void rename(File file) {
        try{
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.contains(ruoyi_STRING)) {
                    lines.set(i, line.replace(ruoyi_STRING, "admin"));
                }
            }
            Files.write(file.toPath(), lines);
        }catch (IOException e){
            logger.log(Level.SEVERE, "解析JSON文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
