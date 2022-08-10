package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.LocalChecksum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.net.URLConnection;

public class FabricUpdater implements InputStreamUpdater, LocalChecksum, LatestBuild {
    private static final String BASE_URL = "https://meta.fabricmc.net/v2/versions";
    private static final String LOADER_URL = BASE_URL + "/loader";
    private static final String DOWNLOAD_URL = LOADER_URL + "/%s/%s/%s/server/jar";
    private static final String INSTALLER_URL = BASE_URL + "/installer";
    private final UpdateBuilder updateBuilder;
    private final boolean isStable;

    public FabricUpdater(UpdateBuilder updateBuilder, boolean isStable) {
        this.updateBuilder = updateBuilder;
        this.isStable = isStable;
    }

    private String getLatestVersion(String url) {
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            InputStream inputStream = connection.getInputStream();
            JSONArray jsonArray = new JSONArray(new JSONTokener(inputStream));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (isStable && !jsonObject.getBoolean("stable")) {
                    continue;
                }
                return jsonObject.getString("version");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLatestLoaderVersion() {
        return getLatestVersion(LOADER_URL);
    }

    private String getLatestInstallerVersion() {
        return getLatestVersion(INSTALLER_URL);
    }

    private String getLatestDownloadUrl(String serverVersion, String loaderVersion, String installerVersion) {
        return String.format(DOWNLOAD_URL, serverVersion, loaderVersion, installerVersion);
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        String[] split = build.split(";");
        if (split.length != 2) {
            return null;
        }
        String loaderVersion = split[0];
        String installerVersion = split[1];
        String downloadUrl = getLatestDownloadUrl(version, loaderVersion, installerVersion);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(downloadUrl));
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String loaderVersion = getLatestLoaderVersion();
        if (loaderVersion == null) {
            return null;
        }
        String installerVersion = getLatestInstallerVersion();
        if (installerVersion == null) {
            return null;
        }
        return loaderVersion + ";" + installerVersion;
    }

    @Override
    public String getChecksum(String version, String build) {
        return "fabric-" + version + "-" + build;
    }

    @Override
    public String getDefaultVersion() {
        return "1.18.2";
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }
}
