# Seal 

[ ![Download](https://api.bintray.com/packages/2bab/maven/seal/images/download.svg) ](https://bintray.com/2bab/maven/seal/_latestVersion) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Seal is a gradle plugin to do precheck of Android Manifest. 

English | [中文说明](http://2bab.me/2017/05/23/gradle-daily-crash-aar-replace-conflict/)

**Notice:**

**Since most of developers has transformed to AAPT2 already, so I decide to update this plugin as well. Luckily, I found that Google has fixed almost all issues of AAPT1 in AAPT2, hence Seal is no longer needed (means for now AAPT2 works well until I find any other new issues I have to make a workaround by myself).** 

**I published Seal 2.x only because I found a new way to get the Manifest files automatically, it only works above 3.0.0 of Android Gradle Plugin. If you still use the AAPT1 and meet some problems on Manifest Merging, please use the [Seal@1.x](https://github.com/2BAB/Seal/tree/master)**

**In Summary, you don't need Seal 2.x so far. Just upgrade to AAPT2 and have fun. If you want to find a way to modify the manifest freely, check another project here: [Weaving](https://github.com/2BAB/Weaving).**

## Conflict / Warning When Manifest Merge 

As we all know, Android provides a tool names [AndroidManifest Merger](https://developer.android.com/studio/build/manifest-merge.html) to manage manifests-merge. But these rules are not enough for some situation like below:   

> 1. Warning: AndroidManifest.xml already defines debuggable (in http://schemas.android.com/apk/res/android); using existing value in manifest.

That's because some out-of-date libraries set `debuggable` at AndroidManifest, but now we use `build.gradle` to do it. 

> 2. Multiple entries with same key: @android:theme=REPLACE and android:theme=REPLACE  /  Multiple entries with same key: @android:allowBackup=REPLACE and android:allowBackup=REPLACE. 

There is a library which defined `android:allowBackup=true` conflicts with yours (`android:allowBackup=false`). You wanna to override it using `tools:replace="android:allowBackup"`, but find that `tools:replace="android:allowBackup"` is also present at lib's manifest, finally the conflict shows above. (Also see [this](http://stackoverflow.com/questions/35131182/manifest-merge-in-android-studio))   

> 3. Sometimes xmlns is wrote in application or any other tags except manifest tag, may cause aapt's 
concealed defect，like debuggable setting of build.gradle would not work;

About this issue, more information [here](http://2bab.me/2017/09/19/gradle-daily-crash-debuggable-not-work/).

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
        classpath 'com.android.tools.build:gradle:3.2.0'
        classpath 'me.2bab:seal:2.0.0'
    }
}

...

// app's build.gradle
apply plugin: 'me.2bab.seal'

```

2. Configurations:

``` gradle
// remove some attrs of application tag
def removeAttrs = [
        'android:debuggable'
]

// remove some value of application's replace attr
def replaceValues = [
        'android:allowBackup'
]

// sweep useless xmlns except the first one
def sweepXmlns = [
        'android=\"http://schemas.android.com/apk/res/android\"'
]


seal {
    enabled = true

    appAttrs {
        enabled = true
        attrsShouldRemove = removeAttrs
    }

    appReplaceValues {
        enabled = true
        valuesShouldRemove = replaceValues
    }
    
    xmlnsSweep {
        enabled = true
        xmlnsShouldSweep = sweepXmlns
    }

}
```

Note: 

- If `build-cache` is enable (for those who update Android Gradle Plugin to above 3.0.0, this feature is enabled by default), Seal recommends that custom build cache folder placed in the Project Folder to avoid impact other projects while you are modifying the manifest of libraries. 
 
    ```
    //gradle.properties
    android.buildCacheDir=./build-cache
    ...
    ```

## Sample

### precheck

A library's AndroidManifest.xml before precheck:

``` xml
<application
        android:allowBackup="true"
        android:debuggable="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        tools:replace="android:allowBackup" >
        ...
</application>
```

And after precheck:

``` xml
<application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
</application>
```

### post process (for now only xmlns sweep)

After `process{variant}Manifest` task, a intermediate (AndroidManifest.xml) file will generate at /application_module/build/intermediate/manifests, so we sweep it at this time before aapt's processing.  

origin:

``` xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.debuggbaletest"
    android:versionCode="1"
    android:versionName="1.0" >

    <uses-sdk
        android:minSdkVersion="16"
        android:targetSdkVersion="26" />

    <meta-data
        android:name="android.support.VERSION"
        android:value="26.0.0-alpha1" />

    <application
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme" >
        <activity android:name="com.example.debuggbaletest.MainActivity" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

after sweep:


``` xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.debuggbaletest"
    android:versionCode="1"
    android:versionName="1.0" >

    <uses-sdk
        android:minSdkVersion="16"
        android:targetSdkVersion="26" />

    <meta-data
        android:name="android.support.VERSION"
        android:value="26.0.0-alpha1" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme" >
        <activity android:name="com.example.debuggbaletest.MainActivity" >
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

The xmlns of application tag cleared by Seal.

## Changelog

### v2.0.0
- Support getting Manifest files automatically
- Support AAPT2 only
- This version is only for testing since AAPT2 works well so far until I find some bugs that I have to make a workaround by myself

### v1.1.0

- Support Sweeping useless xmlns (which may cause aapt's concealed defect)

### v1.0.0

- Support Removing Application Attributes 
- Support Removing Application's `tools:replace` value
  
## License

    Copyright 2018 2BAB

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

