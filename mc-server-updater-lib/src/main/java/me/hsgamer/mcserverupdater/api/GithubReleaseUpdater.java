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

public abstract class GithubReleaseUpdater implements SimpleFileUpdater, LatestBuild {
    private final String repo;
    private final String releasesUrl;
    private final String downloadUrl;

    protected GithubReleaseUpdater(String repo) {
        this.repo = repo;
        this.releasesUrl = "https://api.github.com/repos/" + repo + "/releases";
        this.downloadUrl = "https://github.com/" + repo + "/releases/download/%s/%s";
    }

    public abstract String getArtifact(String version, String build);

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = String.format(downloadUrl, build, getArtifact(version, build));
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String url = releasesUrl + "?per_page=1";
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONArray array = new JSONArray(new JSONTokener(inputStream));
            JSONObject object = array.getJSONObject(0);
            return object.getString("tag_name");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
