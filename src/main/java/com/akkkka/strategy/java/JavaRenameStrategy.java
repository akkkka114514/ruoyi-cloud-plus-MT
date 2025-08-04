package com.akkkka.strategy.java;

import com.akkkka.Parsable;
import com.akkkka.RenameStrategy;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ClassExpr;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.*;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:02
 * @description: 负责在java文件里的类名、包名、导入包名、类创建对象、包声明进行修改
 */
public abstract class JavaRenameStrategy implements RenameStrategy, Parsable<CompilationUnit> {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().contains(".java")&&!file.getName().contains(".java.vm");
    }


    void renameImportExprInJava(CompilationUnit cu) {
        cu.getImports().forEach(importDeclaration -> {
            String oldName = importDeclaration.getNameAsString();
            importDeclaration.setName(oldName.replace(RUOYI_GROUP_ID, MY_GROUP_ID));
        });
    }

    void renamePackageExprInJava(CompilationUnit cu) {
        cu.getPackageDeclaration().ifPresent(pd -> {
            String packageName = pd.getNameAsString();
            packageName = packageName.replace(RUOYI_GROUP_ID, MY_GROUP_ID)
                    .replace(ruoyi_STRING, MY_PROJECT_NAME);
            pd.setName(packageName);
        });
    }

    void renameAuthor(CompilationUnit cu){
        cu.getAllContainedComments().forEach(comment -> {
            String content = comment.getContent();
            // 检查是否包含 @author 并移除整行
            if (content.contains("@author")) {
                // 只移除包含 @author 的行
                String newContent = content.lines()
                        .filter(line -> !line.contains("@author"))
                        .reduce("", (a, b) -> a + "\n" + b)
                        .trim();
                comment.setContent(newContent);
            }
        });
    }
    void renameInClassExpr(ClassExpr classExpr){
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

    @Override
    public CompilationUnit parse(File file) {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        StaticJavaParser.setConfiguration(config);
        try {
            return StaticJavaParser.parse(file);
        }catch (IOException e){
            logger.log(Level.SEVERE, "解析失败，文件不存在: " + file.getAbsolutePath(), e);
        }
        return null;
    }
}
