package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
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

public class PurpurUpdater implements FileDigestChecksum, InputStreamUpdater, LatestBuild {
    private static final String URL = "https://api.purpurmc.org/v2/purpur/";
    private static final String VERSION_URL = URL + "%s/";
    private static final String BUILD_URL = VERSION_URL + "%s/";
    private static final String DOWNLOAD_URL = BUILD_URL + "download";
    private final UpdateBuilder updateBuilder;

    public PurpurUpdater(UpdateBuilder updateBuilder) {
        this.updateBuilder = updateBuilder;
    }

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("MD5");
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = String.format(DOWNLOAD_URL, version, build);
        updateBuilder.debug("Downloading from " + url);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getChecksum(String version, String build) {
        String url = String.format(BUILD_URL, version, build);
        updateBuilder.debug("Getting checksum from " + url);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            return jsonObject.getString("md5");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefaultVersion() {
        updateBuilder.debug("Getting default version from " + URL);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(URL));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("versions");
            return builds.getString(builds.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }

    @Override
    public String getLatestBuild(String version) {
        String url = String.format(VERSION_URL, version);
        updateBuilder.debug("Getting latest build from " + url);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject builds = jsonObject.getJSONObject("builds");
            return builds.getString("latest");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
