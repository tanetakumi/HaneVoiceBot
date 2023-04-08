plugins {
    id("java")
}

group = "net.serveron.hane"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("m2-dv8tion") {
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("net.dv8tion:JDA:4.4.0_350")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}