package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.RuoYi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-04 14:58
 * @description:修改TokenController.java文件
 */
public class TokenControllerJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals("TokenController.java");
    }

    @Override
    public void rename(File file) {
        logger.info("重命名TokenController.java内容："+file.getAbsolutePath());
        CompilationUnit cu = parse(file);

        renameStringLiteralExprInJava(cu,RuoYi_STRING,MY_PROJECT_NAME);

        writeFile(file, cu);
        logger.info("重命名TokenController.java内容："+file.getAbsolutePath()+"成功");
    }
}
