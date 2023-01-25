
plugins {
    `java-library`
}

dependencies {
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    implementation(project(":webgen"))
    testImplementation("junit:junit:4.+")
}


