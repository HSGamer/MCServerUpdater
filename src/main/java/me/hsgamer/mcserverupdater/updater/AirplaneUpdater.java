package me.hsgamer.mcserverupdater.updater;

import me.hsgamer.mcserverupdater.MCServerUpdater;

public class AirplaneUpdater extends JenkinsUpdater {
    public AirplaneUpdater() {
        super("https://ci.tivy.ca/");
        MCServerUpdater.LOGGER.warning("Airplane will be deprecated soon, please consider using another updater.");
    }

    @Override
    public String getJob(String version) {
        if (version.contains("1.17")) {
            return "Airplane-1.17";
        } else if (version.contains("1.16")) {
            return "Airplane-1.16";
        } else {
            return "Airplane-" + version;
        }
    }

    @Override
    public String getArtifactName(String version, String build) {
        return "launcher-airplane.jar";
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
