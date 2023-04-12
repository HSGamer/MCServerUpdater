package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.util.Utils;
import me.hsgamer.mcserverupdater.util.VersionQuery;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgeUpdater implements InputStreamUpdater, FileDigestChecksum {
    private static final String LOADER_URL = "https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json";
    private static final String DOWNLOAD_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s/forge-%1$s-installer.jar";
    private final String downloadUrl;
    private final UpdateBuilder updateBuilder;
    private final Boolean isRecommended;
    private final String build;
    private final String version;

    public ForgeUpdater(VersionQuery versionQuery, boolean isRecommended) {
        this.updateBuilder = versionQuery.updateBuilder;
        this.isRecommended = isRecommended;

        this.version = versionQuery.isDefault ? getDefaultVersion() : versionQuery.version;
        this.build = getBuild();
        this.downloadUrl = getDownloadUrl();
    }

    private String getBuild() {
        updateBuilder.debug("Getting latest build from " + LOADER_URL);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(LOADER_URL));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject promos = jsonObject.getJSONObject("promos");
            return promos.getString(version + (isRecommended ? "-recommended" : "-latest"));
        } catch (Exception e) {
            throw new RuntimeException("Can't get build", e);
        }
    }

    private String getDownloadUrl() {
        try {
            String url = String.format(DOWNLOAD_URL, version + "-" + build);
            // Check if the URL is valid
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            connection.getInputStream().close();
            return url;
        } catch (Exception e) {
            // If not, use the legacy URL
            return String.format(DOWNLOAD_URL, version + "-" + build + "-" + version);
        }
    }

    public String getDefaultVersion() {
        List<String> versionList = new ArrayList<>();
        Pattern versionMatch = Pattern.compile("(\\d+\\.\\d+(\\.\\d+)?)-(.+)");
        updateBuilder.debug("Getting default version from " + LOADER_URL);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(LOADER_URL));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject promos = jsonObject.getJSONObject("promos");
            Iterator<?> keys = promos.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Matcher matcher = versionMatch.matcher(key);
                if (matcher.matches()) {
                    String type = matcher.group(3);
                    if (type != null && type.equals(isRecommended ? "recommended" : "latest")) {
                        versionList.add(matcher.group(1));
                    }
                }
            }
            versionList.sort(Collections.reverseOrder(Utils.getVersionComparator()));
            return versionList.get(0);
        } catch (Exception e) {
            throw new RuntimeException("Can't get default version", e);
        }
    }

    @Override
    public String getChecksum() {
        String formattedUrl = downloadUrl + ".md5";
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(formattedUrl));
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("MD5");
    }

    @Override
    public InputStream getInputStream() {
        String formattedUrl = downloadUrl;
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
    public void debug(String message) {
        updateBuilder.debug(message);
    }
}