package net.danygames2014.nyaview.download;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.fabricmc.mappingio.format.MappingFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DownloadCatalog {
    public HashMap<String, HashMap<String, DownloadEntry>> catalogues;

    @SuppressWarnings("CollectionAddAllCanBeReplacedWithConstructor")
    public HashMap<String, DownloadEntry> getDownloads(String version) {
        HashMap<String, DownloadEntry> downloads = new HashMap<>();

        // Include entries which are avalible for all versions
        downloads.putAll(catalogues.get("all"));
        
        // Then include version specific entries
        if(catalogues.containsKey(version)) {
            downloads.putAll(catalogues.get(version));
        }
        
        return downloads;
    }
    
    public DownloadCatalog() {
        catalogues = new HashMap<>();

        // All
        HashMap<String, DownloadEntry> all = new HashMap<>();

        all.put("calamus_gen2", new DownloadEntry(
                new MavenDownloadable("https://maven.ornithemc.net", "releases", "net.ornithemc", "calamus-intermediary-gen2") {
                    @Override
                    public URL getUrl() {
                        try {
                            return new URL(getBaseArtifactUrl() + "/b1.7.3/calamus-intermediary-gen2-b1.7.3-v2.jar");
                        } catch (MalformedURLException e) {
                            NyaView.LOGGER.error(e.getMessage(), e);
                            return null;
                        }
                    }
                },
                new Intermediary("calamus_gen2", "Calamus Gen 2", Environment.MERGED, "intermediary", "clientOfficial", "serverOfficial", "calamus_gen2.tiny", MappingFormat.TINY_2_FILE)
        ));

        all.put("feather_gen2", new DownloadEntry(
                new MavenDownloadable("https://maven.ornithemc.net", "releases", "net.ornithemc", "feather-gen2") {
                    @Override
                    public URL getUrl() {
                        String latestVersion = MavenUtil.fetchLatestVersion(baseUrl, repository, groupId, artifactId);
                        if (latestVersion == null) {
                            return null;
                        }

                        String[] split = latestVersion.split("\\+");
                        if (split.length != 2) {
                            return null;
                        }
                        
                        String version = NyaView.config.getDownloadVersion() + "+" + split[1];
                        try {
                            return new URL(getBaseArtifactUrl() + "/" + version + "/feather-gen2-" + version + "-v2.jar");
                        } catch (MalformedURLException e) {
                            NyaView.LOGGER.error(e.getMessage(), e);
                            return null;
                        }
                    }
                },
                new Mappings("feather_gen2", "Feather Gen 2", Environment.MERGED, MappingType.BABRIC, "feather_gen2.tiny", MappingFormat.TINY_2_FILE, "calamus_gen2")
        ));

        catalogues.put("all", all);

        // b1.7.3
        HashMap<String, DownloadEntry> b173 = new HashMap<>();

        b173.put("babric", new DownloadEntry(
                new MavenDownloadable("https://maven.glass-launcher.net", "babric", "babric", "intermediary") {
                    @Override
                    public URL getUrl() {
                        try {
                            return new URL(getBaseArtifactUrl() + "/b1.7.3/intermediary-b1.7.3-v2.jar");
                        } catch (MalformedURLException e) {
                            NyaView.LOGGER.error(e.getMessage(), e);
                            return null;
                        }
                    }
                },
                new Intermediary("babric", "Babric", Environment.MERGED, "intermediary", "client", "server", "babric.tiny", MappingFormat.TINY_2_FILE)
        ));

        b173.put("biny", new DownloadEntry(
                new MavenDownloadable("https://maven.glass-launcher.net", "releases", "net.glasslauncher", "biny") {
                    @Override
                    public URL getUrl() {
                        String version = MavenUtil.fetchLatestVersion(baseUrl, repository, groupId, artifactId);
                        try {
                            return new URL(getBaseArtifactUrl() + "/" + version + "/biny-" + version + "-v2.jar");
                        } catch (MalformedURLException e) {
                            NyaView.LOGGER.error(e.getMessage(), e);
                            return null;
                        }
                    }
                },
                new Mappings("biny", "BINY", Environment.MERGED, MappingType.BABRIC, "biny.tiny", MappingFormat.TINY_2_FILE, "babric")
        ));

        catalogues.put("b1.7.3", b173);
    }

    public static class DownloadEntry {
        public Downloadable downloadable;
        public Intermediary intermediary;
        public Mappings mappings;

        public DownloadEntry(Downloadable downloadable, Intermediary intermediary) {
            this.downloadable = downloadable;
            this.intermediary = intermediary;
            this.mappings = null;
        }

        public DownloadEntry(Downloadable downloadable, Mappings mappings) {
            this.downloadable = downloadable;
            this.mappings = mappings;
            this.intermediary = null;
        }
        
        public String getName() {
            if(mappings != null) {
                return mappings.name;
            }
            
            if (intermediary != null) {
                return intermediary.name;
            }
            
            return "";
        }
        
        public String getId() {
            if(mappings != null) {
                return mappings.id;
            }
            if (intermediary != null) {
                return intermediary.id;
            }
            
            return "";
        }
        
        public String getPath(){
            if(mappings != null) {
                return mappings.path;
            }
            if (intermediary != null) {
                return intermediary.path;
            }
            return "";
        }
    }
}
