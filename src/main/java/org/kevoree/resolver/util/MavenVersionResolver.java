package org.kevoree.resolver.util;

import org.kevoree.resolver.api.MavenArtefact;
import org.kevoree.resolver.api.MavenVersionResult;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by duke on 16/05/13.
 */
public class MavenVersionResolver {

    private static final String buildLatestTag = "<latest>";
    private static final String buildEndLatestTag = "</latest>";

    private static final String buildReleaseTag = "<release>";
    private static final String buildEndreleaseTag = "</release>";

    private static final String buildMavenTag = "<buildNumber>";
    private static final String buildEndMavenTag = "</buildNumber>";

    private static final String timestampMavenTag = "<timestamp>";
    private static final String timestampEndMavenTag = "</timestamp>";

    private static final String lastUpdatedMavenTag = "<lastUpdated>";
    private static final String lastUpdatedEndMavenTag = "</lastUpdated>";

    private static final String snapshotVersionClassifierMavenTag = "<classifier>";
    private static final String snapshotVersionClassifierEndMavenTag = "</classifier>";

    private static final String snapshotVersionExtensionMavenTag = "<extension>";
    private static final String snapshotVersionExtensionEndMavenTag = "</extension>";

    private static final String snapshotVersionValueMavenTag = "<value>";
    private static final String snapshotVersionValueEndMavenTag = "</value>";

    private static final String snapshotVersionUpdatedMavenTag = "<updated>";
    private static final String snapshotVersionUpdatedEndMavenTag = "</updated>";

    public static final String metaFile = "maven-metadata.xml";
    private static final String localmetaFile = "maven-metadata-local.xml";

    public MavenVersionResult resolveVersion(MavenArtefact artefact, String basePath, boolean localDeploy) throws IOException {

        StringBuilder builder = new StringBuilder();
        builder.append(basePath);
        String sep = File.separator;
        if (basePath.startsWith("http")) {
            sep = "/";
        }
        if (!basePath.endsWith(sep)) {
            builder.append(sep);
        }
        if (basePath.startsWith("http")) {
            builder.append(artefact.getGroup().replace(".", "/"));
        } else {
            builder.append(artefact.getGroup().replace(".", File.separator));
        }
        builder.append(sep);
        builder.append(artefact.getName());
        builder.append(sep);
        builder.append(artefact.getVersion());
        builder.append(sep);
        if (localDeploy) {
            builder.append(localmetaFile);
        } else {
            builder.append(metaFile);
        }
        URL metadataURL = new URL("file:///" + builder.toString());
        if (basePath.startsWith("http")) {
            metadataURL = new URL(builder.toString());
        }
        URLConnection c = metadataURL.openConnection();

        c.setRequestProperty("User-Agent", "Kevoree");


        InputStream in = c.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder resultBuilder = new StringBuilder();
        String line = reader.readLine();
        resultBuilder.append(line);
        while ((line = reader.readLine()) != null) {
            resultBuilder.append(line);
        }
        String result = resultBuilder.toString();
        in.close();

//        System.out.println(result);

        MavenVersionResult versionResult = new MavenVersionResult();

        boolean found = false;
//        Pattern pattern = Pattern.compile("<snapshotVersion>( *(<extension>(.(?!</snapshotVersion>))*</extension>|<value>(.(?!</snapshotVersion>))*</value>|<updated>(.(?!</snapshotVersion>))*</updated>|<classifier>(.(?!</snapshotVersion>))*</classifier>) *)*</snapshotVersion>");
        Pattern pattern = Pattern.compile("<snapshotVersion> *(.(?!(</snapshotVersion>)))* *</snapshotVersion>");
        Matcher matcher = pattern.matcher(result);
        int index = 0;
        while (matcher.find(index) && !found) {
            String snapshotVersion = matcher.group().trim();
//            System.err.println(snapshotVersion);

            if ((!snapshotVersion.contains(snapshotVersionClassifierMavenTag)
                    || (snapshotVersion.contains(snapshotVersionClassifierMavenTag)
                    && !"sources".equalsIgnoreCase(snapshotVersion.substring(snapshotVersion.indexOf(snapshotVersionClassifierMavenTag) + snapshotVersionClassifierMavenTag.length(), snapshotVersion.indexOf(snapshotVersionClassifierEndMavenTag)))))
                    && snapshotVersion.contains(snapshotVersionValueMavenTag)
                    && snapshotVersion.contains(snapshotVersionUpdatedMavenTag)
                    /*&& snapshotVersion.contains(snapshotVersionExtensionMavenTag)*/
                    && (!snapshotVersion.contains(snapshotVersionExtensionMavenTag)
                    || artefact.getExtension().equalsIgnoreCase(snapshotVersion.substring(snapshotVersion.indexOf(snapshotVersionExtensionMavenTag) + snapshotVersionExtensionMavenTag.length(), snapshotVersion.indexOf(snapshotVersionExtensionEndMavenTag))))
                    ) {
                versionResult.setValue(snapshotVersion.substring(snapshotVersion.indexOf(snapshotVersionValueMavenTag) + snapshotVersionValueMavenTag.length(), snapshotVersion.indexOf(snapshotVersionValueEndMavenTag)));
                versionResult.setLastUpdate(snapshotVersion.substring(snapshotVersion.indexOf(snapshotVersionUpdatedMavenTag) + snapshotVersionUpdatedMavenTag.length(), snapshotVersion.indexOf(snapshotVersionUpdatedEndMavenTag)));
                found = true;
            }
            index += snapshotVersion.length();
        }

        versionResult.setUrl_origin(basePath);
        versionResult.setNotDeployed(localDeploy);
        if (!found) {
            if (result.contains(timestampMavenTag) && result.contains(timestampEndMavenTag) && result.contains(buildMavenTag) && result.contains(buildEndMavenTag) && result.contains(lastUpdatedMavenTag) && result.contains(lastUpdatedEndMavenTag)) {
                versionResult.setValue(result.substring(result.indexOf(timestampMavenTag) + timestampMavenTag.length(), result.indexOf(timestampEndMavenTag)) + "-" + result.substring(result.indexOf(buildMavenTag) + buildMavenTag.length(), result.indexOf(buildEndMavenTag)));
                versionResult.setLastUpdate(result.substring(result.indexOf(lastUpdatedMavenTag) + lastUpdatedMavenTag.length(), result.indexOf(lastUpdatedEndMavenTag)));
                return versionResult;
            } else {
            return null;
            }
        } else {
            return versionResult;
        }
    }


