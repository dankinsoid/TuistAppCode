package com.github.dankinsoid.tuistappcode.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.github.dankinsoid.tuistappcode.services.MyProjectService
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()
        project.basePath?.let {

            println(it)

            LocalFileSystem.getInstance().findFileByPath(it + "/Tuist/Dependencies.swift")?.let {
                FileDocumentManager.getInstance().getDocument(it)?.addDocumentListener(object : DocumentListener {
                    override fun documentChanged(event: DocumentEvent) {
                        print("Document changed ${event}")
                    }
                })
            }
        }
    }
}
