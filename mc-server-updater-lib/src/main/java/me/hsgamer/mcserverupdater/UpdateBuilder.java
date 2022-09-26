package me.hsgamer.mcserverupdater;

import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.mcserverupdater.api.Checksum;
import me.hsgamer.mcserverupdater.api.LatestBuild;
import me.hsgamer.mcserverupdater.api.Updater;
import me.hsgamer.mcserverupdater.updater.*;
import me.hsgamer.mcserverupdater.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Where to create the update process
 */
public final class UpdateBuilder {
    private static final Map<String, Function<UpdateBuilder, Updater>> UPDATERS = new CaseInsensitiveStringHashMap<>();

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
        registerUpdater(FlameCordUpdater::new, "flamecord");
        registerUpdater(FlamePaperUpdater::new, "flamepaper");
        registerUpdater(updateBuilder -> new FabricUpdater(updateBuilder, true), "fabricmc", "fabric");
        registerUpdater(updateBuilder -> new FabricUpdater(updateBuilder, false), "fabricmc-dev", "fabric-dev");
        registerUpdater(() -> new SpongeUpdater(false, false), "spongevanilla");
        registerUpdater(() -> new SpongeUpdater(false, true), "spongevanilla-recommended");
        registerUpdater(() -> new SpongeUpdater(true, false), "spongeforge");
        registerUpdater(() -> new SpongeUpdater(true, true), "spongeforge-recommended");
        registerUpdater(TitaniumUpdater::new, "titanium", "titaniummc");
    }

    private final String project;
    private String version = "default";
    private String build = "latest";
    private File outputFile = new File("server.jar");
    private File workingDirectory = new File(".");
    private boolean checkOnly = false;
    private ChecksumSupplier checksumSupplier = () -> "";
    private ChecksumConsumer checksumConsumer = s -> {
    };

    private UpdateBuilder(String project) {
        this.project = project;
    }

    /**
     * Register a updater
     *
     * @param updater the updater
     * @param names   the names
     */
    public static void registerUpdater(Function<UpdateBuilder, Updater> updater, String... names) {
        for (String name : names) {
            UPDATERS.put(name, updater);
        }
    }

    /**
     * Register a updater
     *
     * @param updater the updater
     * @param names   the names
     */
    public static void registerUpdater(Supplier<Updater> updater, String... names) {
        registerUpdater(builder -> updater.get(), names);
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
     * Set the working directory
     *
     * @param workingDirectory the working directory
     * @return the update process
     */
    public UpdateBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    /**
     * Set the working directory
     *
     * @param workingDirectory the working directory
     * @return the update process
     */
    public UpdateBuilder workingDirectory(String workingDirectory) {
        return workingDirectory(new File(workingDirectory));
    }

    /**
     * Set the checksum supplier
     *
     * @param checksumSupplier the checksum supplier
     * @return the update process
     */
    public UpdateBuilder checksumSupplier(ChecksumSupplier checksumSupplier) {
        this.checksumSupplier = checksumSupplier;
        return this;
    }

    /**
     * Set the checksum consumer
     *
     * @param checksumConsumer the checksum consumer
     * @return the update process
     */
    public UpdateBuilder checksumConsumer(ChecksumConsumer checksumConsumer) {
        this.checksumConsumer = checksumConsumer;
        return this;
    }

    /**
     * Set the checksum file
     *
     * @param checksumFile the checksum file
     * @return the update process
     */
    public UpdateBuilder checksumFile(File checksumFile) {
        checksumSupplier(() -> {
            if (!checksumFile.exists() && Utils.isFailedToCreateFile(checksumFile)) {
                return "";
            }
            return Utils.getString(checksumFile);
        });
        checksumConsumer(checksum -> {
            if (checksumFile.exists() || !Utils.isFailedToCreateFile(checksumFile)) {
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
        return checksumFile(new File(workingDirectory, checksumFile));
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
     * Get the checksum consumer
     *
     * @return the checksum consumer
     */
    public ChecksumConsumer checksumConsumer() {
        return checksumConsumer;
    }

    /**
     * Get the checksum supplier
     *
     * @return the checksum supplier
     */
    public ChecksumSupplier checksumSupplier() {
        return checksumSupplier;
    }

    /**
     * Get the working directory
     *
     * @param create if the directory should be created if it doesn't exist
     * @return the working directory
     */
    public File workingDirectory(boolean create) {
        if (!workingDirectory.exists() && create) {
            workingDirectory.mkdirs();
        }
        return workingDirectory;
    }

    /**
     * Get the working directory
     *
     * @return the working directory
     */
    public File workingDirectory() {
        return workingDirectory(true);
    }

    /**
     * Execute the update process
     *
     * @return the update status
     */
    public CompletableFuture<UpdateStatus> executeAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Updater update = Optional.ofNullable(UPDATERS.get(project)).map(f -> f.apply(this)).orElse(null);
            if (update == null) {
                return UpdateStatus.NO_PROJECT;
            }
            if ("default".equalsIgnoreCase(version) || "latest".equalsIgnoreCase(version)) {
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

    public interface ChecksumSupplier {
        String get() throws IOException;
    }

    public interface ChecksumConsumer {
        void accept(String checksum) throws IOException;
    }
}
