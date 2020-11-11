buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
    java
    id("com.github.ben-manes.versions") version "0.36.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.15"
    id("com.simonharrer.modernizer") version "2.1.0-1" apply false
}

apply<BootstrapPlugin>()
apply<VersionPlugin>()

allprojects {
    group = "com.openosrs"
    version = ProjectVersions.openosrsVersion
    apply<MavenPublishPlugin>()
}

subprojects {
    var subprojectName = name
    group = "com.openosrs.externals"

    project.extra["PluginProvider"] = "OpenOSRS"
    project.extra["ProjectUrl"] = "https://discord.gg/OpenOSRS"
    project.extra["PluginLicense"] = "3-Clause BSD License"

    repositories {
        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
                excludeGroupByRegex("com\\.runelite.*")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://repo.runelite.net")
                }
            }
            filter {
                includeModule("net.runelite", "discord")
                includeModule("net.runelite.jogl", "jogl-all")
                includeModule("net.runelite.gluegen", "gluegen-rt")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }

    apply<JavaPlugin>()
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "se.patrikerdes.use-latest-versions")
    apply(plugin = "com.simonharrer.modernizer")

    dependencies {
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        annotationProcessor(group = "org.pf4j", name = "pf4j", version = "3.5.0")

        compileOnly(group = "com.openosrs", name = "http-api", version = ProjectVersions.openosrsVersion)
        compileOnly(group = "com.openosrs", name = "runelite-api", version = ProjectVersions.openosrsVersion)
        compileOnly(group = "com.openosrs", name = "runelite-client", version = ProjectVersions.openosrsVersion)

        compileOnly(group = "org.apache.commons", name = "commons-text", version = "1.9")
        compileOnly(group = "com.google.guava", name = "guava", version = "30.0-jre")
        compileOnly(group = "com.google.inject", name = "guice", version = "4.2.3", classifier = "no_aop")
        compileOnly(group = "com.google.code.gson", name = "gson", version = "2.8.6")
        compileOnly(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
        compileOnly(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        compileOnly(group = "com.squareup.okhttp3", name = "okhttp", version = "4.9.0")
        compileOnly(group = "org.pf4j", name = "pf4j", version = "3.5.0")
        compileOnly(group = "io.reactivex.rxjava3", name = "rxjava", version = "3.0.7")
        compileOnly(group = "org.pushing-pixels", name = "radiance-substance", version = "2.5.1")

        testAnnotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.16")

        testImplementation(group = "com.openosrs", name = "http-api", version = ProjectVersions.openosrsVersion)
        testImplementation(group = "com.openosrs", name = "runelite-api", version = ProjectVersions.openosrsVersion)
        testImplementation(group = "com.openosrs", name = "runelite-client", version = ProjectVersions.openosrsVersion)

        testImplementation(group = "org.pf4j", name = "pf4j", version = "3.5.0")
        testImplementation(group = "com.google.inject.extensions", name = "guice-testlib", version = "4.2.3")
        testImplementation(group = "com.google.code.gson", name = "gson", version = "2.8.6")
        testImplementation(group = "net.sf.jopt-simple", name = "jopt-simple", version = "5.0.4")
        testImplementation(group = "junit", name = "junit", version = "4.13.1")
        testImplementation(group = "org.mockito", name = "mockito-core", version = "3.6.0")
        testImplementation(group = "org.mockito", name = "mockito-inline", version = "3.6.0")
        testImplementation(group = "org.projectlombok", name = "lombok", version = "1.18.16")
        testImplementation(group = "org.hamcrest", name = "hamcrest-library", version = "2.2")
        testImplementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    }

    checkstyle {
        maxWarnings = 0
        toolVersion = "8.25"
        isShowViolations = true
        isIgnoreFailures = false
    }

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("$buildDir/repo")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<Jar> {
            doLast {
                copy {
                    from("./build/libs/")
                    into("../release/")
                }

                val externalManagerDirectory: String = project.findProperty("externalManagerDirectory")?.toString() ?: System.getProperty("user.home") + "/.runelite/externalmanager"
                val releaseToExternalModules: List<String> = project.findProperty("releaseToExternalmanager")?.toString()?.split(",") ?: emptyList()
                if (releaseToExternalModules.contains(subprojectName) || releaseToExternalModules.contains("all")) {
                    copy {
                        from("./build/libs/")
                        into(externalManagerDirectory)
                    }
                }
            }
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }

        withType<Checkstyle> {
            group = "verification"

            exclude("**/ScriptVarType.java")
            exclude("**/LayoutSolver.java")
            exclude("**/RoomType.java")
        }

        named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
            checkForGradleUpdate = false

            resolutionStrategy {
                componentSelection {
                    all {
                        if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                            reject("Non stable")
                        }
                    }
                }
            }
        }

        register<Copy>("copyDeps") {
            into("./build/deps/")
            from(configurations["runtimeClasspath"])
        }
    }
}

tasks {
    named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
        checkForGradleUpdate = false

        resolutionStrategy {
            componentSelection {
                all {
                    if (candidate.displayName.contains("fernflower") || isNonStable(candidate.version)) {
                        reject("Non stable")
                    }
                }
            }
        }
    }
}

fun isNonStable(version: String): Boolean {
    return listOf("ALPHA", "BETA", "RC").any {
        version.toUpperCase().contains(it)
    }
}
