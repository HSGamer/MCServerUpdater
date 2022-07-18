package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.mcserverupdater.api.Checksum;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.Updater;
import me.hsgamer.mcserverupdater.updater.*;
import me.hsgamer.mcserverupdater.util.ChecksumUtils;
import me.hsgamer.mcserverupdater.util.Utils;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * Where to create the update process
 */
public final class UpdateBuilder {
    private static final Map<String, Supplier<Updater>> UPDATERS = new CaseInsensitiveStringHashMap<>();

    static {
        registerUpdater(() -> new PaperUpdater("paper"), "paper", "papermc", "paperspigot");
        registerUpdater(() -> new PaperUpdater("travertine"), "travertine");
        registerUpdater(() -> new PaperUpdater("waterfall"), "waterfall");
        registerUpdater(() -> new PaperUpdater("velocity"), "velocity");
        registerUpdater(PurpurUpdater::new, "purpur", "purpurmc");
        registerUpdater(BungeeCordUpdater::new, "bungeecord", "bungee");
        registerUpdater(SpigotUpdater::new, "spigot", "spigotmc");
        registerUpdater(PatinaUpdater::new, "patina", "patinamc");
        registerUpdater(PufferfishUpdater::new, "pufferfish");
        registerUpdater(PearlUpdater::new, "pearl");
        registerUpdater(WindSpigotUpdater::new, "windspigot", "wind");
        registerUpdater(MiraiUpdater::new, "mirai");
        registerUpdater(() -> new FabricUpdater(true), "fabricmc", "fabric");
        registerUpdater(() -> new FabricUpdater(false), "fabricmc-dev", "fabric-dev");
        registerUpdater(() -> new SpongeUpdater(false, false), "spongevanilla-dev");
        registerUpdater(() -> new SpongeUpdater(false, true), "spongevanilla");
        registerUpdater(() -> new SpongeUpdater(true, false), "spongeforge-dev");
        registerUpdater(() -> new SpongeUpdater(true, true), "spongeforge");
    }

    private final Updater updater;
    private String version = "default";
    private String build = "latest";
    private File outputFile = new File("server.jar");
    private boolean checkOnly = false;

    private UpdateBuilder(String project) {
        this.updater = Optional.ofNullable(UPDATERS.get(project)).map(Supplier::get).orElse(null);
    }

    /**
     * Register a updater
     *
     * @param updater the updater
     * @param names   the names
     */
    public static void registerUpdater(Supplier<Updater> updater, String... names) {
        for (String name : names) {
            UPDATERS.put(name, updater);
        }
    }

    /**
     * Get the names of available updaters
     *
     * @return the names
     */
    public static Set<String> getUpdaterNames() {
        return UPDATERS.keySet();
    }

    /**
     * Create the update process
     *
     * @param project the project
     * @return the update process
     */
    public static UpdateBuilder updateProject(String project) {
        return new UpdateBuilder(project);
    }

    /**
     * Set the version
     *
     * @param version the version
     * @return the update process
     */
    public UpdateBuilder version(String version) {
        this.version = version;
        return this;
    }

    /**
     * Set the build
     *
     * @param build the build
     * @return the update process
     */
    public UpdateBuilder build(String build) {
        this.build = build;
        return this;
    }

    /**
     * Set the output file
     *
     * @param outputFile the output file
     * @return the update process
     */
    public UpdateBuilder outputFile(File outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    /**
     * Set the output file
     *
     * @param outputFile the output file
     * @return the update process
     */
    public UpdateBuilder outputFile(String outputFile) {
        return outputFile(new File(outputFile));
    }

    /**
     * Set the checksum supplier
     *
     * @param checksumSupplier the checksum supplier
     * @return the update process
     */
    public UpdateBuilder checksumSupplier(ChecksumUtils.ChecksumSupplier checksumSupplier) {
        ChecksumUtils.setChecksumSupplier(checksumSupplier);
        return this;
    }

    /**
     * Set the checksum consumer
     *
     * @param checksumConsumer the checksum consumer
     * @return the update process
     */
    public UpdateBuilder checksumConsumer(ChecksumUtils.ChecksumConsumer checksumConsumer) {
        ChecksumUtils.setChecksumConsumer(checksumConsumer);
        return this;
    }

    /**
     * Set the checksum file
     *
     * @param checksumFile the checksum file
     * @return the update process
     */
    public UpdateBuilder checksumFile(File checksumFile) {
        ChecksumUtils.setChecksumSupplier(() -> Utils.isFailedToCreateFile(checksumFile) ? "" : Utils.getString(checksumFile));
        ChecksumUtils.setChecksumConsumer(checksum -> {
            if (!Utils.isFailedToCreateFile(checksumFile)) {
                Utils.writeString(checksumFile, checksum);
            }
        });
        return this;
    }

    /**
     * Set the checksum file
     *
     * @param checksumFile the checksum file
     * @return the update process
     */
    public UpdateBuilder checksumFile(String checksumFile) {
        return checksumFile(new File(checksumFile));
    }

    /**
     * Set if the update process should only check the checksum
     *
     * @param checkOnly the check only
     * @return the update process
     */
    public UpdateBuilder checkOnly(boolean checkOnly) {
        this.checkOnly = checkOnly;
        return this;
    }

    /**
     * Execute the update process
     *
     * @return the update status
     */
    public CompletableFuture<UpdateStatus> executeAsync() {
        return CompletableFuture.completedFuture(updater).thenApply(update -> {
            if (update == null) {
                return UpdateStatus.NO_PROJECT;
            }
            if ("default".equalsIgnoreCase(version)) {
                version = update.getDefaultVersion();
            }
            if (version == null) {
                return UpdateStatus.NO_VERSION;
            }

            if ("latest".equalsIgnoreCase(build) && update instanceof LatestBuild) {
                build = ((LatestBuild) update).getLatestBuild(version);
            }
            if (build == null) {
                return UpdateStatus.NO_BUILD;
            }

            try {
                if (outputFile.exists()) {
                    if (update instanceof Checksum) {
                        Checksum checksum = (Checksum) update;
                        if (checksum.checksum(outputFile, version, build)) {
                            return UpdateStatus.UP_TO_DATE;
                        } else if (checkOnly) {
                            return UpdateStatus.OUT_OF_DATE;
                        }
                    }
                } else if (Utils.isFailedToCreateFile(outputFile)) {
                    return UpdateStatus.FILE_FAILED;
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }

            try {
                if (update.update(outputFile, version, build)) {
                    if (update instanceof Checksum) {
                        ((Checksum) update).setChecksum(outputFile, version, build);
                    }
                    return UpdateStatus.SUCCESS;
                } else {
                    return UpdateStatus.FAILED;
                }
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).exceptionally(UpdateStatus::unknownError);
    }

    /**
     * Execute the update process
     *
     * @return the status of the process
     */
    public UpdateStatus execute() throws Exception {
        return executeAsync().get();
    }
}
