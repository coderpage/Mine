package com.coderpage.app.gradle.plugin

import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author lc. 2017-09-26 23:06
 * @since 0.5.0 */


class UploadTask extends DefaultTask {
    UploaderExtension extension
    ApplicationVariant variant
    Version version

    @TaskAction
    def uploadApk() {

        version = new Version()
        version.token = extension.token
        version.appName = extension.appName
        version.changeLog = extension.changeLog
        version.isRelease = extension.isRelease

        def apkOutput = variant.outputs.find { variantOutput -> variantOutput instanceof ApkVariantOutput }

        String apkPath = apkOutput.outputFile.getAbsolutePath()
        version.apkPath = apkPath
        println "apkPath ===> " + apkPath

        UploadClient uploadClient;
        if (extension.apiServer != null && !"".equals(extension.apiServer)) {
            uploadClient = new UploadClient(extension.apiServer);
        } else {
            uploadClient = new UploadClient();
        }
        uploadClient.uploadApk(version)
    }
}