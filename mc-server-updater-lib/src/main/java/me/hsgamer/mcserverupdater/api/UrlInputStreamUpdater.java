package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public interface UrlInputStreamUpdater extends InputStreamUpdater {
    String getFileUrl(String version, String build);

    @Override
    default InputStream getInputStream(String version, String build) {
        String url = getFileUrl(version, build);
        getUpdateBuilder().debug("Getting input stream from " + url);
        if (url == null) {
            return null;
        }
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            return connection.getInputStream();
        } catch (IOException e) {
            getUpdateBuilder().debug(e);
            return null;
        }
    }
}
