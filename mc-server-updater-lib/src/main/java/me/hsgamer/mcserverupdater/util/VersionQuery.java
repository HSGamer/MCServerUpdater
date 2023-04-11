package me.hsgamer.mcserverupdater.util;

import me.hsgamer.mcserverupdater.UpdateBuilder;

public class VersionQuery {
    public final String version;
    public final boolean isLatest;
    public final UpdateBuilder updateBuilder;

    public VersionQuery(String version, UpdateBuilder updateBuilder) {
        this.version = version;
        this.isLatest = "latest".equalsIgnoreCase(version) || "default".equalsIgnoreCase(version);
        this.updateBuilder = updateBuilder;
    }
}
