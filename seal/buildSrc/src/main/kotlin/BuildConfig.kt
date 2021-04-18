import java.util.*

object BuildConfig {

    val props = Properties()

    init {
        javaClass.classLoader.getResourceAsStream("versions.properties")
            .use { props.load(it) }
    }

    object Deps {
        const val ktStd = "stdlib-jdk8"
        val agp by lazy { "com.android.tools.build:gradle:${props["agpVersion"]}" }
        const val polyfill = "me.2bab:polyfill:0.2.0"

        // Test
        const val junit = "junit:junit:4.12"
        const val mockito = "org.mockito:mockito-core:3.9.0"
        const val mockitoInline = "org.mockito:mockito-inline:3.9.0"

        const val fastJson = "com.alibaba:fastjson:1.2.73"
        const val zip4j = "net.lingala.zip4j:zip4j:2.6.2"
    }

}