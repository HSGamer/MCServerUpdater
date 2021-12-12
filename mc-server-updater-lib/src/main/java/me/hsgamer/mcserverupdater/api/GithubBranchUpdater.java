package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.Utils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public abstract class GithubBranchUpdater implements SimpleFileUpdater, LatestBuild {
    private final String refLatestCommitUrl;
    private final String downloadUrl;

    protected GithubBranchUpdater(String repo) {
        String apiUrl = "https://api.github.com/repos/" + repo + "/";
        this.refLatestCommitUrl = apiUrl + "commits/heads/%s";
        this.downloadUrl = "https://github.com/" + repo + "/raw/%s/%s";
    }

    public abstract String getBranch(String version);

    public abstract String getFile(String version, String build);

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = String.format(downloadUrl, build, getFile(version, build));
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
        String url = String.format(refLatestCommitUrl, getBranch(version));
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            return jsonObject.getString("sha");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getChecksum(String version, String build) {
        return build;
    }

    @Override
    public File getChecksumFile() throws IOException {
        return Utils.getFile("github.commit");
    }
}
