package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.mcserverupdater.api.Checksum;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.Updater;
import me.hsgamer.mcserverupdater.updater.*;
import me.hsgamer.mcserverupdater.util.Utils;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class UpdateBuilder {
    private static final Map<String, Supplier<Updater>> UPDATERS = new CaseInsensitiveStringHashMap<>();

    static {
        registerUpdater(() -> new PaperUpdater("paper"), "paper", "papermc", "paperspigot");
        registerUpdater(() -> new PaperUpdater("travertine"), "travertine");
        registerUpdater(() -> new PaperUpdater("waterfall"), "waterfall");
        registerUpdater(() -> new PaperUpdater("velocity"), "velocity");
        registerUpdater(PurpurUpdater::new, "purpur", "purpurmc");
        registerUpdater(AirplaneUpdater::new, "airplane");
        registerUpdater(BungeeCordUpdater::new, "bungeecord", "bungee");
        registerUpdater(SpigotUpdater::new, "spigot", "spigotmc");
        registerUpdater(PatinaUpdater::new, "patina", "patinamc");
        registerUpdater(NachoSpigotUpdater::new, "nacho", "nachospigot");
        registerUpdater(BurritoSpigotUpdater::new, "burrito", "burritospigot");
    }

    private final Updater updater;
    private String version = "default";
    private String build = "latest";
    private File outputFile = new File("server.jar");

    private UpdateBuilder(String project) {
        if (UPDATERS.containsKey(project)) {
            this.updater = UPDATERS.get(project).get();
        } else {
            this.updater = null;
        }
    }

    public static void registerUpdater(Supplier<Updater> updater, String... names) {
        for (String name : names) {
            UPDATERS.put(name, updater);
        }
    }

    public static Set<String> getUpdaterNames() {
        return UPDATERS.keySet();
    }

    public static UpdateBuilder updateProject(String project) {
        return new UpdateBuilder(project);
    }

    public UpdateBuilder version(String version) {
        if (updater != null && version.equalsIgnoreCase("default")) {
            this.version = updater.getDefaultVersion();
        } else {
            this.version = version;
        }
        return this;
    }

    public UpdateBuilder build(String build) {
        if (updater != null && version != null && build.equalsIgnoreCase("latest") && updater instanceof LatestBuild) {
            this.build = ((LatestBuild) updater).getLatestBuild(version);
        } else {
            this.build = build;
        }
        return this;
    }

    public UpdateBuilder outputFile(File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public UpdateBuilder outputFile(String outputFile) {
        this.outputFile = new File(outputFile);
        return this;
    }

    public UpdateStatus execute() throws Exception {
        if (updater == null) {
            return UpdateStatus.NO_PROJECT;
        }
        if (version == null) {
            return UpdateStatus.NO_VERSION;
        }
        if (build == null) {
            return UpdateStatus.NO_BUILD;
        }

        if (outputFile.exists()) {
            if (updater instanceof Checksum) {
                Checksum checksum = (Checksum) updater;
                if (checksum.checksum(outputFile, version, build)) {
                    return UpdateStatus.UP_TO_DATE;
                }
            }
        } else {
            if (Utils.isFailedToCreateFile(outputFile)) {
                return UpdateStatus.FILE_FAILED;
            }
        }

        return updater.update(outputFile, version, build)
                ? UpdateStatus.SUCCESS
                : UpdateStatus.FAILED;
    }
}
