package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class TitaniumUpdater extends GithubReleaseUpdater {
    public TitaniumUpdater(VersionQuery versionQuery) {
        super(versionQuery, "TitaniumMC/Titanium");
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }

    @Override
    public JSONObject getReleaseObject() {
        return getLatestRelease();
    }
}
