package me.hsgamer.mcserverupdater.api;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.Utils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public abstract class JenkinsUpdater implements SimpleFileUpdater, LatestBuild {
    private final String jenkinsUrl;
    private final String jobUrl;
    private final String artifactUrl;

    protected JenkinsUpdater(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
        this.jobUrl = jenkinsUrl + "job/%s/";
        this.artifactUrl = jobUrl + "%s/artifact/%s";
    }

    public abstract String getJob(String version);

    public abstract String getArtifactName(String version, String build);

    @Override
    public String getChecksum(String version, String build) {
        return String.join("||", version, build, getJob(version), getJenkinsUrl());
    }

    @Override
    public InputStream getInputStream(String version, String build) {
        String url = getArtifactUrl(version, build);
        try {
            URLConnection connection = WebUtils.openConnection(url, UserAgent.CHROME);
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public File getChecksumFile() throws IOException {
        return Utils.getFile("jenkins.build");
    }

    @Override
    public String getLatestBuild(String version) {
        String url = getJobUrl(version);
        String api = url + "api/json";
        String treeUrl = api + "?tree=lastSuccessfulBuild[number]";
        try {
            URLConnection connection = WebUtils.openConnection(treeUrl, UserAgent.CHROME);
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
        String job = getJob(version);
        return String.format(jobUrl, job);
    }

    public String getArtifactUrl(String version, String build) {
        String artifact = getArtifactName(version, build);
        String job = getJob(version);
        return String.format(artifactUrl, job, build, artifact);
    }
}
