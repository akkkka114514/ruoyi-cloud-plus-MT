package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-07-30 17:43
 * @description: 修改dockerfile中的ruoyi字段
 */
public class DockerfileRenameStrategy implements RenameStrategy {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(DirAndFileRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals("Dockerfile");
    }

    @Override
    public void rename(File file) {
        try {
            // 读取所有行
            List<String> lines = new ArrayList<>();
            Path path = Paths.get(file.getAbsolutePath());
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 替换行中的 ruoyi 字段
                    line = line.replace(ruoyi_STRING, MY_PROJECT_NAME);
                    lines.add(line);
                }
            }
            // 写回文件
            Files.write(path, lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

            logger.info("成功修改 Dockerfile: " + file.getAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE,"处理 Dockerfile 失败: " + file.getAbsolutePath(),e);
        }
    }
}
