package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class GithubReleaseUpdater implements LocalChecksum, UrlInputStreamUpdater {
    private final String repo;
    private final boolean versionAsTag;
    private final String releasesUrl;
    private final String releaseByTagUrl;
    private final String releaseAssetUrl;

    protected GithubReleaseUpdater(String repo, boolean versionAsTag) {
        this.repo = repo;
        this.versionAsTag = versionAsTag;
        String url = "https://api.github.com/repos/" + repo + "/";
        this.releasesUrl = url + "releases";
        this.releaseByTagUrl = url + "releases/tags/%s";
        this.releaseAssetUrl = url + "releases/%s/assets";
    }

    public abstract Pattern getArtifactPattern(String version, String build);

    @Override
    public String getFileUrl(String version, String build) {
        String assetUrl = String.format(releaseAssetUrl, build);
        getUpdateBuilder().debug("Getting asset URL from " + assetUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(assetUrl));
            InputStream inputStream = connection.getInputStream();
            JSONArray array = new JSONArray(new JSONTokener(inputStream));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                if (getArtifactPattern(version, build).matcher(name).matches()) {
                    String url = object.getString("browser_download_url");
                    getUpdateBuilder().debug("Found asset URL: " + url);
                    return url;
                }
            }
            return null;
        } catch (IOException e) {
            getUpdateBuilder().debug(e);
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        JSONObject object = null;
        if (versionAsTag) {
            String tagToIdUrl = String.format(releaseByTagUrl, version);
            getUpdateBuilder().debug("Getting release ID from " + tagToIdUrl);
            try {
                URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(tagToIdUrl));
                InputStream inputStream = connection.getInputStream();
                object = new JSONObject(new JSONTokener(inputStream));
            } catch (IOException e) {
                getUpdateBuilder().debug(e);
            }
        } else {
            String url = releasesUrl + "?per_page=1";
            getUpdateBuilder().debug("Getting release ID from " + url);
            try {
                URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
                InputStream inputStream = connection.getInputStream();
                JSONArray array = new JSONArray(new JSONTokener(inputStream));
                object = array.getJSONObject(0);
            } catch (IOException e) {
                getUpdateBuilder().debug(e);
            }
        }
        if (object == null) {
            return null;
        }
        String id = Objects.toString(object.get("id"), null);
        getUpdateBuilder().debug("Found release ID: " + id);
        return id;
    }

    @Override
    public String getChecksum(String version, String build) {
        return repo + "||" + build;
    }
}
