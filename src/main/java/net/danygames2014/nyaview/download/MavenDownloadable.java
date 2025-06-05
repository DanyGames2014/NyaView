package net.danygames2014.nyaview.download;

public abstract class MavenDownloadable implements Downloadable{
    String baseUrl;
    String repository;
    String groupId;
    String artifactId;

    public MavenDownloadable(String baseUrl, String repository, String groupId, String artifactId) {
        this.baseUrl = baseUrl;
        this.repository = repository;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }
    
    public String getBaseArtifactUrl() {
        return baseUrl + "/" + repository + "/" + groupId.replace('.', '/') + "/" + artifactId;
    }
}
