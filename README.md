basic-maven-resolver
====================

A minimal but efficient and portable (self-contained) maven URL resolver. Usable on Java and Dalvik VM.

## Why a minimal maven resolver ?

Because Aether is a very complex project, heavy for small devices.
In another hand maven repository is very used, well described, and a defacto standard for continuous delivery.

So this minimal resolver aims a performing one only task, resolve a artefact remotely and use a local cache on disk for efficiency.

With an only one dependency (only one class as a logger) this minimal resolver can be reused in many projects. It's easy embedded and tested in Java JVM, or in a Dalvik VM.

## How to use ? (Last version : 3)

    <dependency>
        <groupId>org.kevoree</groupId>
        <artifactId>org.kevoree.maven.resolver</artifactId>
        <version>REPLACE_LOG_VERSION</version>
    </dependency>
   
### Then in your Java code :

Create a resolver instance :

	MavenResolver resolver = new MavenResolver();
	//optionaly set the base pah (by default userdir/.m2/repository)
	resolver.setBasePath("/your_cache_path");
	
Resolve an artifact :
URL Pattern : mvn:groupID:artID:version[:ext]

	File cachedFile = resolver.resolve("mvn:org.kevoree.log:org.kevoree.log:1:jar",Arrays.asList("https://oss.sonatype.org/content/groups/public/"));
	
Or using splitted strings :

	File cachedFile = resolver.resolve("org.kevoree.log","org.kevoree.log", "1", "jar",Arrays.asList("https://oss.sonatype.org/content/groups/public/"));

## How to manage proxy ?
Sometimes, you need to handle proxy connection to access the Internet. In that case, you need to provide some extra information to the Java Virtual Machine to be able to use this proxy and acces the Internet and so to use our maven resolver.

Basically, you need to specify some system properties. The following lines give you examples to use http://myproxy:myproxyport as the proxy url:

	java -Dhttp.useProxy=true -Dhttp.proxyHost=htp://myproxy:myproxyport -jar myjar
	java -Dhttp.useProxy=true -Dhttp.proxyHost=myproxy -Dhttp.proxyPort=myproxyport -jar myjar


The following web page give you detailed information of the potential system properties that you can use:

http://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html

That's all :-)
