package me.hsgamer.mcserverupdater.api;

import java.io.File;

public interface LocalChecksum extends SimpleChecksum, GetUpdateBuilder {
    @Override
    default String getCurrentChecksum(File file) throws Exception {
        return getUpdateBuilder().checksumSupplier().get();
    }

    @Override
    default void setChecksum(File file, String version, String build) throws Exception {
        getUpdateBuilder().checksumConsumer().accept(getChecksum(version, build));
    }
}
