plugins {
    java
    id("com.diffplug.spotless") version "5.7.0"

}

version = "0.0.1"
group = "up"

allprojects {
    repositories {
        mavenCentral()
    }

    plugins.apply("java")

    java.sourceCompatibility = JavaVersion.VERSION_1_10

}

spotless {
  java {
    target("**/*.java")
    googleJavaFormat()
  }
}
