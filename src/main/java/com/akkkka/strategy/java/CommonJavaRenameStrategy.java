package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;

/**
 * @author: akkkka114514
 * @create: 2025-08-05 10:14
 * @description: 用来兜底的java rename 策略
 */
public class CommonJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public void rename(File file) {
        logger.info("重命名java文件中的package语句、author、import语句内容："+file.getAbsolutePath());
        CompilationUnit cu = parse(file);

        renamePackageExprInJava(cu);
        renameImportExprInJava(cu);
        renameAuthor(cu);

        writeFile(file, cu);
        logger.info("重命名java文件中的package语句、author、import语句内容完成："+file.getAbsolutePath());
    }
}
