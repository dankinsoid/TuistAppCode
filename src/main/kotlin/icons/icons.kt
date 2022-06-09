package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

interface TuistPluginIcons {
    companion object {
        val TUIST_ACTION: Icon = IconLoader.getIcon("/icons/tuist.svg", ClassLoader.getSystemClassLoader())
    }
}