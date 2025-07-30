package com.akkkka;

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
        strategies.add(new JavaRenameStrategy());
        strategies.add(new YamlRenameStrategy());
        strategies.add(new DirAndFileRenameStrategy());
    }

    public void renameFile(File file) {
        for (RenameStrategy strategy : strategies) {
            if (strategy.supports(file)) {
                strategy.rename(file);
            }
        }
    }
}
