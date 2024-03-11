package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class KaiijuUpdater extends GithubReleaseUpdater {
    public KaiijuUpdater(VersionQuery versionQuery) {
        super(versionQuery, "KaiijuMC/Kaiiju");
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile("kaiiju-paperclip-.+-reobf\\.jar");
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
