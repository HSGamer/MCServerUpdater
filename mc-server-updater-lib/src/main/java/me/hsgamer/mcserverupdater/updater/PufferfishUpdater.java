package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

public class PufferfishUpdater extends JenkinsUpdater {
    public PufferfishUpdater() {
        super("https://ci.pufferfish.host/");
    }

    @Override
    public String getJob(String version) {
        String job = "Pufferfish-";
        if (version.contains("purpur")) {
            job += "Purpur-";
        }
        if (version.contains("1.17")) {
            job += "1.17";
        } else if (version.contains("1.18")) {
            job += "1.18";
        } else {
            return "INVALID";
        }
        return job;
    }

    @Override
    public String getArtifactName(String version, String build) {
        String path = "build/libs/";
        if (version.contains("1.17")) {
            path += "Pufferfish-1.17.1-R0.1-SNAPSHOT.jar";
        } else if (version.contains("1.18")) {
            path += "pufferfish-paperclip-1.18.1-R0.1-SNAPSHOT-reobf.jar";
        } else {
            return "INVALID";
        }
        return path;
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
