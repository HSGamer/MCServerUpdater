package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class PearlUpdater extends GithubReleaseUpdater {
    private final UpdateBuilder updateBuilder;

    public PearlUpdater(UpdateBuilder updateBuilder) {
        super("Pearl-Project/Pearl", true);
        this.updateBuilder = updateBuilder;
    }

    @Override
    public Pattern getArtifactPattern(String version, String build) {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.18.2";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
