package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;

/**
 * @author: akkkka114514
 * @create: 2025-08-05 13:02
 * @description: TestBusController.java重命名策略
 */
public class TestBusControllerJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().contains("TestBusController.java");
    }

    @Override
    public void rename(File file) {
        CompilationUnit cu = parse(file);

        renameCommentInJava(cu);

        writeFile(file, cu);
    }
}
