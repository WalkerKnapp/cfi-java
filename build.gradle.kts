plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "me.walkerknapp"
version = "0.0.1"

java.sourceCompatibility = JavaVersion.VERSION_1_9

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.dslplatform:dsl-json-java8:1.9.8")
    annotationProcessor("com.dslplatform:dsl-json-java8:1.9.8")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.11.0")

    implementation("org.slf4j:slf4j-api:2.0.0-alpha2")
}

val sourceJar by tasks.creating(Jar::class) {
    from(sourceSets.main.get().allJava)
    this.archiveClassifier.set("sources")
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn("javadoc")
    from(tasks.javadoc.get().destinationDir)
    this.archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("cfi-java") {
            from(components["java"])
            artifact(sourceJar)
            artifact(javadocJar)

            groupId = project.group as String
            artifactId = "cfi-java"
            version = project.version as String?

            pom {
                name.set("CFI-Java")
                description.set("CFI-Java is a library to interact with CMake projects through the CMake File Api.")
                url.set("https://github.com/WalkerKnapp/cfi-java")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("WalkerKnapp")
                        name.set("Walker Knapp")
                        email.set("walker@walkerknapp.me")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/WalkerKnapp/cfi-java.git")
                    developerConnection.set("scm:git:git@github.com:WalkerKnapp/cfi-java.git")
                    url.set("https://github.com/WalkerKnapp/cfi-java")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("PGP_KEY_ID"), System.getenv("PGP_KEY"), System.getenv("PGP_PASSWORD"))
    sign(publishing.publications["cfi-java"])
}
