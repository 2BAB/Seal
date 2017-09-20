package me.xx2bab.gradle.seal.xmlns

/**
 * Created by 2bab on 2017/9/19.
 */
class XmlnsSweeper {

    static final String TEMP_NS_PREFIX = "sealtempns"
    static final String STANDARD_NS_PREFIX = "xmlns"
    static final String ANDROID_MANIFEST_NAME = "AndroidManifest.xml"

    XmlnsSweepExtension extension

    XmlnsSweeper(XmlnsSweepExtension extension) {
        this.extension = extension
    }

    void sweep(String processManifestOutputFilePath) {
        if (!extension.enabled) {
            return
        }

        File manifestFile = new File(processManifestOutputFilePath)
        if (!manifestFile.exists() || manifestFile.name != ANDROID_MANIFEST_NAME) {
            return
        }
        String manifestFileContent = manifestFile.text
        StringBuilder builder = new StringBuilder(manifestFileContent)

        extension.xmlnsShouldSweep.each { String xmlnsItem ->

            String fullName = STANDARD_NS_PREFIX + ":" + xmlnsItem

            // maintain first xmlns
            int firstIndex = builder.indexOf(fullName)
            if (firstIndex < 0) {
                return
            }
            builder.replace(firstIndex, firstIndex + STANDARD_NS_PREFIX.length(), TEMP_NS_PREFIX)

            // replace all except first xmlns
            replaceAll(builder, fullName, "")

            // recovery first item
            replaceAll(builder, TEMP_NS_PREFIX, STANDARD_NS_PREFIX)

        }

        manifestFile.text = builder.toString()
    }

    static void replaceAll(StringBuilder sb, String regex, String replacement) {
        String aux = sb.toString()
        aux = aux.replaceAll(regex, replacement)
        sb.setLength(0)
        sb.append(aux)
    }


}
