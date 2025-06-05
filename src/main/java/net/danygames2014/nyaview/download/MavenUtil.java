package net.danygames2014.nyaview.download;

import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MavenUtil {
    public static String fetchLatestVersion(String baseUrl, String repository, String groupId, String artifactId) {
        String groupPath = groupId.replace('.', '/');
        String artifactPath = baseUrl + "/" + repository + "/" + groupPath + "/" + artifactId;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(artifactPath + "/maven-metadata.xml").openConnection();
            connection.setRequestMethod("GET");

            StringBuilder output = new StringBuilder();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    output.append(line);
                }
            }

            JSONObject json = XML.toJSONObject(output.toString());
            return json.getJSONObject("metadata").getJSONObject("versioning").getString("latest");
        } catch (Exception e) {
            return null;
        }
    }
}
