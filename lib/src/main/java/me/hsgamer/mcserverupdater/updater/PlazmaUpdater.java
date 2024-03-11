package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class PlazmaUpdater extends GithubReleaseUpdater {
    public PlazmaUpdater(VersionQuery versionQuery) {
        super(versionQuery, "PlazmaMC/PlazmaBukkit");
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile("plazma-paperclip-.+-reobf\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.19.4";
    }

    @Override
    public JSONObject getReleaseObject() {
        return getReleaseByTag("latest-" + version);
    }
}
