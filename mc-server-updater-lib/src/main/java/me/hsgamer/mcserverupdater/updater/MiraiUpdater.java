package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class MiraiUpdater extends GithubReleaseUpdater {
    public MiraiUpdater() {
        super("etil2jz/Mirai", true);
    }

    @Override
    public Pattern getArtifactPattern(String version, String build) {
        return Pattern.compile(".*\\.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "1.18.2";
    }
}
