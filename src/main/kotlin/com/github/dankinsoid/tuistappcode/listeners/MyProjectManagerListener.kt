package com.github.dankinsoid.tuistappcode.listeners

import com.github.dankinsoid.tuistappcode.services.TuistCLI
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.WindowManager
import java.util.*

internal class MyProjectManagerListener : ProjectManagerListener {

    private val changedFiles: MutableSet<String> = mutableSetOf()
    private val dependenciesName = "Dependencies"
    private var lastUpdateDate = Date()

    override fun projectOpened(project: Project) {

        println(ProjectManager.getInstance().openProjects.map { it.name })
        println(project.name)
        if (project.name != "Manifests") {
            return
        }
        val bus = project.messageBus

        bus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {

            override fun before(events: MutableList<out VFileEvent>) {
                println(events.map { it.isFromSave })
                changedFiles.addAll(
                    events
                        .filter {
                            it.file?.extension == "swift" && it.isFromSave
                        }
                        .mapNotNull { it.file?.name }
                )
                updateWithThrottle(project)
            }
        })
    }

    override fun projectClosed(project: Project) {
        update(project)
    }

    private fun updateWithThrottle(project: Project) {
        val now = Date()
        if (now.time - lastUpdateDate.time > 5_000 && !changedFiles.isEmpty()) {
            update(project)
            lastUpdateDate = now
        }
    }

    private fun update(project: Project) {
        println("update")
        if (changedFiles.contains(dependenciesName)) {
            fetch(project) {
                generate(project)
            }
        } else if (changedFiles.isNotEmpty()) {
            generate(project)
        }
    }

    private fun generate(project: Project) {
        TuistCLI(project).generate {
            changedFiles.clear()
        }
    }

    private fun fetch(project: Project, onSuccess: () -> Unit) {
        TuistCLI(project).fetch {
            changedFiles.remove(dependenciesName)
            onSuccess()
        }
    }
}
