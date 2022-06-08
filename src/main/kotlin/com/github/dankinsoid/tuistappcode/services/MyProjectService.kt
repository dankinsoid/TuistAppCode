package com.github.dankinsoid.tuistappcode.services

import com.intellij.openapi.project.Project
import com.github.dankinsoid.tuistappcode.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
