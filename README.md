basic-maven-resolver
====================

A minimal but efficient and portable (self-contained) maven URL resolver. Usable on Java and Dalvik VM.

## Why a minimal maven resolver ?

Because Aether is a very complexe project, heavy for small devices.
In another hand maven repository is very used, well described, and a defacto standard for continuous delivery.

So this minimal resolver aims a performing one only task, resolve a artefact remotely and use a local cache on disk for efficiency.

With an only on dependency (only one class as a logger) this minimal resolver can be resued in many projects. It's easy embedded and tested Java JVM, or a Dalvik VM.

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


That's all :-)
