import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("java")
    id("info.solidsoft.pitest") version "1.15.0"
    id("com.github.spotbugs") version "6.0.24"
    id("org.sonarqube") version "5.1.0.4882"
}

group = "com.viktor.task1"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val fuzz by sourceSets.creating {
    java.srcDir("src/fuzz/java")
    resources.srcDir("src/fuzz/resources")
    compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

val jazzer by configurations.creating

configurations[fuzz.implementationConfigurationName].extendsFrom(configurations["testImplementation"])
configurations[fuzz.runtimeOnlyConfigurationName].extendsFrom(configurations["testRuntimeOnly"])

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("org.jetbrains:annotations:24.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testImplementation("io.cucumber:cucumber-java:7.18.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("org.hamcrest:hamcrest:2.2")

    add(fuzz.implementationConfigurationName, "com.code-intelligence:jazzer-api:0.24.0")
    jazzer("com.code-intelligence:jazzer:0.24.0")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

tasks.named<ProcessResources>("processFuzzResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

spotbugs {
    toolVersion.set("4.8.6")
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    ignoreFailures.set(true)
}

tasks.withType<SpotBugsTask>().configureEach {
    val taskName = this.name
    reports.create("html") {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/${taskName}.html"))
    }
    reports.create("xml") {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/${taskName}.xml"))
    }
}

sonar {
    properties {
        property("sonar.projectName", "Task1Gradle")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.java.binaries", layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
        property("sonar.junit.reportPaths", layout.buildDirectory.dir("test-results/test").get().asFile.absolutePath)
    }
}

fun registerJazzerTask(taskName: String, targetClass: String, corpusDir: String, artifactPrefix: String) {
    tasks.register<JavaExec>(taskName) {
        group = "verification"
        description = "Run Jazzer for $targetClass"
        classpath = jazzer + fuzz.runtimeClasspath
        mainClass.set("com.code_intelligence.jazzer.Jazzer")

        val corpusPath = layout.projectDirectory.dir(corpusDir).asFile.absolutePath
        val crashPath = layout.buildDirectory.dir("reports/jazzer").get().asFile.absolutePath

        doFirst {
            file(corpusPath).mkdirs()
            file(crashPath).mkdirs()
        }

        args(
            "--target_class=$targetClass",
            "--cp=${fuzz.runtimeClasspath.asPath}",
            "--instrumentation_includes=com.viktor.task1.*",
            "--reproducer_path=$crashPath",
            "--",
            "-max_total_time=20",
            "-artifact_prefix=$artifactPrefix",
            corpusPath,
        )
    }
}

registerJazzerTask(
    taskName = "fuzzJsonInputLoader",
    targetClass = "com.viktor.task1.fuzz.JsonInputLoaderFuzzTarget",
    corpusDir = "src/fuzz/resources/corpus/json-input-loader",
    artifactPrefix = "${layout.buildDirectory.asFile.get().absolutePath}/reports/jazzer/json-",
)

registerJazzerTask(
    taskName = "fuzzAnalyticalCollisionDetector",
    targetClass = "com.viktor.task1.fuzz.AnalyticalCollisionDetectorFuzzTarget",
    corpusDir = "src/fuzz/resources/corpus/analytical-collision",
    artifactPrefix = "${layout.buildDirectory.asFile.get().absolutePath}/reports/jazzer/collision-",
)

pitest {
    junit5PluginVersion.set("1.2.1")
    targetClasses.set(
        setOf(
            "com.viktor.task1.collision.*",
            "com.viktor.task1.io.*",
            "com.viktor.task1.model.*",
            "com.viktor.task1.service.*",
        ),
    )
    threads.set(4)
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
}

tasks.named("jar") {
    (this as Jar).manifest {
        attributes["Main-Class"] = "com.viktor.task1.ui.ApplicationRunner"
    }
}
