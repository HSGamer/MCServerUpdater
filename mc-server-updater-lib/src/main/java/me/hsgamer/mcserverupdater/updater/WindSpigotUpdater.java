package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class WindSpigotUpdater extends GithubReleaseUpdater {
    public WindSpigotUpdater() {
        super("Wind-Development/WindSpigot", false);
    }

    @Override
    public Pattern getArtifactPattern(String version, String build) {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "";
    }
}
