# Seal 

[![Download](https://api.bintray.com/packages/2bab/maven/Seal-Manifest-Precheck-Plugin/images/download.svg)](https://bintray.com/2bab/maven/Seal-Manifest-Precheck-Plugin/_latestVersion) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Seal is a gradle plugin to do precheck of Android Manifest. 

English | [中文说明](http://2bab.me/2017/05/23/gradle-daily-crash-aar-replace-conflict/)


## Conflict / Warning When Manifest Merge 

As we all know, Android provides a tool names [AndroidManifest Merger](https://developer.android.com/studio/build/manifest-merge.html) to manage manifests-merge. But these rules are not enough for some situation like below:   

> 1. Warning: AndroidManifest.xml already defines debuggable (in http://schemas.android.com/apk/res/android); using existing value in manifest.

That's because some out-of-date libraries set `debuggable` at AndroidManifest, but now we use `build.gradle` to do it. 

> 2. Multiple entries with same key: @android:theme=REPLACE and android:theme=REPLACE  /  Multiple entries with same key: @android:allowBackup=REPLACE and android:allowBackup=REPLACE. 

There is a library which defined `android:allowBackup=true` conflicts with yours (`android:allowBackup=false`). You wanna to override it using `tools:replace="android:allowBackup"`, but find that `tools:replace="android:allowBackup"` is also present at lib's manifest, finally the conflict shows above. (Also see [this](http://stackoverflow.com/questions/35131182/manifest-merge-in-android-studio))   

...

All of these are what we face with, and **Seal** trying to solve.

## Quick Start

1. Compile&apply Seal plugin:
```
// project's build.gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.1'
        classpath 'me.xx2bab.gradle:seal-manifest-precheck-plugin:1.0.0'
    }
}

...

// app's build.gradle
apply plugin: 'seal'

```

2. Configurations:
``` gradle
def projectRoot = project.getRootProject().rootDir.absolutePath

// Folders may include AndroidManifest.xml files
// 1. For gradle plugin 2.3.0 or higher, build-cache is default choice,
// 2. But we should make sure snapshot-libs will be checked too.
// 3. Free to add your folders for more customization 
def manifestPath = [
        // for AAR of Release
        // see note below
        projectRoot + '/build-cache', 
        // for AAR of SNAPSHOT
        projectRoot + '/app/build/intermediates/exploded-aar'
]

def removeAttrs = [
        'android:debuggable'
]

def replaceValues = [
        'android:allowBackup'
]


seal {
    enabled = true
    manifests = manifestPath

    appAttrs {
        enabled = true
        attrsShouldRemove = removeAttrs
    }

    appReplaceValues {
        enabled = true
        valuesShouldRemove = replaceValues
    }
}
```

Note: If `build-cache` is enable, Seal recommends that custom build cache folder placed in the Project Folder. 
 
```
//gradle.properties
android.buildCacheDir=./build-cache
...
```

## Changelog

### v1.0.0

- Support Removing Application Attributes 
- Support Removing Application's `tools:replace` value  