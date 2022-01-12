package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.Locale;

public class AirplaneUpdater implements InputStreamUpdater {

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = build.toLowerCase(Locale.ROOT).contains("purpur")
                ? "https://airplane.gg/dl/launcher-airplanepurpur1.17.1.jar"
                : "https://airplane.gg/dl/launcher-airplane1.17.1.jar";
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
