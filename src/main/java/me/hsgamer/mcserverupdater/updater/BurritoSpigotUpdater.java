package me.hsgamer.mcserverupdater.updater;

public class BurritoSpigotUpdater extends GithubReleaseUpdater {
    public BurritoSpigotUpdater() {
        super("CobbleSword/BurritoSpigot");
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }

    @Override
    public String getArtifact(String version, String build) {
        return "BurritoSpigot.jar";
    }
}
