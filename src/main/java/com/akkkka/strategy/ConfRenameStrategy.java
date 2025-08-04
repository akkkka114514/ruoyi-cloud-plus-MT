package com.akkkka.strategy;

import com.akkkka.RenameStrategy;

import java.io.File;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 14:19
 * @description:
 */
public class ConfRenameStrategy implements RenameStrategy {
    @Override
    public boolean supports(File file) {
        return file.getName().endsWith(".conf");
    }

    @Override
    public void rename(File file) {

    }
}
