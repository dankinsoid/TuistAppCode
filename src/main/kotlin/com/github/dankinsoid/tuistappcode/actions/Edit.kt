package com.github.dankinsoid.tuistappcode.actions

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import java.nio.file.Path

class Edit: AnAction(), DumbAware {

    override fun actionPerformed(p0: AnActionEvent) {
        p0.project?.let { project ->
            deleteFile(project, "Manifests.xcworkspace")
            deleteFile(project, "Manifests.xcodeproj")
            TuistCLI(project).edit {
                if (!ProjectManager.getInstance().openProjects.map { it.name }.contains("Manifests")) {
                    project.basePath?.let {
                        ApplicationManager.getApplication().invokeLater {
                            ProjectUtil.tryOpenFiles(null, listOf(Path.of("$it/Manifests.xcworkspace")), it)
                        }
                    }
                }
            }
        }
    }

    private fun deleteFile(project: Project, name: String) {
        project.basePath?.let { it ->
            LocalFileSystem.getInstance().findFileByPath("$it/$name")?.let { file ->
                try {
                    LocalFileSystem.getInstance().deleteFile(name, file)
                } catch (_: Exception) {}
            }
        }
    }

    override fun update(e: AnActionEvent) {}
}
