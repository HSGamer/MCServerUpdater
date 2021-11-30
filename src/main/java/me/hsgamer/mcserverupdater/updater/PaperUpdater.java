package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;

public class PaperUpdater implements InputStreamUpdater, FileDigestChecksum, LatestBuild {
    private final String projectUrl;
    private final String versionUrl;
    private final String buildUrl;
    private final String downloadUrl;

    public PaperUpdater(String project) {
        projectUrl = String.format("https://papermc.io/api/v2/projects/%s/", project);
        versionUrl = projectUrl + "versions/%s/";
        buildUrl = versionUrl + "builds/%s/";
        downloadUrl = buildUrl + "downloads/%s";
    }

    private JSONObject getDownload(String version, String build) throws IOException {
        String formattedUrl = String.format(buildUrl, version, build);
        URLConnection connection = WebUtils.openConnection(formattedUrl, UserAgent.CHROME);
        InputStream inputStream = connection.getInputStream();
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        JSONObject downloads = jsonObject.getJSONObject("downloads");
        return downloads.getJSONObject("application");
    }

    @Override
    public String getChecksum(String version, String build) {
        try {
            JSONObject application = getDownload(version, build);
            return application.getString("sha256");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("SHA-256");
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        String fileName;
        try {
            JSONObject application = getDownload(version, build);
            fileName = application.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String formattedUrl = String.format(downloadUrl, version, build, fileName);
        try {
            URLConnection connection = WebUtils.openConnection(formattedUrl, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String formattedUrl = String.format(versionUrl, version);
        try {
            URLConnection connection = WebUtils.openConnection(formattedUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("builds");
            return Integer.toString(builds.getInt(builds.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefaultVersion() {
        try {
            URLConnection connection = WebUtils.openConnection(projectUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("versions");
            return builds.getString(builds.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
