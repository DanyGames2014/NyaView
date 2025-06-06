package net.danygames2014.nyaview.download;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.fabricmc.mappingio.format.MappingFormat;

import java.net.URL;
import java.util.HashMap;

public class DownloadCatalog {
    public HashMap<String, HashMap<String, DownloadEntry>> catalogues;

    public HashMap<String, DownloadEntry> getCatalog(String gameVersion) {
        if (!catalogues.containsKey(gameVersion)) {
            catalogues.put(gameVersion, new HashMap<>());
        }

        return catalogues.get(gameVersion);
    }

    public void addToCatalog(String key, DownloadEntry downloadEntry) {
        String version = downloadEntry.version;
        getCatalog(version).put(key, downloadEntry);
    }

    public void addToCatalog(String key, DownloadEntry downloadEntry, String version) {
        getCatalog(version).put(key, downloadEntry);
    }

    public DownloadCatalog() {
        catalogues = new HashMap<>();

        // TODO: RetroMCP, Ornithe >r1.2.5, Ornithe, <b1.0, BIN-BH, Barn, BIN, Nostalgia
        // TODO: Read this from YAML File on a github repo

        // OrnitheMC (intermediary -> clientOfficial, serverOfficial)
        // r1.2.5 - b1.0
        var ornitheVersions = new String[]{"1.2.5", "1.2.4", "1.2.3", "1.2.2", "1.2.1", "1.2", "1.1", "1.0.1", "1.0.0", "b1.9-pre6", "b1.9-pre5", "b1.9-pre2", "b1.9-pre1", "b1.8.1", "b1.8", "b1.7.3", "b1.7.2", "b1.7_01", "b1.7", "b1.6.6", "b1.6.5", "b1.6.4", "b1.6.3", "b1.6.2", "b1.6.1", "b1.6", "b1.5_02", "b1.5_01", "b1.5", "b1.4_01", "b1.3_01", "b1.2_02", "b1.2_01", "b1.2", "b1.1_02", "b1.1_01", "b1.0.2", "b1.0_01", "b1.0"};

        for (var gameVersion : ornitheVersions) {
            // Calamus Gen 2
            addToCatalog("calamus_gen2",
                    new DownloadEntry(
                            new MavenDownloadable("https://maven.ornithemc.net", "releases", "net.ornithemc", "calamus-intermediary-gen2") {
                                @Override
                                public String getUrlString() {
                                    return getBaseArtifactUrl() + "/" + NyaView.config.getDownloadVersion() + "/calamus-intermediary-gen2-" + NyaView.config.getDownloadVersion() + "-v2.jar";
                                }
                            },
                            new Intermediary("calamus_gen2", "Calamus Gen 2", Environment.MERGED, "intermediary", "clientOfficial", "serverOfficial", "calamus_gen2.tiny", MappingFormat.TINY_2_FILE),
                            gameVersion
                    )
            );

            // Calamus Gen 2
            addToCatalog("feather_gen2",
                    new DownloadEntry(
                            new MavenDownloadable("https://maven.ornithemc.net", "releases", "net.ornithemc", "feather-gen2") {
                                @Override
                                public String getUrlString() {
                                    String latestVersion = MavenUtil.fetchLatestVersion(baseUrl, repository, groupId, artifactId);
                                    if (latestVersion == null) {
                                        return null;
                                    }

                                    String[] split = latestVersion.split("\\+");
                                    if (split.length != 2) {
                                        return null;
                                    }

                                    String version = NyaView.config.getDownloadVersion() + "+" + split[1];
                                    return getBaseArtifactUrl() + "/" + version + "/feather-gen2-" + version + "-v2.jar";
                                }
                            },
                            new Mappings("feather_gen2", "Feather Gen 2", Environment.MERGED, MappingType.BABRIC, "feather_gen2.tiny", MappingFormat.TINY_2_FILE, "calamus_gen2"),
                            gameVersion
                    )
            );
        }


        // Babric b1.7.3
        addToCatalog("babric", new DownloadEntry(
                        new MavenDownloadable("https://maven.glass-launcher.net", "babric", "babric", "intermediary") {
                            @Override
                            public String getUrlString() {
                                return getBaseArtifactUrl() + "/b1.7.3/intermediary-b1.7.3-v2.jar";
                            }
                        },
                        new Intermediary("babric", "Babric", Environment.MERGED, "intermediary", "client", "server", "babric.tiny", MappingFormat.TINY_2_FILE),
                        "b1.7.3"
                )
        );

        addToCatalog("biny", new DownloadEntry(
                        new MavenDownloadable("https://maven.glass-launcher.net", "releases", "net.glasslauncher", "biny") {
                            @Override
                            public String getUrlString() {
                                String version = MavenUtil.fetchLatestVersion(baseUrl, repository, groupId, artifactId);
                                return getBaseArtifactUrl() + "/" + version + "/biny-" + version + "-v2.jar";
                            }
                        },
                        new Mappings("biny", "BINY", Environment.MERGED, MappingType.BABRIC, "biny.tiny", MappingFormat.TINY_2_FILE, "babric"),
                        "b1.7.3"
                )
        );

        // Retro MCP
        String[] retromcpVersions = new String[]{"1.2.5", "1.2.4", "1.2.3", "1.1", "1.0.0", "b1.9-pre5", "b1.8.1/b1.8", "b1.8", "b1.7.3/b1.7", "b1.7.2/b1.7", "b1.7_01/b1.7", "b1.7", "b1.6.6/b1.6", "b1.6.5/b1.6", "b1.6.4/b1.6", "b1.6.3/b1.6", "b1.6.2/b1.6", "b1.6.1/b1.6", "b1.6", "b1.5_01", "b1.4_01", "b1.3_01", "b1.2_02/b1.2", "b1.2_01/b1.2", "b1.1_02/b1.1", "b1.1_01/b1.1"};
        for (String versionString : retromcpVersions) {
            String gameVersion;
            String mappingVersion;
            
            String[] split = versionString.split("/");
            if(split.length == 2) {
                gameVersion = split[0];
                mappingVersion = split[1];
            } else {
                gameVersion = versionString;
                mappingVersion = versionString;
            }

            addToCatalog("mcp_client", new DownloadEntry(
                            new SimpleDownloadable() {
                                @Override
                                public String getUrlString() {
                                    return "https://mcphackers.org/versions/" + mappingVersion + ".zip";
                                }
                            },
                            new Mappings("mcp_client", "MCP Client", Environment.CLIENT, MappingType.MCP, "mcp_client.tiny", MappingFormat.TINY_2_FILE, null),
                            gameVersion,
                            "client.tiny"
                    )
            );

            addToCatalog("mcp_server", new DownloadEntry(
                            new SimpleDownloadable() {
                                @Override
                                public String getUrlString() {
                                    return "https://mcphackers.org/versions/" + mappingVersion + ".zip";
                                }
                            },
                            new Mappings("mcp_server", "MCP Server", Environment.SERVER, MappingType.MCP, "mcp_server.tiny", MappingFormat.TINY_2_FILE, null),
                            gameVersion,
                            "server.tiny"
                    )
            );

        }
    }

