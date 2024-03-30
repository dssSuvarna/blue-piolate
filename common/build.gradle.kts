dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.mockito:mockito-inline:5.2.0")
}

tasks.named("bootJar").configure {
    enabled = false
}