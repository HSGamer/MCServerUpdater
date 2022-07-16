package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.util.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class GithubReleaseUpdater implements SimpleFileUpdater, LatestBuild, UrlInputStreamUpdater {
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
        try {
            URLConnection connection = WebUtils.openConnection(assetUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONArray array = new JSONArray(new JSONTokener(inputStream));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                if (getArtifactPattern(version, build).matcher(name).matches()) {
                    return object.getString("browser_download_url");
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        JSONObject object = null;
        if (versionAsTag) {
            String tagToIdUrl = String.format(releaseByTagUrl, version);
            try {
                URLConnection connection = WebUtils.openConnection(tagToIdUrl, UserAgent.CHROME);
                InputStream inputStream = connection.getInputStream();
                object = new JSONObject(new JSONTokener(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String url = releasesUrl + "?per_page=1";
            try {
                URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
                InputStream inputStream = connection.getInputStream();
                JSONArray array = new JSONArray(new JSONTokener(inputStream));
                object = array.getJSONObject(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (object == null) {
            return null;
        }
        return Objects.toString(object.get("id"), null);
    }

    @Override
    public String getChecksum(String version, String build) {
        return repo + "||" + build;
    }

    @Override
    public File getChecksumFile() throws IOException {
        return Utils.getFile("github.release");
    }
}
