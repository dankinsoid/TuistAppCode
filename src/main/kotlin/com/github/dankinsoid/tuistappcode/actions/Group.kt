package com.github.dankinsoid.tuistappcode.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class Group: DefaultActionGroup() {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }
}