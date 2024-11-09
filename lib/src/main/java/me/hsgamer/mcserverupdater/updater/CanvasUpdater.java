package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CanvasUpdater extends GithubReleaseUpdater {
    private static final Pattern VERSION_PATTERN = Pattern.compile("\\[\"version\"\\s*=\\s*MC([\\d.]+)]");

    public CanvasUpdater(VersionQuery versionQuery) {
        super(versionQuery, "CraftCanvasMC/Canvas");
    }

    private static String getVersion(String body) {
        Matcher versionMatcher = VERSION_PATTERN.matcher(body);
        if (!versionMatcher.find()) {
            throw new IllegalStateException("Cannot find the version");
        }
        return versionMatcher.group(1);
    }

    @Override
    public Pattern getArtifactPattern() {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        JSONObject release = getLatestRelease();
        String body = release.getString("body");
        return getVersion(body);
    }

    @Override
    public JSONObject getReleaseObject() {
        return getReleaseByPredicate(release -> {
            String body = release.getString("body");
            return getVersion(body).equals(version);
        });
    }
}
