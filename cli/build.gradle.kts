
plugins {
    java
    application
}

application {
    mainClass.set("up.visulog.cli.CLILauncher")
}

dependencies {
    implementation("commons-cli:commons-cli:20040117.000000")
    implementation(project(":analyzer"))
    implementation(project(":webgen"))
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
}


