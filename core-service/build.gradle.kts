dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-mysql")
	implementation("software.amazon.awssdk:s3:2.15.51")
	implementation(project(mapOf("path" to ":")))
	implementation(project(":common"))

	implementation("org.postgresql:postgresql:42.6.0")
	testImplementation("org.flywaydb:flyway-core")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql:1.18.3")
	testImplementation ("org.testcontainers:testcontainers:1.16.0")
	testImplementation ("org.testcontainers:localstack:1.16.0")
	testImplementation ("com.amazonaws:aws-java-sdk-s3:1.11.914")
	testImplementation("org.mockito:mockito-inline:5.2.0")
}