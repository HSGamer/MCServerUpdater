package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.Utils;
import me.hsgamer.mcserverupdater.api.DigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;

public class PaperUpdater implements InputStreamUpdater, DigestChecksum, LatestBuild {
    private static final String URL = "https://papermc.io/api/v2/projects/paper/";
    private static final String VERSION_URL = URL + "versions/%s/";
    private static final String BUILD_URL = VERSION_URL + "builds/%s/";

    @Override
    public String getChecksum(String version, String build) {
        String buildUrl = String.format(BUILD_URL, version, build);
        try {
            URLConnection connection = WebUtils.openConnection(buildUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            String content = Utils.getContent(inputStream);
            JSONObject jsonObject = new JSONObject(content);
            JSONObject downloads = jsonObject.getJSONObject("downloads");
            JSONObject application = downloads.getJSONObject("application");
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
        String buildUrl = String.format(BUILD_URL, version, build);
        String fileName;
        try {
            URLConnection connection = WebUtils.openConnection(buildUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            String content = Utils.getContent(inputStream);
            JSONObject jsonObject = new JSONObject(content);
            JSONObject downloads = jsonObject.getJSONObject("downloads");
            JSONObject application = downloads.getJSONObject("application");
            fileName = application.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String downloadUrl = buildUrl + "downloads/" + fileName;
        try {
            URLConnection connection = WebUtils.openConnection(downloadUrl, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String versionUrl = String.format(VERSION_URL, version);
        try {
            URLConnection connection = WebUtils.openConnection(versionUrl, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            String content = Utils.getContent(inputStream);
            JSONObject jsonObject = new JSONObject(content);
            JSONArray builds = jsonObject.getJSONArray("builds");
            return Integer.toString(builds.getInt(builds.length() - 1));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
