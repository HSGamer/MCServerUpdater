package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.hscore.web.UserAgent;
import me.hsgamer.hscore.web.WebUtils;
import me.hsgamer.mcserverupdater.Utils;
import me.hsgamer.mcserverupdater.api.InputStreamUpdater;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.SimpleChecksum;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URLConnection;

public abstract class JenkinsUpdater implements InputStreamUpdater, SimpleChecksum, LatestBuild {
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
        return String.join("||", version, build, getJob(version), jenkinsUrl);
    }

    @Override
    public String getFileChecksum(File file) throws Exception {
        File checksumFile = getChecksumFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(checksumFile))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
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
    public boolean update(File file, String version, String build) throws IOException {
        boolean success = InputStreamUpdater.super.update(file, version, build);
        if (success) {
            File checksumFile = getChecksumFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(checksumFile))) {
                writer.write(getChecksum(version, build));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    private File getChecksumFile() throws IOException {
        File file = new File("jenkins.build");
        if (!file.exists() && !Utils.createFile(file)) {
            throw new IOException("Can't create file " + file.getAbsolutePath());
        }
        return file;
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
