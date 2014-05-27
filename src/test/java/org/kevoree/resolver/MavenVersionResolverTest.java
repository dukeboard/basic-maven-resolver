package org.kevoree.resolver;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class MavenVersionResolverTest {

    @Test
    public void testOnlyCentral() {
        MavenResolver resolver = new MavenResolver();
        Set<String> urls = new HashSet<String>();
        urls.add("http://repo1.maven.org/maven2/");
        File kevoreeEditor = resolver.resolve("org.kevoree.library.java", "org.kevoree.library.java.javaNode", "latest", "jar",urls);

        Set<String> sets = resolver.listVersion("org.kevoree.library.java", "org.kevoree.library.java.javaNode", "jar",urls);
        for(String version : sets){
            System.out.println(version);
        }

        assertNotNull(kevoreeEditor);

    }

    @Test
    public void testfindMaxVersion() throws IOException {
        MavenResolver resolver = new MavenResolver();
        HashSet<String> sona = new HashSet<String>();
        sona.add("http://oss.sonatype.org/content/groups/public");
        File result = resolver.resolve("org.kevoree", "org.kevoree.core", "RELEASE", "jar", sona);
        System.out.println(result.getAbsolutePath());
        Assert.assertTrue("RELEASE", !result.getAbsolutePath().contains("SNAPSHOT"));

        HashSet<String> central = new HashSet<String>();
        central.add("http://repo1.maven.org/maven2");

        File result2 = resolver.resolve("org.kevoree", "org.kevoree.core", "RELEASE", "jar", central);
        System.out.println(result2.getAbsolutePath());
        Assert.assertTrue("RELEASE", !result2.getAbsolutePath().contains("SNAPSHOT"));


        File result3 = resolver.resolve("org.kevoree", "org.kevoree.core", "LATEST", "jar", sona);
        System.out.println(result3.getCanonicalPath());
        Assert.assertTrue("SNAPSHOT", result3.getAbsolutePath().contains("SNAPSHOT"));
    }

}
