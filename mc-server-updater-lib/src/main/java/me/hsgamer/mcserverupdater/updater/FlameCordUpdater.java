package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

public class FlameCordUpdater extends GithubReleaseUpdater {
    public FlameCordUpdater() {
        super("2lstudios-mc/FlameCord");
    }

    @Override
    public String getArtifact(String version, String build) {
        return "FlameCord.jar";
    }
}
