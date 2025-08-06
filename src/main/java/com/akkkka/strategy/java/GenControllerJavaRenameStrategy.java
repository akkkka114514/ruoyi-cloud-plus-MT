package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.ruoyi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-05 09:45
 * @description: GenController.java重命名策略
 */
public class GenControllerJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals("GenController.java");
    }

    @Override
    public void rename(File file) {
        logger.info("重命名GenController.java内容："+file.getAbsolutePath());
        CompilationUnit cu = parse(file);

        renameStringLiteralExprInJava(cu,ruoyi_STRING,MY_PROJECT_NAME);

        writeFile(file, cu);
        logger.info("重命名GenController.java内容："+file.getAbsolutePath()+"成功");
    }
}
