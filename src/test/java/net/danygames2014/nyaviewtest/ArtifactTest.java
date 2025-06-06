package net.danygames2014.nyaviewtest;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.download.DownloadCatalog;
import net.danygames2014.nyaview.download.Downloader;

public class ArtifactTest {
    public static void main(String[] args) {
        NyaView.init();

        DownloadCatalog catalog = new DownloadCatalog();

        for (var v : catalog.catalogues.entrySet()) {
            System.out.print("\n" + v.getKey() + " : " );
            for (var e : v.getValue().entrySet()) {
                System.out.print(e.getValue().getName() + " ");
            }
        }
        
        //Downloader.download(catalog.getDownloads("b1.7.3").get("babric"));
//        var downloads = catalog.getDownloads(NyaView.config.getDownloadVersion());
//        for (var dl : downloads.entrySet()) {
//            Downloader.download(dl.getValue());
//        }

//        String baseUrl = "https://maven.glass-launcher.net";
//        String repository = "releases";
//        String groupId = "net.glasslauncher";
//        String artifactId = "biny";
//
//        try {
//            String a = MavenUtil.fetchLatestVersion(baseUrl, repository, groupId, artifactId);
//            System.out.println(a);
//            System.err.println(System.getProperty("java.version"));
//            fetchFile(
//                new URL("https://maven.glass-launcher.net/releases/net/glasslauncher/biny/b1.7.3+01cac70/biny-b1.7.3+01cac70-v2.jar"),
//                Path.of(System.getProperty("user.dir") + "/temp/" + "aaa.jar")
//            );
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
