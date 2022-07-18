package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;

public class SpongeUpdater implements InputStreamUpdater, FileDigestChecksum, LatestBuild {
    private final String versionUrl;
    private final String buildUrl;
    private final boolean isRecommended;

    public SpongeUpdater(boolean isForge, boolean isRecommended) {
        this.isRecommended = isRecommended;
        String baseUrl = "https://dl-api-new.spongepowered.org/api/v2/groups/org.spongepowered/artifacts/";
        String artifactUrl = baseUrl + (isForge ? "spongeforge" : "spongevanilla") + "/";
        versionUrl = artifactUrl + "versions";
        buildUrl = versionUrl + "/%s";
    }

    private String getQueryReadyFetchUrl(String url) {
        return url + "?" + (isRecommended ? "recommended=true" : "");
    }

    private JSONObject getJarInfo(String build) throws Exception {
        String url = String.format(buildUrl, build);
        URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
        InputStream inputStream = connection.getInputStream();
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        JSONArray assets = jsonObject.getJSONArray("assets");
        JSONObject jarInfo = null;
        boolean hasUniversal = false;
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            String extension = asset.getString("extension");
            String classifier = asset.getString("classifier");
            if (classifier == null || extension == null || !extension.equalsIgnoreCase("jar")) continue;
            if (classifier.equalsIgnoreCase("universal")) {
                hasUniversal = true;
                jarInfo = asset;
            } else if (classifier.trim().isEmpty() && !hasUniversal) {
                jarInfo = asset;
            }
        }
        return jarInfo;
    }

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("MD5");
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        try {
            JSONObject jarInfo = getJarInfo(build);
            if (jarInfo == null) {
                return null;
            }
            String downloadUrl = jarInfo.getString("downloadUrl");
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(downloadUrl));
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String url = getQueryReadyFetchUrl(versionUrl) + "&limit=1&tags=,minecraft:" + version;
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject artifacts = jsonObject.getJSONObject("artifacts");
            String[] builds = JSONObject.getNames(artifacts);
            if (builds == null || builds.length == 0) {
                return null;
            }
            return builds[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getChecksum(String version, String build) {
        try {
            JSONObject jarInfo = getJarInfo(build);
            if (jarInfo == null) {
                return null;
            }
            return jarInfo.getString("md5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefaultVersion() {
        return "1.12.2";
    }
}
