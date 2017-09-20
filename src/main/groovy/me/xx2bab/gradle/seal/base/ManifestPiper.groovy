package me.xx2bab.gradle.seal.base

/**
 * Created by 2bab
 */
class ManifestPiper {

    Node manifestRoot

    ManifestPiper pipe(IManifestPreChecker checker) {
        if (!checker.isEnabled()) {
            println(Constants.TAG + ": ${checker.class.simpleName} is disabled.")
        }
        checker.check(manifestRoot)
        return this
    }

    boolean dest(String destPath) {
        dest(new File(destPath))
    }

    boolean dest(File destFile) {
        new XmlNodePrinter(new PrintWriter(new FileWriter(destFile))).print(manifestRoot)
    }

    ManifestPiper(File manifestFile) {
        try {
            manifestRoot = new XmlParser().parse(manifestFile.absolutePath)
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception(Constants.TAG + ": ${manifestFile.absolutePath} file parsed error!")
        }
    }

    ManifestPiper(String manifestPath) {
        this(new File(manifestPath))
    }

}
