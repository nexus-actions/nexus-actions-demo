plugins {
  kotlin("multiplatform").version("1.4.30")
  id("maven-publish")
  id("signing")
}

repositories {
  mavenCentral()
}

kotlin {
  jvm()
  macosX64()
  mingwX64()
}

group = "com.github.nexus-actions.sample"
version = "0.2"

/**
 * Minimalist publishing setup. The multiplatform plugin does most of the work for us but we still
 * need to configure repositories and pom. A more complete setup would also configure signing.
 *
 * See https://github.com/vanniktech/gradle-maven-publish-plugin for a plugin that helps setting this up.
 */

// Empty javadoc
val javadocJar = tasks.register("javadocJar", Jar::class.java) {
  archiveClassifier.set("javadoc")
}

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
  useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PRIVATE_PASSWORD"))
  sign(publishing.publications)
}