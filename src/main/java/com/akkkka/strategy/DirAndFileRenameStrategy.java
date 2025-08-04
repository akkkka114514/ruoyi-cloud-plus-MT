package com.akkkka.strategy;

import com.akkkka.RenameStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.akkkka.Constants.*;
import static com.akkkka.RenameConfig.*;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:10
 * @description: 负责重命名目录和文件名
 */
public class DirAndFileRenameStrategy implements RenameStrategy {
    private static final Logger logger;
    static {
        logger = Logger.getLogger(DirAndFileRenameStrategy.class.getName());
        logger.setLevel(LOG_LEVEL);
    }
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
        String filename = file.getName();
        if (filename.contains(RUOYI_PROJECT_NAME)) {
            //处理项目根文件夹RuoYi-Cloud-Plus-x.x.x
            filename = MY_PROJECT_NAME;
        } else if (filename.contains(APP_FILENAME_SUFFIX) && filename.contains(RuoYi_STRING)) {
            //处理xxxApplication.java
            filename = file.getName().replace(RuoYi_STRING, MY_APP_NAME);
        } else if(filename.contains(ruoyi_STRING)) {
            //处理文件名带ruoyi字段的文件和文件夹
            filename = filename.replace(ruoyi_STRING, MY_PROJECT_NAME);
        } else if (filename.contains(RUOYI_COMPANY_NAME)) {
            //处理dromara文件夹
            filename = filename.replace(RUOYI_COMPANY_NAME, MY_COMPANY_NAME);
        } else if (filename.contains(RUOYI_TOP_DOMAIN) && file.isDirectory()) {
            //处理org文件夹
            filename = filename.replace(RUOYI_TOP_DOMAIN, MY_TOP_DOMAIN);
        }else if(filename.equals("README.md")
                &&file.getParentFile().getName().contains(RUOYI_PROJECT_NAME)){
            //确保是项目根文件夹下的README.md,其他有用
            try {
                FileUtils.delete(file);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "删除README.md失败: " + file.getAbsolutePath(), e);
            }
            return;
        }else if(filename.equals(".gitee")){
            try{
                FileUtils.deleteDirectory(file);
            }catch (IOException e){
                logger.log(Level.SEVERE, "删除.gitee失败: " + file.getAbsolutePath(), e);
            }
            return;
        }else {
            return;
        }

        File target = new File(file.getParentFile(), filename);
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
                if (filename.split("\\.").length >= 3) {
                    //处理ruoyi-snailjob-server.run.xml这样的，一般方法会重命名失败
                    FileUtils.copyFile(file, target);
                    FileUtils.delete(file);
                } else {
                    FileUtils.moveFile(file, target);
                }
            }
            if(!target.exists()){
                logger.log(Level.SEVERE, "重命名dirname和filename失败: " + file.getAbsolutePath());
            }else{
                logger.info("重命名dirname和filename成功:" + file.getAbsolutePath());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "重命名dirname和filename 失败: " + file.getAbsolutePath(), e);
        }
    }
}
