package com.github.dankinsoid.tuistappcode.listeners

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import java.util.*

internal class ManifestsProjectListener : ProjectManagerListener {

    private val changedFiles: MutableSet<String> = mutableSetOf()
    private val dependenciesName = "Dependencies"
    private val projectName = "Manifests"
    private var lastUpdateDate = Date()

    override fun projectOpened(project: Project) {
        if (project.name != projectName) {
            return
        }
        val bus = project.messageBus

        bus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {

            override fun before(events: MutableList<out VFileEvent>) {
                changedFiles.addAll(
                    events
                        .filter {
                            it.isFromSave && it.file?.extension == "swift"
                        }
                        .mapNotNull { it.file?.name }
                )
                updateWithThrottle(project)
            }
        })
    }

    override fun projectClosed(project: Project) {
        if (project.name != projectName) {
            return
        }
        update(project)
        if (project.basePath?.contains(".idea") != true) {
            return
        }
        delete(project)
    }

    private fun delete(project: Project) {
        val projectPath = project.basePath ?: return
        val file = LocalFileSystem.getInstance().findFileByPath(projectPath) ?: return
        try {
            LocalFileSystem.getInstance().deleteFile(project.name, file)
        } catch (_: Exception) {}
    }

    private fun updateWithThrottle(project: Project) {
        val now = Date()
        if (now.time - lastUpdateDate.time > 5_000 && changedFiles.isNotEmpty()) {
            update(project)
            lastUpdateDate = now
        }
    }

    private fun update(project: Project) {
        if (changedFiles.contains(dependenciesName)) {
            fetch(project) {
                generate(project)
            }
        } else if (changedFiles.isNotEmpty()) {
            generate(project)
        }
    }

    private fun generate(project: Project) {
        tuist(project).generate {
            changedFiles.clear()
        }
    }

    private fun fetch(project: Project, onSuccess: () -> Unit) {
        tuist(project).fetch {
            changedFiles.remove(dependenciesName)
            onSuccess()
        }
    }

    private fun tuist(project: Project): TuistCLI = TuistCLI(project, rootPath(project), true)

    private fun rootPath(project: Project): String? {
        val projectPath = project.basePath ?: return null
        return if (projectPath.contains(".idea")) {
            projectPath.substringBeforeLast("/.idea")
        } else {
            null
        }
    }
}
