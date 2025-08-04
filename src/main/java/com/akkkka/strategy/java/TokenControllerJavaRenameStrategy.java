package com.akkkka.strategy.java;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.LOG_LEVEL;
import static com.akkkka.Constants.RuoYi_STRING;
import static com.akkkka.RenameConfig.MY_PROJECT_NAME;

/**
 * @author: akkkka114514
 * @create: 2025-08-04 14:58
 * @description:
 */
public class TokenControllerJavaRenameStrategy extends JavaRenameStrategy{
    private static final Logger logger;
    static {
        logger = Logger.getLogger(JavaRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
    @Override
    public boolean supports(File file) {
        return file.getName().contains("TokenController.java");

    }

    @Override
    public void rename(File file) {
        CompilationUnit cu = parse(file);

        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(StringLiteralExpr n, Void arg) {
                String originalValue = n.getValue();
                System.out.println("发现字符串: " + originalValue);
                if (originalValue.contains(RuoYi_STRING)) {
                    String newValue = originalValue.replace(RuoYi_STRING, MY_PROJECT_NAME);
                    n.setString(newValue);
                }
                super.visit(n, arg);
            }
        }, null);
        try {
            Files.writeString(file.toPath(), cu.toString());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "写入文件失败: " + file.getAbsolutePath(), e);
        }
    }
}
