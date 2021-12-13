package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubBranchUpdater;

public class BurritoSpigotUpdater extends GithubBranchUpdater {
    public BurritoSpigotUpdater() {
        super("CobbleSword/BurritoSpigot");
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }

    @Override
    public String getBranch(String version) {
        return "downloads";
    }

    @Override
    public String getFile(String version, String build) {
        return "jars/BurritoSpigot.jar";
    }
}
