package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.File;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 14:14
 * @description:
 */
public class SqlRenameStrategy implements RenameStrategy {
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".sql");
    }

    @Override
    public void rename(File file) {

    }
}
