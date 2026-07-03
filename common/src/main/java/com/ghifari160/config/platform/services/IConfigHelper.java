package com.ghifari160.config.platform.services;

import java.nio.file.Path;

public interface IConfigHelper {
    Path getDefaultConfigDir();
    Path getConfigDir();

    boolean isDedicatedServer();
}
