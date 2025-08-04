package com.akkkka;

import java.io.File;

/**
 * @author: akkkka114514
 * @create: 2025-08-02 16:24
 * @description:
 */
public interface Parsable<R>{
    R parse(File file);
}
