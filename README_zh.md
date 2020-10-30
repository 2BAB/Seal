<img src="./seal-banner.png" alt="Seal" width="359px">

[![Download](https://api.bintray.com/packages/2bab/maven/seal/images/download.svg)](https://bintray.com/2bab/maven/seal/_latestVersion) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)


[English](./README.md) | 中文说明

Seal 是一款处理 AndroidManifest.xml 合并冲突的 Gradle 插件。

需要注意：除了移除标签外，其他删除/更新功能都应该优先考虑 “tools:replace”，“tools:remove” 和其他官方合并器（ManifestMerger）中有的功能。

从功能上来说，Seal 提供的功能更像是一个 **silver bullet**（但可能有人会觉得治标不治本），来**拯救被 ManifestMerger 
阻碍的紧急发布**。开发们有责任向库的开发者（写出这个有问题的 Manifest 的人）, ManifestMerger(Google), AAPT2(Google) 报告bug，因为那才是解决合并冲突的根本方法。

## 快速开始

1. 编译Seal插件:

``` kotlin
// 根项目的 build.gradle.kts
buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha15")
        classpath("me.2bab:seal:3.0.1")
    }
}
```

2. 使用插件:

``` kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    // 引入 Seal 插件
    id("me.2bab.seal")
}
```

3. 配置插件:

``` kotlin
seal {

    // 0. 两个 manifest 合并之前的例子
    beforeMerge("Remove description attr for library input Manifest.")
        .tag("application")
        .attr("android:description")
        .deleteAttr()
    beforeMerge("Remove problematic replace attr for library input Manifest.")
        .tag("application")
        .attr("tools:replace")
        .deleteAttr()

    // 对合并后的 manifest 操作的完整示例（1-5）。
    // 1. 这样的操作十分危险，请尽可能地指定详细的属性和值
    afterMerge("Remove all uses-feature tags.")
        .tag("uses-feature")
        .deleteTag()

    // 2. 这样的操作十分危险，请尽可能地指定详细的值
    afterMerge("Remove all custom permission tags.")
        .tag("permission")
        .attr("android:protectionLevel")
        .deleteTag()

    // 3. 这是我们推荐删除tag的方式
    afterMerge("Remove invalid service tag.")
        .tag("service")
        .attr("android:name")
        .value("me.xx2bab.seal.sample.library.LegacyService")
        .deleteTag()

    // 你应该尽可能使用 "tools:remove" 或 "tools:replace" 来替代 "deleteAttr"
    // 4. 删除属性和它的值
    afterMerge("Remove application's allowBackup attr.")
        .tag("application")
        .attr("android:allowBackup")
        .deleteAttr()

    // 你应该尽可能使用 "tools:remove" 或 "tools:replace" 来替代 "deleteAttr"
    // 5. 你也能指定值作为查询参数的一部分
//    afterMerge("Remove application's allowBackup attr.")
//        .tag("application")
//        .attr("android:allowBackup")
//        .value("true")
//        .deleteAttr()

}
```

整体配置分为三个部分：

1. 选择 `beforeMerge(ruleName: String)`或`afterMerge(ruleName: String)` 作为 hook 的入口
2. 通过传入 `tag(name: String)` `attr(name: String)` `value(name: String)` 作为指定的查询参数 （目前还未支持正则表达式），请尽可能的精确以确保定位到相应的元素
3. 选择 `deleteTag()` 或 `deleteAttr()` 中的一个来指定删除的类型，需要注意的是，只有一个删除方法会被执行，不要调用超过一个 `deleteXXX` 方法

## 常见问题:

> 1. Warning: AndroidManifest.xml already defines debuggable (in http://schemas.android.com/apk/res/android); using existing value in manifest.

这是因为一些过时的库在清单文件中设置了 `debuggable`，但是现在我们把这个属性从 `build.gradle`/`build.gradle.kts` 传递到AAPT。


> 2. Multiple entries with same key: @android:theme=REPLACE and android:theme=REPLACE  /  Multiple entries with same key: @android:allowBackup=REPLACE and android:allowBackup=REPLACE. 

这是因为你使用的一个库定义了 `android:allowBackup=true` 和你自己定义的属性相冲突(`android:allowBackup=false`)。你想使用 `tools:replace="android:allowBackup"` 来覆盖它，但是发现 `tools:replace="android:allowBackup"` 同样出现在那个库的清单文件中，所以出现了上面的冲突。(另见 [this](http://stackoverflow.com/questions/35131182/manifest-merge-in-android-studio))

> 3. Sometimes xmlns is wrote in application or any other tags except manifest tag, may cause aapt's 
concealed defect，like debuggable setting of build.gradle would not work;

点击[链接](https://issuetracker.google.com/issues/66074488)查看详情。

> 4. Error:
tools:replace specified at line:25 for attribute android:authorities, but no new value specified

点击[链接](https://stackoverflow.com/questions/42893846/androidmanifest-merge-error-using-fileprovider)查看详情。

## 版本适配

Polyfill 只支持并在最新的两个 Android Gradle Plugin 版本（例如 4.2.x，4.3.x）进行测试。

AGP 版本号| 最新支持版本号
:-----------:|:-----------------:
4.2.x | 3.0.1
3.0.x | [2.0.0](https://github.com/2BAB/Seal/tree/2.0.0)
2.3.x | [1.1.0](https://github.com/2BAB/Seal/tree/1.1.0)

## 为什么 Seal 使用 DOM 解析 API 

[Oracle文档: Comparing StAX to Other JAXP APIs](https://docs.oracle.com/javase/tutorial/jaxp/stax/why.html#bnbea)

因为我们需要支持“删除标签”功能，而且保持输出的简单化，故从以上的链接我们可以知道 DOM 是实现这两个功能最简单的方式。尽管它花费了更多的 CPU 和内存资源，但幸运的是大多数 AndroidManifest.xml 文件都不复杂，并且输入不改变的情况下我们还可以通过 Gradle 缓存 task 的结果。

## License

    Copyright 2017-2020 2BAB

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.