    public static class DownloadEntry {
        public final Downloadable downloadable;
        public final Intermediary intermediary;
        public final Mappings mappings;
        public final String version;
        public final String mappingFilePath;

        public DownloadEntry(Downloadable downloadable, Intermediary intermediary, String gameVersion) {
            this(downloadable, intermediary, gameVersion, "mappings/mappings.tiny");
        }

        public DownloadEntry(Downloadable downloadable, Intermediary intermediary, String gameVersion, String mappingFilePath) {
            this.downloadable = downloadable;
            this.intermediary = intermediary;
            this.mappings = null;
            this.version = gameVersion;
            this.mappingFilePath = mappingFilePath;
        }

        public DownloadEntry(Downloadable downloadable, Mappings mappings, String version) {
            this(downloadable, mappings, version, "mappings/mappings.tiny");
        }

        public DownloadEntry(Downloadable downloadable, Mappings mappings, String gameVersion, String mappingFilePath) {
            this.downloadable = downloadable;
            this.mappings = mappings;
            this.intermediary = null;
            this.version = gameVersion;
            this.mappingFilePath = mappingFilePath;
        }

        public URL getUrl() {
            return downloadable.getUrl();
        }

        public String getName() {
            if (mappings != null) {
                return mappings.name;
            }

            if (intermediary != null) {
                return intermediary.name;
            }

            return "";
        }

        public String getId() {
            if (mappings != null) {
                return mappings.id;
            }
            if (intermediary != null) {
                return intermediary.id;
            }

            return "";
        }

        public String getPath() {
            if (mappings != null) {
                return mappings.path;
            }
            if (intermediary != null) {
                return intermediary.path;
            }
            return "";
        }
    }
}
