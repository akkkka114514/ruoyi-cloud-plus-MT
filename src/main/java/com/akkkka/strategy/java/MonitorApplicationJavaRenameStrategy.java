package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
/**
 * @author: akkkka114514
 * @create: 2025-08-04 15:00
 * @description:
 */
public class MonitorApplicationJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().equals("MyExampleProjectMonitorApplication.java");
    }

    @Override
    public void rename(File file) {
        CompilationUnit cu = parse(file);
        renameInRunFunc(cu);
        renameAuthor(cu);
        renameAuthor(cu);
        renameImportExprInJava(cu);
        renamePackageExprInJava(cu);
        try {
            Files.write(Paths.get(file.getAbsolutePath()), cu.toString().getBytes());
        }catch (IOException e){
            logger.log(Level.SEVERE, "写回Java文件失败: " + file.getAbsolutePath(), e);
        }
    }

    private void renameInRunFunc(CompilationUnit cu){
        cu.findAll(MethodCallExpr.class).stream()
            .filter(mce -> "run".equals(mce.getNameAsString()))
            .filter(mce -> mce.getScope().isPresent())
            .filter(mce -> "SpringApplication".equals(mce.getScope().get().toString()))
            .forEach(mce -> {
                mce.getArguments().stream()
                    .filter(arg -> arg instanceof ClassExpr)
                    .forEach(arg -> renameInClassExpr((ClassExpr)arg));

            });
    }
}
