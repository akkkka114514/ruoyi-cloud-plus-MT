package com.akkkka;

import java.io.File;
import java.io.IOException;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 20:57
 * @description: 策略接口
 */
public interface RenameStrategy {
    boolean supports(File file);
    void rename(File file);
}
