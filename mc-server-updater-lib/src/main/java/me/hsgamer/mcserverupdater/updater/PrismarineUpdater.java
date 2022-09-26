package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.UpdateBuilder;
import me.hsgamer.mcserverupdater.api.GithubReleaseUpdater;

import java.util.regex.Pattern;

public class PrismarineUpdater extends GithubReleaseUpdater {
    private final UpdateBuilder updateBuilder;

    public PrismarineUpdater(UpdateBuilder updateBuilder) {
        super("PrismarineTeam/Prismarine", false);
        this.updateBuilder = updateBuilder;
    }

    @Override
    public UpdateBuilder getUpdateBuilder() {
        return updateBuilder;
    }

    @Override
    public Pattern getArtifactPattern(String version, String build) {
        return Pattern.compile(".+paperclip.+reobf.jar");
    }

    @Override
    public String getDefaultVersion() {
        return "";
    }
}
