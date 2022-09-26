package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class TitaniumUpdater extends GithubReleaseUpdater {
    private final UpdateBuilder updateBuilder;

    public TitaniumUpdater(UpdateBuilder updateBuilder) {
        super("TitaniumMC/Titanium", false);
        this.updateBuilder = updateBuilder;
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
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
