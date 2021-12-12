package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class NachoSpigotUpdater implements InputStreamUpdater {

    @Override
    public InputStream getInputStream(String version, String build) {
        try {
            String url = "https://nightly.link/CobbleSword/NachoSpigot/workflows/build-nachospigot/master/NachoSpigot-server.zip";
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            String fileName = "NachoSpigot.jar";
            return Utils.getExtractedInputStream(inputStream, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }
}
