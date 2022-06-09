package com.github.dankinsoid.tuistappcode.actions

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.ProjectManager

class Edit: AnAction(), DumbAware {

    override fun actionPerformed(p0: AnActionEvent) {
        p0.project?.let { project ->
            TuistCLI(project).edit {
                if (!ProjectManager.getInstance().openProjects.map { it.name }.contains("Manifests")) {
                    ProjectManager.getInstance().loadAndOpenProject((project.basePath ?: "") + "/Manifests.xcodeproj")
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {}
}
