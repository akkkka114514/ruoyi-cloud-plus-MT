package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.io.File;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.RuoYi_STRING;
import static com.akkkka.RenameConfig.MY_APP_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-04 14:59
 * @description: 重命名XXXApplication.java里的ruoyi字段
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
        logger.info("重命名Application.java内容："+file.getAbsolutePath());
        CompilationUnit cu = parse(file);

        renameAppClassNameInJava(cu);
        renameObjCreationInJava(cu);

        writeFile(file, cu);
        logger.info("重命名Application.java内容："+file.getAbsolutePath()+"成功");
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
