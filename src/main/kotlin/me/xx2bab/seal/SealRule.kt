package me.xx2bab.seal

import java.util.concurrent.atomic.AtomicBoolean

class SealRule(private val rules: MutableSet<SealRule>,
               val hookType: HookType,
               var ruleName: String,
               val ruleId: Int) {

    enum class HookType {
        BEFORE_MERGE, AFTER_MERGE
    }

    enum class DeleteType {
        TAG, ATTR, VALUE
    }

    private var tag: String? = null
    private var attr: String? = null
    private var value: String? = null
    private val deleteActionSettled = AtomicBoolean(false)
    private var deleteType: DeleteType = DeleteType.TAG

    init {
        if (ruleName.isBlank()) {
            ruleName = ruleId.toString()
        }
    }

    fun tag(tagFilterRegex: String): SealRule {
        tag = tagFilterRegex
        return this
    }

    fun attr(attrFilterRegex: String): SealRule {
        attr = attrFilterRegex
        return this
    }

    fun value(valueFilterRegex: String): SealRule {
        value = valueFilterRegex
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

    fun deleteValue() {
        deleteActionPreCheck()
        build(DeleteType.VALUE)
    }

    private fun deleteActionPreCheck() {
        if (deleteActionSettled.get()) {
            throw IllegalStateException("Delete action has been called once, " +
                    "please avoid adding multiple delete actions in one rule.")
        }
    }

    private fun build(deleteType: DeleteType) {
        this.deleteType = deleteType
        rules.add(this)
    }

}