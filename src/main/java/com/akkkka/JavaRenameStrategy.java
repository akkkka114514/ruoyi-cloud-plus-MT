package com.akkkka;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.*;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:02
 * @description: 负责在java文件里的类名、包名、导入包名、类创建对象、包声明进行修改
 */
public class JavaRenameStrategy implements RenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.toString().contains(APP_FILENAME_SUFFIX);

    }

    @Override
    public void rename(File file) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            renameAppClassNameInJava(cu);
            renameImportExprInJava(cu);
            renameObjCreationInJava(cu);
            renamePackageExprInJava(cu);

            Files.write(Paths.get(file.getAbsolutePath()), cu.toString().getBytes());
            logger.info("成功修改Java文件内容: " + file.getAbsolutePath());
        } catch (IOException e){
            if(e instanceof FileNotFoundException){
                logger.log(Level.SEVERE, "解析失败，文件不存在: " + file.getAbsolutePath(), e);
            }else{
                logger.log(Level.SEVERE, "写回Java文件失败: " + file.getAbsolutePath(), e);
            }
        }
    }

    private void renameImportExprInJava(CompilationUnit cu) {
        cu.getImports().forEach(importDeclaration -> {
            String oldName = importDeclaration.getNameAsString();
            importDeclaration.setName(oldName.replace(RUOYI_GROUP_ID, MY_GROUP_ID));
        });
    }

    private void renamePackageExprInJava(CompilationUnit cu) {
        cu.getPackageDeclaration().ifPresent(pd -> {
            String packageName = pd.getNameAsString();
            packageName = packageName.replace(RUOYI_GROUP_ID, MY_GROUP_ID)
                    .replace(ruoyi_STRING, MY_PROJECT_NAME)
                    .replace(RUOYI_TOP_DOMAIN, MY_TOP_DOMAIN)
                    .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME);
            pd.setName(packageName);
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

    private void renameObjCreationInJava(CompilationUnit cu) {
        cu.findAll(ObjectCreationExpr.class).forEach(oc -> {
            if (oc.getType().getNameAsString().equals("SpringApplication")) {
                oc.getArguments().forEach(arg -> {
                    if (arg instanceof ClassExpr) {
                        ClassExpr classExpr = (ClassExpr) arg;
                        String className = classExpr.getType().asString();
                        if (className.contains(RuoYi_STRING)) {
                            String newClassName = className.replace(RuoYi_STRING, MY_APP_NAME);
                            try {
                                classExpr.setType(StaticJavaParser.parseType(newClassName));
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "无法解析新的类名: " + newClassName, e);
                            }
                        }
                    }
                });
            }
        });
    }
}
