plugins {
    java
    `java-library`
    `maven-publish`
}

val repoUser: String by project
val repoPass: String by project

repositories {
    mavenLocal()

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
//
//    maven {
//        url = uri("https://maven.vaadin.com/vaadin-prereleases/")
//    }
//
//    maven {
//        url = uri("https://maven.vaadin.com/vaadin-addons")
//    }
    maven {
        credentials.password = repoUser;
        credentials.username = repoPass;

        url = uri("http://10.198.0.1:18081/repository/maven-releases/")

        isAllowInsecureProtocol = true
    }

}

dependencies {
    implementation("com.vaadin:vaadin-core:23.3.6")
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
}

val groupLibrary = "cz.miloslavbrada.vaadin"
val artifactIdLibrary = "quill-editor"
val versionLibrary = "1.0.10"
val libraryDescription = "Quill rich text editor Vaadin component by Transkript online s.r.o"

java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = groupLibrary
            artifactId = artifactIdLibrary
            version = versionLibrary

            pom {
                developers {
                    developer {
                        id.set("mbrada")
                        name.set("Ing. Miloslav Brada")
                        email.set("brada@transkript.cz")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            isAllowInsecureProtocol = true

            name = "transkript-repo"
            url = uri("http://10.198.0.1:18081/repository/maven-releases/")
            credentials {
                //set repo username & password, see readme.md
                username = repoUser
                password = repoPass
            }
        }
    }

}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
