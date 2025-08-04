package io.github.projectunified.mcserverupdater.updater;

import io.github.projectunified.mcserverupdater.UpdateBuilder;
import io.github.projectunified.mcserverupdater.api.DebugConsumer;
import io.github.projectunified.mcserverupdater.api.FileDigestChecksum;
import io.github.projectunified.mcserverupdater.api.UrlInputStreamUpdater;
import io.github.projectunified.mcserverupdater.util.VersionQuery;
import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;

public class PaperUpdater implements UrlInputStreamUpdater, FileDigestChecksum {
    private final UpdateBuilder updateBuilder;
    private final String projectUrl;
    private final String versionUrl;
    private final String buildUrl;
    private final String version;

    public PaperUpdater(VersionQuery versionQuery, String project) {
        this.updateBuilder = versionQuery.updateBuilder;
        String apiUrl = "https://fill.papermc.io/v3";
        projectUrl = apiUrl + "/projects/" + project;
        versionUrl = projectUrl + "/versions";
        buildUrl = versionUrl + "/%s/builds/latest";

        version = versionQuery.isDefault ? getDefaultVersion() : versionQuery.version;
    }

    private String getDefaultVersion() {
        updateBuilder.debug("Getting default version from " + projectUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(versionUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray builds = jsonObject.getJSONArray("versions");
            JSONObject latestVersion = builds.getJSONObject(0);
            return latestVersion.getJSONObject("version").getString("id");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getDownload() throws IOException {
        String formattedUrl = String.format(buildUrl, version);
        updateBuilder.debug("Getting download from " + formattedUrl);
        URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
        InputStream inputStream = connection.getInputStream();
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        JSONObject downloads = jsonObject.getJSONObject("downloads");
        return downloads.getJSONObject("server:default");
    }

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("SHA-256");
    }

    @Override
    public String getChecksum() {
        try {
            JSONObject application = getDownload();
            JSONObject checksumObject = application.getJSONObject("checksums");
            return checksumObject.getString("sha256");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFileUrl() {
        try {
            JSONObject application = getDownload();
            return application.getString("url");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DebugConsumer getDebugConsumer() {
        return updateBuilder.debugConsumer();
    }
}
