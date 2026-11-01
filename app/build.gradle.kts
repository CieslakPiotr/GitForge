plugins {
    id("java-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.modulith.bom))
    implementation(project(":common"))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.modulith.starter.core)
    testImplementation(libs.archunit.junit5)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)
    implementation(libs.spring.boot.starter.data.jpa)

    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.modulith.starter.test)

}

tasks.bootJar {
    archiveFileName = "GitForge.jar"
}