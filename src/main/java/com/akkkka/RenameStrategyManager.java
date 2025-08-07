package com.akkkka;

import com.akkkka.strategy.*;
import com.akkkka.strategy.java.*;
import com.akkkka.strategy.yml.*;
import com.akkkka.strategy.xml.MapperXmlRenameStrategy;
import com.akkkka.strategy.xml.PomXmlRenameStrategy;
import com.akkkka.strategy.xml.RunXmlRenameStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:13
 * @description: 策略管理器
 */
public class RenameStrategyManager {
    private final List<RenameStrategy> strategies;
    Logger logger = Logger.getLogger(RenameStrategyManager.class.getName());

    public RenameStrategyManager() {
        this.strategies = new ArrayList<>();

        strategies.add(new PomXmlRenameStrategy());
        strategies.add(new MapperXmlRenameStrategy());
        strategies.add(new RunXmlRenameStrategy());

        strategies.add(new PropertiesRenameStrategy());
        strategies.add(new DockerfileRenameStrategy());
        strategies.add(new ConfRenameStrategy());
        strategies.add(new SqlRenameStrategy());
        strategies.add(new JsonRenameStrategy());

        strategies.add(new ApplicationJavaRenameStrategy());
        strategies.add(new MonitorApplicationJavaRenameStrategy());
        strategies.add(new TokenControllerJavaRenameStrategy());
        strategies.add(new RedisConfigurationJavaRenameStrategy());
        strategies.add(new TestBusControllerJavaRenameStrategy());
        strategies.add(new GenControllerJavaRenameStrategy());
        strategies.add(new CommonJavaRenameStrategy());

        strategies.add(new ApplicationCommonYmlRenameStrategy());
        strategies.add(new ApplicationYmlRenameStrategy());
        strategies.add(new DockerComposeYmlRenameStrategy());
        strategies.add(new PrometheusYmlRenameStrategy());
        strategies.add(new JobYmlRenameStrategy());
        strategies.add(new GatewayYmlRenameStrategy());
        strategies.add(new MonitorYmlRenameStrategy());
        strategies.add(new SnailjobServerYmlRenameStrategy());

    }

    public void renameFile(File file) {
        for (RenameStrategy strategy : strategies) {
            if (strategy.supports(file)) {
                logger.info("开始重命名文件："+file.getAbsolutePath());
                long startTime = System.currentTimeMillis();
                strategy.rename(file);
                long endTime = System.currentTimeMillis();
                logger.info("重命名文件完成："+file.getAbsolutePath()+",耗时："+(endTime-startTime)+"ms");
            }
        }
    }

    public boolean supports(File file) {
        for (RenameStrategy strategy : strategies) {
            if (strategy.supports(file)) {
                return true;
            }
        }
        return false;
    }
}
