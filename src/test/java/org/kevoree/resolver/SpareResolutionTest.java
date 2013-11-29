package org.kevoree.resolver;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 26/11/2013
 * Time: 10:58
 */
public class SpareResolutionTest {

    @Test
    public void test() {

        MavenResolver resolver = new MavenResolver();
        File r = resolver.resolve("mvn:org.kevoree.library.java:org.kevoree.library.java.javaNode:3.0.0-SNAPSHOT", new ArrayList<String>());
        System.out.println(r);

        File r2 = resolver.resolve("mvn:org.kevoree.library.java:org.kevoree.library.java.javaNode:latest", new ArrayList<String>());
        System.out.println(r2);


    }

}
