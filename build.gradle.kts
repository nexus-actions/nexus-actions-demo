plugins {
    kotlin("multiplatform") version "1.4.31"
    id("org.jetbrains.dokka") version "1.4.20"
    id("maven-publish")
    id("signing")
}

repositories {
    jcenter()
    mavenCentral()
}

val dokkaOutputDir = "$buildDir/dokka"

tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

kotlin {
    jvm()
    macosX64()
    mingwX64()
}

group = "com.github.nexus-actions.sample"
version = "0.1"

/**
 * Minimalist publishing setup. The multiplatform plugin does most of the work for us but we still
 * need to configure repositories and pom. A more complete setup would also configure signing.
 *
 * See https://github.com/vanniktech/gradle-maven-publish-plugin for a plugin that helps setting this up.
 */
publishing {
    repositories {
        maven {
            name = "Oss"
            setUrl {
                val repositoryId =
                    System.getenv("SONATYPE_REPOSITORY_ID") ?: error("Missing env variable: SONATYPE_REPOSITORY_ID")
                "https://oss.sonatype.org/service/local/staging/deployByRepositoryId/${repositoryId}/"
            }
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
        maven {
            name = "Snapshot"
            setUrl { "https://oss.sonatype.org/content/repositories/snapshots/" }
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("nexus-actions-repo-sample")
                description.set("a sample project for the nexus-actions Github Actions")
                url.set("https://github.com/nexus-actions/create-nexus-staging-repo-sample/")
                developers {
                    developer {
                        name.set("Martin Bonnin")
                    }
                }
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://github.com/nexus-actions/create-nexus-staging-repo-sample/")
                    }
                }
                scm {
                    url.set("https://github.com/nexus-actions/create-nexus-staging-repo-sample/")
                }
            }
        }
    }
}

signing {
  useInMemoryPgpKeys(
      System.getenv("GPG_PRIVATE_KEY"),
      System.getenv("GPG_PRIVATE_PASSWORD")
  )
  sign(publishing.publications)
}