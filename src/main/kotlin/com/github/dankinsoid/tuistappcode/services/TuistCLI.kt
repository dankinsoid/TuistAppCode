package com.github.dankinsoid.tuistappcode.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import java.io.File
import java.nio.charset.Charset

class TuistCLI(project: Project) {
    val project = project

    fun generate(onSuccess: () -> Unit = {}) {
        execute("generate", "Generating project", "-n") {
            onSuccess()
        }
    }

    fun fetch(onSuccess: () -> Unit = {}) {
        execute("fetch", "Fetching dependencies") {
            onSuccess()
        }
    }

    fun clean(onSuccess: () -> Unit = {}) {
        execute("clean", "Cleaning project") {
            onSuccess()
        }
    }

    fun edit(onSuccess: () -> Unit = {}) {
        execute("edit", "Creating manifest project", "--permanent") {
            onSuccess()
        }
    }

    fun build(onSuccess: () -> Unit = {}) {
        execute("build", "Building project") {
            onSuccess()
        }
    }

    fun test(onSuccess: () -> Unit = {}) {
        execute("test", "Testing project") {
            onSuccess()
        }
    }

    fun execute(command: String, title: String, vararg arguments: String, onSuccess: () -> Unit = {}) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, title) {
            override fun run(progressIndicator: ProgressIndicator) {
                val output = execute(commandLine(command, *arguments), {
                    progressIndicator.text = it.text
                    progressIndicator.fraction += (1 - progressIndicator.fraction) / 4
                }, {
                    progressIndicator.fraction = 1.0
                })
                if (output.exitCode == 0) {
                    onSuccess()
                }
            }
        })
    }

    private fun execute(commandLine: GeneralCommandLine, onText: (event: ProcessEvent) -> Unit = {}, onTerminate: (event: ProcessEvent) -> Unit = {}): ProcessOutput {
        val output = CapturingProcessHandler(commandLine).also { processHandler ->
            processHandler.addProcessListener(object : ProcessAdapter() {

                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    onText(event)
                }

                override fun processTerminated(event: ProcessEvent) {
                    onTerminate(event)
                }
            })
        }.runProcess()
        if (output.exitCode != 0) {
            Notification(
                "TuistNotifications",
                "Tuist ${commandLine.parametersList.list.joinToString(" ")}",
                output.stderrLines.joinToString("\n"),
                NotificationType.ERROR
            ).notify(project)
        }
        return output
    }

    private fun commandLine(command: String, vararg arguments: String): GeneralCommandLine {
        val commandLine = GeneralCommandLine("tuist")

        commandLine.workDirectory = File(project.basePath ?: "")
        commandLine.charset = Charset.forName("UTF-8")
        commandLine.addParameter(command)
        arguments.forEach { commandLine.addParameter(it) }

        return commandLine
    }
}