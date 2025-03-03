import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.spring") version "1.8.10"
	kotlin("plugin.jpa") version "1.8.10"
	jacoco
}

group = "com.innocv"

java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("org.apache.pdfbox:pdfbox:2.0.24")
	implementation("org.apache.poi:poi:5.2.3")
	implementation("org.apache.poi:poi-scratchpad:5.2.3")
	implementation("org.apache.poi:poi-ooxml:5.2.3")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jobrunr:jobrunr-spring-boot-3-starter:7.3.2")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	implementation(platform("com.aallam.openai:openai-client-bom:3.7.2"))
	implementation("com.aallam.openai:openai-client")
	runtimeOnly("io.ktor:ktor-client-okhttp")
}

springBoot {
	buildInfo()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar>{
	manifest {
		attributes("Implementation-Version" to archiveVersion)
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		csv.required.set(false)
	}
}

tasks.getByName<Jar>("jar") {
	enabled = false
}
