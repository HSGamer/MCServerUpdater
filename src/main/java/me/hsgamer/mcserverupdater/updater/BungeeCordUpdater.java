package me.hsgamer.mcserverupdater.updater;

public class BungeeCordUpdater extends JenkinsUpdater {
    public BungeeCordUpdater() {
        super("https://ci.md-5.net/");
    }

    @Override
    public String getJob(String version) {
        return "BungeeCord";
    }

    @Override
    public String getArtifactName(String version, String build) {
        return "bootstrap/target/BungeeCord.jar";
    }

    @Override
    public String getDefaultVersion() {
        return "1.17.1";
    }
}
