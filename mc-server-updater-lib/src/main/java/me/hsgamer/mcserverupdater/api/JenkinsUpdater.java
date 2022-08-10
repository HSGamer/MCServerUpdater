package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.regex.Pattern;

public abstract class JenkinsUpdater implements LocalChecksum, InputStreamUpdater, LatestBuild {
    private final String jenkinsUrl;

    protected JenkinsUpdater(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
    }

    public abstract String[] getJob(String version);

    public abstract Pattern getArtifactRegex(String version, String build);

    @Override
    public String getChecksum(String version, String build) {
        return String.join("||", version, build, String.join("_", getJob(version)), getJenkinsUrl());
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = getArtifactUrl(version, build);
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(url));
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getLatestBuild(String version) {
        String url = getJobUrl(version);
        String api = url + "api/json";
        String treeUrl = api + "?tree=lastSuccessfulBuild[number]";
        try {
            URLConnection connection = UserAgent.CHROME.assignToConnection(WebUtils.createConnection(treeUrl));
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
            JSONObject build = jsonObject.getJSONObject("lastSuccessfulBuild");
            return Integer.toString(build.getInt("number"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public String getJobUrl(String version) {
        String[] job = getJob(version);
        StringBuilder builder = new StringBuilder();
        builder.append(jenkinsUrl);
        for (String s : job) {
            builder.append("job/").append(s).append("/");
        }
        return builder.toString();
    }

    public String getArtifactUrl(String version, String build) {
        Pattern artifactRegex = getArtifactRegex(version, build);
        String jobUrl = getJobUrl(version);
        String artifactListUrl = jobUrl + build + "/api/json?tree=artifacts[fileName,relativePath]";
        String artifact = "INVALID";
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
            e.printStackTrace();
        }
        String artifactUrl = jobUrl + "%s/artifact/%s";
        return String.format(artifactUrl, build, artifact);
    }
}
