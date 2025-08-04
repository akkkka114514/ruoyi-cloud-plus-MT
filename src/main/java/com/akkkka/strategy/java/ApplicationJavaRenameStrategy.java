package com.akkkka.strategy.java;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.RuoYi_STRING;
import static com.akkkka.RenameConfig.MY_APP_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-04 14:59
 * @description:
 */
public class ApplicationJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().contains("Application.java")&&!file.getName().contains(".java.vm");

    }

    @Override
    public void rename(File file) {
        CompilationUnit cu = parse(file);
        renameAppClassNameInJava(cu);
        renameImportExprInJava(cu);
        renameObjCreationInJava(cu);
        renamePackageExprInJava(cu);
        renameAuthor(cu);
        try {
            Files.write(Paths.get(file.getAbsolutePath()), cu.toString().getBytes());
        }catch (IOException e) {
            logger.log(Level.SEVERE, "写回Java文件失败: " + file.getAbsolutePath(), e);
        }
    }

    private void renameObjCreationInJava(CompilationUnit cu) {
        cu.findAll(ObjectCreationExpr.class).stream()
            .filter(oc -> oc.getType().getNameAsString().equals("SpringApplication"))
            .forEach(oc -> {
                oc.getArguments().stream()
                    .filter(arg -> arg instanceof ClassExpr)
                    .forEach(arg -> renameInClassExpr((ClassExpr)arg));
        });
    }
    private void renameAppClassNameInJava(CompilationUnit cu) {
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cid -> {
            String className = cid.getNameAsString();
            if (className.contains(RuoYi_STRING)) {
                className = className.replace(RuoYi_STRING, MY_APP_NAME);
                cid.setName(className);
            }
        });
    }
}
