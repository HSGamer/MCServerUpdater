package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class JenkinsUpdater implements LocalChecksum, InputStreamUpdater {
    private final String jenkinsUrl;
    private final Map<String, String> buildCache = new HashMap<>();

    protected JenkinsUpdater(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
    }

    public abstract String[] getJob(String version);

    public abstract Pattern getArtifactRegex(String version, String build);

    private String getBuild(String version) {
        if (buildCache.containsKey(version)) {
            return buildCache.get(version);
        }

        String url = getJobUrl(version);
        String api = url + "api/json";
        String treeUrl = api + "?tree=lastSuccessfulBuild[number]";
        getUpdateBuilder().debug("Getting latest build from " + treeUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(treeUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject build = jsonObject.getJSONObject("lastSuccessfulBuild");
            String buildNumber = Integer.toString(build.getInt("number"));
            getUpdateBuilder().debug("Latest build: " + buildNumber);
            buildCache.put(version, buildNumber);
            return buildNumber;
        } catch (IOException e) {
            getUpdateBuilder().debug(e);
            return null;
        }
    }

    @Override
    public String getChecksum(String version) {
        return String.join("||", version, getBuild(version), String.join("_", getJob(version)), getJenkinsUrl());
    }

    @Override
    public InputStream getInputStream(String version) {
        String url = getArtifactUrl(version);
        getUpdateBuilder().debug("Downloading " + url);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            return connection.getInputStream();
        } catch (IOException e) {
            getUpdateBuilder().debug(e);
            return null;
        }
    }

    private String getJenkinsUrl() {
        return jenkinsUrl;
    }

    private String getJobUrl(String version) {
        String[] job = getJob(version);
        StringBuilder builder = new StringBuilder();
        builder.append(jenkinsUrl);
        for (String s : job) {
            builder.append("job/").append(s).append("/");
        }
        return builder.toString();
    }

    private String getArtifactUrl(String version) {
        String build = getBuild(version);
        Pattern artifactRegex = getArtifactRegex(version, build);
        String jobUrl = getJobUrl(version);
        String artifactListUrl = jobUrl + build + "/api/json?tree=artifacts[fileName,relativePath]";
        String artifact = "INVALID";
        getUpdateBuilder().debug("Getting artifact from " + artifactListUrl);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(artifactListUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONArray artifacts = jsonObject.getJSONArray("artifacts");
            for (int i = 0; i < artifacts.length(); i++) {
                JSONObject artifactObject = artifacts.getJSONObject(i);
                String fileName = artifactObject.getString("fileName");
                if (artifactRegex.matcher(fileName).matches()) {
                    artifact = artifactObject.getString("relativePath");
                    break;
                }
            }
        } catch (IOException e) {
            getUpdateBuilder().debug(e);
        }
        String artifactUrl = jobUrl + "%s/artifact/%s";
        String formattedArtifactUrl = String.format(artifactUrl, build, artifact);
        getUpdateBuilder().debug("Artifact URL: " + formattedArtifactUrl);
        return formattedArtifactUrl;
    }
}
