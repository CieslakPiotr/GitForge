plugins {
    id("java-conventions")
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.modulith.bom))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.modulith.api)

    testImplementation(libs.spring.boot.starter.test)
}