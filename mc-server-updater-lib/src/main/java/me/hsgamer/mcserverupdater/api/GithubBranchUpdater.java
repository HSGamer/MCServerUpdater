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
import java.util.regex.Pattern;

public abstract class GithubBranchUpdater implements SimpleFileUpdater, LatestBuild, UrlInputStreamUpdater {
    private final String refLatestCommitUrl;
    private final String downloadUrl;
    private final String filesUrl;

    protected GithubBranchUpdater(String repo) {
        String apiUrl = "https://api.github.com/repos/" + repo + "/";
        this.refLatestCommitUrl = apiUrl + "commits/heads/%s";
        this.downloadUrl = "https://github.com/" + repo + "/raw/%s/%s";
        this.filesUrl = apiUrl + "git/trees/%s?recursive=true";
    }

    public abstract String getBranch(String version);

    public abstract Pattern getFilePattern(String version, String build);

    public String getFile(String version, String build) {
        String url = String.format(filesUrl, build);
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONObject object = new JSONObject(new JSONTokener(inputStream));
            JSONArray array = object.getJSONArray("tree");
            Pattern pattern = getFilePattern(version, build);
            for (int i = 0; i < array.length(); i++) {
                JSONObject file = array.getJSONObject(i);
                String path = file.getString("path");
                String type = file.getString("type");
                if (type.equalsIgnoreCase("blob") && pattern.matcher(path).matches()) {
                    return path;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getFileUrl(String version, String build) {
        String file = getFile(version, build);
        if (file == null) {
            return null;
        }
        return String.format(downloadUrl, build, file);
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
