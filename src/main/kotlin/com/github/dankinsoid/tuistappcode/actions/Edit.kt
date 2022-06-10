package com.github.dankinsoid.tuistappcode.actions

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import java.nio.file.Path

class Edit: AnAction(), DumbAware {

    override fun actionPerformed(p0: AnActionEvent) {
        val project = p0.project ?: return
        val projectDir = project.basePath ?: return
        val projectFile = File("${projectDir}/.idea")
        deleteFile(project, ".idea/Manifests")
        val file = FileUtilRt.createTempDirectory(projectFile, "Manifests", null, true)
        TuistCLI(project).edit(file.absolutePath) {
            if (!ProjectManager.getInstance().openProjects.map { it.name }.contains("Manifests")) {
                ApplicationManager.getApplication().invokeLater {
                    ProjectUtil.tryOpenFiles(null, listOf(Path.of("${file.path}/Manifests.xcworkspace")), projectDir)
                }
            }
        }
    }

    private fun deleteFile(project: Project, name: String) {
        val projectDir = project.basePath ?: return
        val file = LocalFileSystem.getInstance().findFileByPath("$projectDir/$name") ?: return
        try {
            LocalFileSystem.getInstance().deleteFile(name, file)
        } catch (_: Exception) {}
    }

    override fun update(e: AnActionEvent) {}
}
