package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class FlamePaperUpdater extends JenkinsUpdater {
    public FlamePaperUpdater() {
        super("https://ci.2lstudios.dev/");
    }

    @Override
    public String[] getJob(String version) {
        return new String[]{"FlamePaper"};
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(Pattern.quote("FlamePaper.jar"));
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }
}

