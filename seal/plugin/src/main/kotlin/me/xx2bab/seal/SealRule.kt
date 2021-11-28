package me.xx2bab.seal

import kotlinx.serialization.Serializable
import org.gradle.api.tasks.Input

@Serializable
data class SealRule(
    @Input var ruleId: Int = -1,
    @Input var ruleName: String = "",
    @Input var tag: String = "",
    @Input var attr: String = "",
    @Input var value: String = "",
    @Input var hookType: String = "",
    @Input var deleteType: String = "",
)

