package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.util.VersionQuery;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ForgeUpdater implements InputStreamUpdater, FileDigestChecksum {
    private static final String LOADER_URL = "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";
    private static final String DOWNLOAD_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s/forge-%1$s-universal.jar";
    private final UpdateBuilder updateBuilder;
    private final Boolean isRecommended;
    private final String build;
    private final String version;

    public ForgeUpdater(VersionQuery versionQuery, boolean isRecommended) {
        this.updateBuilder = versionQuery.updateBuilder;
        this.isRecommended = isRecommended;
        
        this.version = versionQuery.isLatest ? getDefaultVersion() : versionQuery.version;
        this.build = getBuild();
    }

    private String getBuild() {
        String build;
        updateBuilder.debug("Getting latest build from " + LOADER_URL);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(LOADER_URL));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject promos = jsonObject.getJSONObject("promos");
            if (isRecommended && promos.getBoolean(version + "-recommended")) {
                build = promos.getString(version + "-recommended");
            } else {
                build = promos.getString(version + "-latest");
            }
            return build;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDownload() {
        return String.format(DOWNLOAD_URL, version + "-" + build);
    }

    public String getDefaultVersion() {
        List<String> versionList = new ArrayList<>();
        updateBuilder.debug("Getting default version from " + LOADER_URL);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(LOADER_URL));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject promos = jsonObject.getJSONObject("promos");
            Iterator<?> keys = promos.keys();
            while(keys.hasNext()) {
                String key = (String) keys.next();
                if (key.contains("recommended")) {
                    versionList.add(key);
                }
            }
            Collections.sort(versionList, Collections.reverseOrder());
            return versionList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getChecksum() {
        String formattedUrl = getDownload() + ".sha256";
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
            InputStream inputStream = connection.getInputStream();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
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
        String formattedUrl = getDownload();
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
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}