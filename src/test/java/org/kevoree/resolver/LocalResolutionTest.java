package org.kevoree.resolver;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 21/05/13
 * Time: 14:57
 */
public class LocalResolutionTest {

    @Test
    public void testLocalResolution() {
        MavenResolver resolver = new MavenResolver();
        Set<String> l = new HashSet<String>();
        l.add("https://oss.sonatype.org/content/groups/public/");

        File resolved2 = resolver.resolve("org.kevoree.log","org.kevoree.log", "1", "jar", l);
        Assert.assertNotSame(null, resolved2);


        HashSet<String> sona = new HashSet<String>();
        sona.addAll(Arrays.asList("https://oss.sonatype.org/content/groups/public/"));


        File resolved3 = resolver.resolve("mvn:org.kevoree.log:org.kevoree.log:1:jar",sona);
        Assert.assertNotSame(null, resolved3);

        File resolved4 = resolver.resolve("mvn:org.kevoree.log:org.kevoree.log:1", l);
        Assert.assertNotSame(null, resolved4);

        File resolved5 = resolver.resolve("mvn:org.kevoree.kcl:org.kevoree.kcl:LATEST", l);
        Assert.assertNotSame(null, resolved5);

    }

}
