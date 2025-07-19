package com.akkkka;

import com.akkkka.utils.DecompressUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws IOException {
        String zipPath = "C:\\Users\\Admin\\Downloads\\RuoYi-Cloud-Plus.zip";
        String destDir = "D:";

        if(!destDir.endsWith("\\")){
            destDir = destDir+"\\";
        }
        DecompressUtil.unzip(zipPath, destDir);
        logger.info("解压完成");
    }

}