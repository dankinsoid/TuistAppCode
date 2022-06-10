package com.github.dankinsoid.tuistappcode.actions

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class Clean: AnAction(), DumbAware {

    override fun actionPerformed(p0: AnActionEvent) {
        p0.project?.let {
            TuistCLI(it).clean()
        }
    }

    override fun update(e: AnActionEvent) {}
}
