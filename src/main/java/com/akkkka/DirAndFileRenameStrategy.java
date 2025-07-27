package com.akkkka;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.*;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:10
 * @description: 负责重命名目录和文件名
 */
public class DirAndFileRenameStrategy implements RenameStrategy{
    private static final Logger logger = Logger.getLogger(DirAndFileRenameStrategy.class.getName());

    @Override
    public boolean supports(File file) {
        return true; // 作为默认策略处理所有文件和目录
    }

    @Override
    public void rename(File file) {
        renameDirAndFileName(file);
    }

    private void renameDirAndFileName(File file) {
        if (!file.getParentFile().exists()) {
            return;
        }

        String filename = file.getName(), originName = file.getName();
        if (file.getName().contains(RUOYI_PROJECT_NAME)) {
            filename = MY_PROJECT_NAME;
        } else if (file.getName().contains(APP_FILENAME_SUFFIX)) {
            filename = file.getName().replace(RuoYi_STRING, MY_APP_NAME);
        } else {
            filename = filename.replace(ruoyi_STRING, MY_PROJECT_NAME)
                    .replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME)
                    .replace(RUOYI_TOP_DOMAIN, MY_TOP_DOMAIN);
        }

        File target = new File(file.getParentFile(), filename);
        if (!filename.equals(originName)) {
            try {
                if (file.isDirectory()) {
                    if (target.exists()) {
                        // 如果目标目录已存在，则将源目录移动到目标目录中
                        FileUtils.moveToDirectory(file, target, !DO_CREATE_DEST_DIR);
                    } else {
                        // 如果目标目录不存在，则创建目标目录并移动源目录
                        FileUtils.moveDirectory(file, target);
                    }
                } else {
                    FileUtils.moveFile(file, new File(file.getParentFile(), filename));
                }
                logger.info("重命名dirname和filename成功:" + file.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "重命名dirname和filename 失败: " + file.getAbsolutePath(),e);
            }
        }
    }
}
