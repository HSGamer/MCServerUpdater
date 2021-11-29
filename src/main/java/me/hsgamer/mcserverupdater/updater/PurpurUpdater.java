package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.api.FileDigestChecksum;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Locale;

public class PurpurUpdater implements FileDigestChecksum, InputStreamUpdater {
    private static final String URL = "https://api.pl3x.net/v2/purpur/";
    private static final String VERSION_URL = URL + "%s/";
    private static final String BUILD_URL = VERSION_URL + "%s/";
    private static final String DOWNLOAD_URL = BUILD_URL + "download";

    @Override
    public MessageDigest getMessageDigest() throws Exception {
        return MessageDigest.getInstance("MD5");
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        build = build.toLowerCase(Locale.ROOT);
        String url = String.format(DOWNLOAD_URL, version, build);
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getChecksum(String version, String build) {
        build = build.toLowerCase(Locale.ROOT);
        String url = String.format(BUILD_URL, version, build);
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            return jsonObject.getString("md5");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
