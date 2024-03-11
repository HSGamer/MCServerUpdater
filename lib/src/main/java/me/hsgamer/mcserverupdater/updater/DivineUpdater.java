package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class DivineUpdater extends GithubReleaseUpdater {
    public DivineUpdater(VersionQuery versionQuery) {
        super(versionQuery, "DivineMC/DivineMC");
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile("DivineMC-paperclip-.+-reobf\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.20.4";
    }

    @Override
    public JSONObject getReleaseObject() {
        return getReleaseByTag("latest-" + version);
    }
}
