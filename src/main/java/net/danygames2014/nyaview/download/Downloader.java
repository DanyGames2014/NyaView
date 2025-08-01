package net.danygames2014.nyaview.download;

import net.danygames2014.nyaview.ActionResult;
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
    public static ActionResult download(DownloadCatalog.DownloadEntry dl) {
        NyaView.LOGGER.info("Downloading " + dl.getName());

        URL url = dl.downloadable.getUrl();
        if (url == null) {
            NyaView.LOGGER.error("Download Failed, error while forming the URL");
            return new ActionResult(30, "Download Failed, Error while forming the URL");
        }

        String tempPath = Util.getProgramPath() + "/temp/" + dl.getId() + ".jar";

        if (!fetchFile(url, Path.of(tempPath))) {
            NyaView.LOGGER.error("Download Failed, error while fetching the file");
            return new ActionResult(31, "Download Failed, Error while fetching the file");
        }

        if (!extractFile(tempPath, Util.getMappingPath(dl.getPath()).toString(), dl.mappingFilePath)) {
            NyaView.LOGGER.error("Download Failed, error while extracting the file");
            return new ActionResult(32, "Download Failed, Error while extracting the file");
        }

        if (!installMappings(dl)) {
            NyaView.LOGGER.error("Download Failed, error while installing mappings");
            return new ActionResult(33, "Download Failed, Error while installing mappings");
        }
        
        return new  ActionResult(0, "Download Successful");
    }

    public static boolean installMappings(DownloadCatalog.DownloadEntry dl) {
        if (dl.mappings != null) {
            Mappings mappings = dl.mappings;
            NyaView.LOGGER.info("Installing mappings " + mappings.name);
            if (!NyaView.profileManager.activeProfile.addMappings(mappings.id, mappings).successful()) {
                NyaView.LOGGER.warn("Mappings " + mappings.name + " already exist");
            }
            return true;
        }

        if (dl.intermediary != null) {
            Intermediary intermediary = dl.intermediary;
            NyaView.LOGGER.info("Installing intermediary " + intermediary.name);
            if (!NyaView.profileManager.activeProfile.addIntermediaries(intermediary.id, intermediary).successful()) {
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
