package me.xx2bab.seal

import org.gradle.api.Named
import java.util.concurrent.atomic.AtomicInteger

abstract class SealExtension {

    val rules = mutableSetOf<SealRule>()
    private val idGenerator = AtomicInteger(0)


    fun beforeMerge(ruleName: String = ""): SealRuleBuilder {
        return SealRuleBuilder(rules,
                SealRuleBuilder.HookType.BEFORE_MERGE,
                ruleName,
                idGenerator.incrementAndGet())
    }

    fun afterMerge(ruleName: String = ""): SealRuleBuilder {
        return SealRuleBuilder(rules,
                SealRuleBuilder.HookType.AFTER_MERGE,
                ruleName,
                idGenerator.incrementAndGet())
    }

}