package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class WindSpigotUpdater extends GithubReleaseUpdater {
    private final UpdateBuilder updateBuilder;

    public WindSpigotUpdater(UpdateBuilder updateBuilder) {
        super("Wind-Development/WindSpigot", false);
        this.updateBuilder = updateBuilder;
    }

    @Override
    public Pattern getArtifactPattern(String version, String build) {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
