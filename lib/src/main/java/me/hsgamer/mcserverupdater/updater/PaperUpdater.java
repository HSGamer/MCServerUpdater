package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.logger.common.Logger;
import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;

public class PaperUpdater implements InputStreamUpdater, FileDigestChecksum {
    private final UpdateBuilder updateBuilder;
    private final String version;
    private final String build;
    private final String projectUrl;
    private final String versionUrl;
    private final String buildUrl;
    private final String downloadUrl;

    public PaperUpdater(VersionQuery versionQuery, String project) {
        this.updateBuilder = versionQuery.updateBuilder;
        projectUrl = String.format("https://api.papermc.io/v2/projects/%s/", project);
        versionUrl = projectUrl + "versions/%s/";
        buildUrl = versionUrl + "builds/%s/";
        downloadUrl = buildUrl + "downloads/%s";

        version = versionQuery.isDefault ? getDefaultVersion() : versionQuery.version;
        build = getBuild();
    }

    private String getDefaultVersion() {
        updateBuilder.debug("Getting default version from " + projectUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(projectUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("versions");
            return builds.getString(builds.length() - 1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getBuild() {
        String formattedUrl = String.format(versionUrl, version);
        updateBuilder.debug("Getting latest build from " + formattedUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("builds");
            return Integer.toString(builds.getInt(builds.length() - 1));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getDownload() throws IOException {
        String formattedUrl = String.format(buildUrl, version, build);
        updateBuilder.debug("Getting download from " + formattedUrl);
        URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
        InputStream inputStream = connection.getInputStream();
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        JSONObject downloads = jsonObject.getJSONObject("downloads");
        return downloads.getJSONObject("application");
    }

    @Override
    public String getChecksum() {
        try {
            JSONObject application = getDownload();
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
    public InputStream getInputStream() {
        String fileName;
        try {
            JSONObject application = getDownload();
            fileName = application.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String formattedUrl = String.format(downloadUrl, version, build, fileName);
        updateBuilder.debug("Getting input stream from " + formattedUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Logger getLogger() {
        return updateBuilder.logger();
    }
}
