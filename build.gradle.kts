plugins {
	java
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.2"
	id("io.freefair.lombok") version "8.2.2"
}

group = "JuegoDeDados.Mongo"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	//spring config
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.2")

	//Mongo
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	//Lombok
	implementation("org.projectlombok:lombok:1.18.20")

	// Swagger
	implementation("io.swagger.core.v3:swagger-annotations:2.2.15")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	//anotaciones
	implementation("org.webjars:webjars-locator-core:0.53")
	implementation("com.google.code.findbugs:jsr305:3.0.2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
