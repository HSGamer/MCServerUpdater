package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.JenkinsUpdater;

import java.util.regex.Pattern;

public class MiraiUpdater extends JenkinsUpdater {

    public MiraiUpdater() {
        super("https://ci.codemc.io/");
    }

    @Override
    public String getDefaultVersion() {
        return "1.19";
    }

    @Override
    public String[] getJob(String version) {
        if (version.startsWith("1.19")) {
            return new String[]{"etil2jz", "Mirai-1.19"};
        }
        return new String[0];
    }

    @Override
    public Pattern getArtifactRegex(String version, String build) {
        return Pattern.compile(".*\\.jar");
    }
}
