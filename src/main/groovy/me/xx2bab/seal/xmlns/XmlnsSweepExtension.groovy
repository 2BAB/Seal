package me.xx2bab.seal.xmlns

/**
 * Created by 2bab on 2017/9/19.
 */
class XmlnsSweepExtension {

    boolean enabled

    Iterable<String> xmlnsShouldSweep

    XmlnsSweepExtension() {
        enabled = false
        xmlnsShouldSweep = []
    }

}
