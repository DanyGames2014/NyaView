package net.danygames2014.nyaview.download;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.Util;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.Mappings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Downloader {
    public static void download(DownloadCatalog.DownloadEntry dl) {
        NyaView.LOGGER.info("Downloading " + dl.getName());

        URL url = dl.downloadable.getUrl();
        if (url == null) {
            NyaView.LOGGER.error("Download Failed, error while forming the URL");
            return;
        }

        String tempPath = Util.getProgramPath() + "/temp/" + dl.getId() + ".jar";

        if (!fetchFile(dl.downloadable.getUrl(), Path.of(tempPath))) {
            NyaView.LOGGER.error("Download Failed, error while fetching the file");
            return;
        }

        if (!extractFile(tempPath, Util.getMappingPath(dl.getPath()).toString(), "mappings/mappings.tiny")) {
            NyaView.LOGGER.error("Download Failed, error while extracting the file");
            return;
        }

        if (!installMappings(dl)) {
            NyaView.LOGGER.error("Download Failed, error while installing mappings");
            return;
        }

    }

    public static boolean installMappings(DownloadCatalog.DownloadEntry dl) {
        if (dl.mappings != null) {
            Mappings mappings = dl.mappings;
            NyaView.LOGGER.info("Installing mappings " + mappings.name);
            if (!NyaView.config.addMappings(mappings.id, mappings)) {
                NyaView.LOGGER.warn("Mappings " + mappings.name + " already exist");
            }
            return true;
        }

        if (dl.intermediary != null) {
            Intermediary intermediary = dl.intermediary;
            NyaView.LOGGER.info("Installing intermediary " + intermediary.name);
            if (!NyaView.config.addIntermediaries(intermediary.id, intermediary)) {
                NyaView.LOGGER.warn("Intermediaries " + intermediary.id + " already exist");
            }
            return true;
        }

        NyaView.LOGGER.warn("No mappings found for " + dl.downloadable.getUrl());
        return false;
    }

    public static boolean extractFile(String archivePath, String outputPath, String fileName) {
        NyaView.LOGGER.info("Extracting mappings from " + archivePath + " to " + outputPath);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(fileName)) {
                    Files.createDirectories(Path.of(outputPath).getParent());

                    try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    zis.closeEntry();
                    NyaView.LOGGER.info("Extracted " + fileName + " to " + outputPath);
                    return true;
                }
            }
        } catch (IOException e) {
            NyaView.LOGGER.error("Error while extracting mappings", e);
        }

        return false;
    }

    public static boolean fetchFile(URL url, Path path) {
        NyaView.LOGGER.info("Fetching File " + url + " to " + path);
        try (InputStream in = url.openStream()) {
            try {
                Files.createDirectories(path);
            } catch (FileAlreadyExistsException ignored) {

            }
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            NyaView.LOGGER.error(e.getMessage(), e);
            return false;
        }
    }
}
