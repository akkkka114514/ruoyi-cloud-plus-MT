package com.akkkka;

import com.akkkka.strategy.DockerfileRenameStrategy;
import com.akkkka.strategy.JsonRenameStrategy;
import com.akkkka.strategy.java.ApplicationJavaRenameStrategy;
import com.akkkka.strategy.java.JavaRenameStrategy;
import com.akkkka.strategy.java.MonitorApplicationJavaRenameStrategy;
import com.akkkka.strategy.java.TokenControllerJavaRenameStrategy;
import com.akkkka.strategy.yml.*;
import com.akkkka.strategy.xml.MapperXmlRenameStrategy;
import com.akkkka.strategy.xml.PomXmlRenameStrategy;
import com.akkkka.strategy.xml.RunXmlRenameStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: akkkka114514
 * @create: 2025-07-26 21:13
 * @description: 策略管理器
 */
public class RenameStrategyManager {
    private final List<RenameStrategy> strategies;

    public RenameStrategyManager() {
        this.strategies = new ArrayList<>();

        strategies.add(new PomXmlRenameStrategy());
        strategies.add(new MapperXmlRenameStrategy());
        strategies.add(new RunXmlRenameStrategy());

        //strategies.add(new PropertiesRenameStrategy());
        strategies.add(new DockerfileRenameStrategy());

        strategies.add(new ApplicationJavaRenameStrategy());
        strategies.add(new MonitorApplicationJavaRenameStrategy());
        strategies.add(new TokenControllerJavaRenameStrategy());

        strategies.add(new ApplicationCommonYmlRenameStrategy());
        strategies.add(new ApplicationYmlRenameStrategy());
        strategies.add(new DockerComposeYmlRenameStrategy());
        strategies.add(new PrometheusYmlRenameStrategy());
        strategies.add(new JobYmlRenameStrategy());
        strategies.add(new GatewayYmlRenameStrategy());

        strategies.add(new JsonRenameStrategy());
    }

    public void renameFile(File file) {
        for (RenameStrategy strategy : strategies) {
            if (strategy.supports(file)) {
                strategy.rename(file);
            }
        }
    }
}
