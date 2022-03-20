<img src="./seal-banner.png" alt="Seal" width="359px">

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/seal/badge.svg)](https://search.maven.org/artifact/me.2bab/seal)
 [![Actions Status](https://github.com/2bab/Seal/workflows/CI/badge.svg)](https://github.com/2bab/Seal/actions) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

English | [中文说明](/README_zh.md)

Seal is a Gradle Plugin to resolve AndroidManifest.xml merge conflicts, powered by [New Variant/Artifact API](https://developer.android.com/studio/build/extend-agp) & [Polyfill](https://github.com/2BAB/Polyfill).

To be noticed, except the tag removing, any other delete/update features should always consider the "tools:replace", "tools:remove", and other official features that [ManifestMerger](https://developer.android.com/studio/build/manifest-merge) provided as higher priority.

Functionality that Seal provided is more like the **first aid** to **save an urgent publish that is blocked by ManifestMerger**, including pre/post processors to intercept the merge flow of AndroidManifest.xml. Developers should take responsibility to report bugs to library authors(who introduced problematic Manifest), ManifestMerger(Google), AAPT2(Google), which is the true way to solve the merge issues.

## Quick Start

1. Compile Seal plugin:

``` kotlin
// root project's build.gradle.kts
buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.2")
        classpath("me.2bab:seal:3.2.0")
    }
}
```

2. Apply plugin:

``` Kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    // Apply this plugin
    id("me.2bab.seal")
}
```

3. Configurations:

``` Kotlin
seal {

    // 0. Two cases for before merge.
    beforeMerge("Remove description attr for library input Manifest.")
        .tag("application")
        .attr("android:description")
        .deleteAttr()
    beforeMerge("Remove problematic replace attr for library input Manifest.")
        .tag("application")
        .attr("tools:replace")
        .deleteAttr()

    // Full covered cases for after merge (1-5).
    // 1. The target of this operation is too broad, please specify the attr and value if possible.
    afterMerge("Remove all uses-feature tags.")
        .tag("uses-feature")
        .deleteTag()

    // 2. The target of this operation is too broad, please specify the value if possible.
    afterMerge("Remove all custom permission tags.")
        .tag("permission")
        .attr("android:protectionLevel")
        .deleteTag()

    // 3. This is the way we recommend to delete the tag(s).
    afterMerge("Remove invalid service tag.")
        .tag("service")
        .attr("android:name")
        .value("me.xx2bab.seal.sample.library.LegacyService")
        .deleteTag()

    // You should try to use "tools:remove" or "tools:replace" instead of "deleteAttr" if possible
    // 4. To delete an attr and its value.
    afterMerge("Remove application's allowBackup attr.")
        .tag("application")
        .attr("android:allowBackup")
        .deleteAttr()

    // You should try to use "tools:remove" or "tools:replace" instead of "deleteAttr" if possible
    // 5. Also u can specify the value as part of finding params.
//    afterMerge("Remove application's allowBackup attr.")
//        .tag("application")
//        .attr("android:allowBackup")
//        .value("true")
//        .deleteAttr()

}
```

The configuration is separated by 3 parts:

1. To specify the hook entry which you can select from `beforeMerge(ruleName: String)` or `afterMerge(ruleName: String)`, `before` intercepts all merge inputs (all libraries, except the main application one), while `after` modifies the merged `AndroidManifest.xml`;
2. To specify the search params which you can pass `tag(name: String)` `attr(name: String)` `value(name: String)` (currently we haven't support regex), please pass as precise as you can to locate the element
3. To specify the delete type which you an select from `deleteTag()` or `deleteAttr()`, to be noticed, only one delete action will be executed, DO NOT call more than one `deleteXXX`

## Common issues:

> 1. Warning: AndroidManifest.xml already defines debuggable (in http://schemas.android.com/apk/res/android); using existing value in manifest.

That's because some out-of-date libraries set `debuggable` at AndroidManifest, but now we pass this setting from `build.gradle` / `build.gradle.kts` to AAPT.

> 2. Multiple entries with same key: @android:theme=REPLACE and android:theme=REPLACE  /  Multiple entries with same key: @android:allowBackup=REPLACE and android:allowBackup=REPLACE. 

There is a library which defined `android:allowBackup=true` conflicts with yours (`android:allowBackup=false`). You wanna to override it using `tools:replace="android:allowBackup"`, but find that `tools:replace="android:allowBackup"` is also present at lib's manifest, finally the conflict shows above. (Also see [this](http://stackoverflow.com/questions/35131182/manifest-merge-in-android-studio))   

> 3. Sometimes xmlns is wrote in application or any other tags except manifest tag, may cause aapt's 
concealed defect，like debuggable setting of build.gradle would not work;

Please check [this link](https://issuetracker.google.com/issues/66074488) for more info.

> 4. Error:
tools:replace specified at line:25 for attribute android:authorities, but no new value specified

Please check [this link](https://stackoverflow.com/questions/42893846/androidmanifest-merge-error-using-fileprovider) for more info.

## Compatible Specification

Polyfill is only supported & tested on latest **2** Minor versions of Android Gradle Plugin. Since `3.0.2`, the publish repository has been shifted to **Maven Central**.

| AGP Version |                                                           Latest Support Version                                                            |
|:-----------:|:-------------------------------------------------------------------------------------------------------------------------------------------:|
|    7.1.x    | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.2bab/seal/badge.svg)](https://search.maven.org/artifact/me.2bab/seal) |
|    7.0.x    |                                          [3.1.0](https://github.com/2BAB/Seal/releases/tag/3.1.0)                                           |
|    4.2.x    |                                          [3.0.2](https://github.com/2BAB/Seal/releases/tag/3.0.2)                                           |
|    3.0.x    |                                          [2.0.0](https://github.com/2BAB/Seal/releases/tag/2.0.0)                                           |
|    2.3.x    |                                          [1.1.0](https://github.com/2BAB/Seal/releases/tag/1.1.0)                                           |

(The project currently compiles with the latest version of AGP 7.0, and compiles and tests against the both AGP 7.0 and 7.1 on CI.)

## Why Seal use DOM parser API

[Oracle Docs: Comparing StAX to Other JAXP APIs](https://docs.oracle.com/javase/tutorial/jaxp/stax/why.html#bnbea)

Since we need to support "delete tag" feature, and export outputs simply, from the link above we can know DOM is easiest one to process that. Though it consumes more CPU and memory resources, luckily most of `AndroidManifest.xml` are not complex and with the help of Gradle we can cache the task result if those input(s) didn't change.


## License

    Copyright 2017-2022 2BAB

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

