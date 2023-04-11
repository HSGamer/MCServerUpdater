package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;

import java.util.regex.Pattern;

public class TitaniumUpdater extends GithubReleaseUpdater {
    public TitaniumUpdater(VersionQuery versionQuery) {
        super(versionQuery, "TitaniumMC/Titanium", false);
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }
}
