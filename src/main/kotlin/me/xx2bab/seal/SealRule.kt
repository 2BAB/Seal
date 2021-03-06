package me.xx2bab.seal

import org.gradle.api.tasks.Input
import java.util.concurrent.atomic.AtomicBoolean

class SealRule {

    @get:Input
    var ruleId = -1

    @get:Input
    var ruleName = ""

    @get:Input
    var tag = ""

    @get:Input
    var attr = ""

    @get:Input
    var value = ""

    @get:Input
    var hookType = ""

    @get:Input
    var deleteType = ""
}

class SealRuleBuilder(private val rules: MutableSet<SealRule>,
                      private val hookType: HookType,
                      private var ruleName: String,
                      private val ruleId: Int) {

    enum class HookType {
        BEFORE_MERGE, AFTER_MERGE
    }

    enum class DeleteType {
        TAG, ATTR
    }

    private var tag: String? = null
    private var attr: String? = null
    private var value: String? = null

    private var multiResult: Boolean = false

    private val deleteActionSettled = AtomicBoolean(false)

    init {
        if (ruleName.isBlank()) {
            ruleName = "Rule #${ruleId}"
        }
    }

    fun tag(tagFilter: String): SealRuleBuilder {
        tag = tagFilter
        return this
    }

    fun attr(attrFilter: String): SealRuleBuilder {
        attr = attrFilter
        return this
    }

    fun value(valueFilter: String): SealRuleBuilder {
        value = valueFilter
        return this
    }

    fun multiResult(): SealRuleBuilder {
        this.multiResult = true
        return this
    }

    fun deleteTag() {
        deleteActionPreCheck()
        build(DeleteType.TAG)
    }

    fun deleteAttr() {
        deleteActionPreCheck()
        build(DeleteType.ATTR)
    }

    private fun deleteActionPreCheck() {
        if (deleteActionSettled.get()) {
            throw IllegalStateException("Delete action has been called once, " +
                    "please avoid adding multiple delete actions in one rule.")
        }
    }

    private fun build(deleteType: DeleteType) {
        val sealRule = SealRule()
        ruleId.apply { sealRule.ruleId = this }
        ruleName.apply { sealRule.ruleName = this }
        tag?.apply { sealRule.tag = this }
        attr?.apply { sealRule.attr = this }
        value?.apply { sealRule.value = this }
        hookType.apply { sealRule.hookType = this.name }
        deleteType.apply { sealRule.deleteType = this.name }
        rules.add(sealRule)
    }

}