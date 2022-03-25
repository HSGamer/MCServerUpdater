package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

public class FlamePaperUpdater extends JenkinsUpdater {
    public FlamePaperUpdater() {
        super("https://ci.2lstudios.dev/");
    }

    @Override
    public String getJob(String version) {
        return "FlamePaper";
    }

    @Override
    public String getArtifactName(String version, String build) {
        return "FlamePaper.jar";
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }
}
