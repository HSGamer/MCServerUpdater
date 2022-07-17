package me.hsgamer.mcserverupdater.util;

import java.io.IOException;

public class ChecksumUtils {
    private static ChecksumSupplier checksumSupplier = () -> "";
    private static ChecksumConsumer checksumConsumer = s -> {
    };

    private ChecksumUtils() {
        // EMPTY
    }

    public static ChecksumConsumer getChecksumConsumer() {
        return checksumConsumer;
    }

    public static void setChecksumConsumer(ChecksumConsumer consumer) {
        checksumConsumer = consumer;
    }

    public static ChecksumSupplier getChecksumSupplier() {
        return checksumSupplier;
    }

    public static void setChecksumSupplier(ChecksumSupplier supplier) {
        checksumSupplier = supplier;
    }

    public interface ChecksumSupplier {
        String get() throws IOException;
    }

    public interface ChecksumConsumer {
        void accept(String checksum) throws IOException;
    }
}
