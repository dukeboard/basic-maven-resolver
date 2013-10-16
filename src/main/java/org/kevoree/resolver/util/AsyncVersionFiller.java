package org.kevoree.resolver.util;

import org.kevoree.resolver.api.MavenArtefact;

import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 18/06/13
 * Time: 17:23
 */
public class AsyncVersionFiller implements Callable<String> {

    private MavenVersionResolver versionResolver = null;
    private MavenArtefact artefact = null;
    private String url = null;
    private String base = null;

    public AsyncVersionFiller(MavenVersionResolver r, MavenArtefact a,String base, String u) {
        this.versionResolver = r;
        this.artefact = a;
        this.url = u;
        this.base = base;
    }

    @Override
    public String call() {
        return versionResolver.foundRelevantVersion(artefact,base, url, false);
    }
}