    public String foundRelevantVersion(MavenArtefact artefact, String basePath, boolean localDeploy) {
        String askedVersion = artefact.getVersion().toLowerCase();
        Boolean release = false;
        Boolean lastest = false;
        if (askedVersion.equalsIgnoreCase("release")) {
            release = true;
        }
        if (askedVersion.equalsIgnoreCase("latest")) {
            lastest = true;
        }
        if (!release && !lastest) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(basePath);
        String sep = File.separator;
        if (basePath.startsWith("http")) {
            sep = "/";
        }
        if (!basePath.endsWith(sep)) {
            builder.append(sep);
        }
        if (basePath.startsWith("http")) {
            builder.append(artefact.getGroup().replace(".", "/"));
        } else {
            builder.append(artefact.getGroup().replace(".", File.separator));
        }
        builder.append(sep);
        builder.append(artefact.getName());
        builder.append(sep);

        if (localDeploy) {
            builder.append(localmetaFile);
        } else {
            builder.append(metaFile);
        }
        try {
            URL metadataURL = new URL("file:///" + builder.toString());
            if (basePath.startsWith("http")) {
                metadataURL = new URL(builder.toString());
            }
            URLConnection c = metadataURL.openConnection();

            c.setRequestProperty("User-Agent", "Kevoree");

            InputStream in = c.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder resultBuilder = new StringBuilder();
            String line = reader.readLine();
            resultBuilder.append(line);
            while ((line = reader.readLine()) != null) {
                resultBuilder.append(line);
            }
            String result = resultBuilder.toString();
            in.close();
            if (release) {
                if (result.contains(buildReleaseTag) && result.contains(buildEndreleaseTag)) {
                    return result.substring(result.indexOf(buildReleaseTag) + buildReleaseTag.length(), result.indexOf(buildEndreleaseTag));
                }
            }
            if (lastest) {
                if (result.contains(buildLatestTag) && result.contains(buildEndLatestTag)) {
                    return result.substring(result.indexOf(buildLatestTag) + buildLatestTag.length(), result.indexOf(buildEndLatestTag));
                }
            }
        } catch (MalformedURLException ignored) {
        } catch (IOException ignored) {
        }
        return null;
    }


}
