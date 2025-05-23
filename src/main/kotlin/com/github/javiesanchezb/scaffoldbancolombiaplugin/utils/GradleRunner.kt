package com.github.javiesanchezb.scaffoldbancolombiaplugin.utils

import java.io.File

object GradleRunner {
    fun runGradleCommand(command: String, projectDir: String): String {
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        val gradleWrapper = if (isWindows) "gradlew.bat" else "gradlew"
        val wrapperFile = File(projectDir, gradleWrapper)

        // Validar existencia del wrapper
        check(wrapperFile.exists()) {
            "No se encontró el wrapper de Gradle ($gradleWrapper) en el proyecto.\n" +
                    "Por favor asegúrate de ejecutar 'gradle wrapper' o incluir los archivos necesarios."
        }


        // Preparar el comando
        val fullCommand = if (isWindows) {
            listOf("cmd", "/c", gradleWrapper) + command.split(" ")
        } else {
            listOf("./$gradleWrapper") + command.split(" ")
        }

        val process = ProcessBuilder(fullCommand)
            .directory(File(projectDir))
            .redirectErrorStream(true)
            .start()

        return process.inputStream.bufferedReader().readText()
    }
}
