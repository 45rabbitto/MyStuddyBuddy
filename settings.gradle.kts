pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
        // Repository untuk plugin Chaquopy
        maven {
            url = uri("https://chaquo.com/maven")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
        .version("1.0.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        // Repository untuk dependencies Python (numpy, onnxruntime, dll.)
        maven {
            url = uri("https://chaquo.com/maven")
        }
    }
}

rootProject.name = "MyStudyBuddy"
include(":app")