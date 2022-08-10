package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class FlameCordUpdater extends JenkinsUpdater {
    public FlameCordUpdater() {
        super("https://ci.2lstudios.dev/");
    }

    @Override
    public String[] getJob(String version) {
        return new String[]{"FlameCord"};
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(Pattern.quote("FlameCord.jar"));
    }

    @Override
    public String getDefaultVersion() {
        return "1.8.8";
    }
}
