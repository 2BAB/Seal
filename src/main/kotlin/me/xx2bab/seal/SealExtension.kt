package me.xx2bab.seal

import org.gradle.api.Named
import java.util.concurrent.atomic.AtomicInteger

abstract class SealExtension: Named {

    private val rules = mutableSetOf<SealRule>()
    private val idGenerator = AtomicInteger(0)

    override fun getName(): String {
        return "seal"
    }

    fun beforeMerge(ruleName: String = ""): SealRule {
        return SealRule(rules,
                SealRule.HookType.BEFORE_MERGE,
                ruleName,
                idGenerator.incrementAndGet())
    }

    fun afterMerge(ruleName: String = ""): SealRule {
        return SealRule(rules,
                SealRule.HookType.AFTER_MERGE,
                ruleName,
                idGenerator.incrementAndGet())
    }

}