package com.coderpage.app.gradle.plugin

import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author lc. 2017-09-26 22:59
 * @since 0.5.0 */
class Uploader implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def log = project.logger
        def hasAppPlugin = project.plugins.findPlugin(AppPlugin)
        if (!hasAppPlugin) {
            throw new IllegalStateException("The 'com.android.application' plugin is required")
        }

        def uploaderExtension = project.extensions.create("uploader", UploaderExtension)

        // http://tools.android.com/tech-docs/new-build-system/user-guide
        project.android.applicationVariants.all { variant ->
            if (uploaderExtension == null) {
                log.error("Please config your upload token in your build.gradle")
                return
            }

            def buildTypeName = variant.buildType.name.capitalize()
            println "buildTypeName:" + buildTypeName

            def productFlavorNames = variant.productFlavors.collect { it.name.capitalize() }
            if (productFlavorNames.isEmpty()) {
                productFlavorNames = [""]
            }
            def productFlavorName = productFlavorNames.join('')
            def varialtionName = "${productFlavorName}${buildTypeName}"

            def publishApkTaskName = "publishApk${varialtionName}"
            def outputData = variant.outputs.first()
            def assembleTask = variant.assemble
            def variantData = variant.variantData

            def publishApkTask = project.tasks.create(publishApkTaskName, UploadTask)
            publishApkTask.extension = uploaderExtension
            publishApkTask.variant = variant
            publishApkTask.description = "Upload the APK fro the ${varialtionName} build"
            publishApkTask.group = "myApkUploader"
            publishApkTask.dependsOn assembleTask

        }
    }
}
