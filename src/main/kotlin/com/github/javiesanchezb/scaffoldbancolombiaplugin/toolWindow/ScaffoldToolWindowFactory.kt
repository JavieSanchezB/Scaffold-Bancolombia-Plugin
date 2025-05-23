package com.github.javiesanchezb.scaffoldbancolombiaplugin.toolWindow

import com.github.javiesanchezb.scaffoldbancolombiaplugin.utils.GradleRunner
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import com.intellij.util.SVGLoader.load
import java.awt.GridLayout
import javax.swing.*

class ScaffoldToolWindowFactory : ToolWindowFactory {

    private fun loadIcon(path: String): Icon? {
        return try {
            val resource = javaClass.classLoader.getResource(path)
            println("Intentando cargar ícono desde: $path")
            println("Resource URL: $resource")
            resource?.let {
                load(it, 1.0f).let { svg ->
                    com.intellij.util.ui.JBImageIcon(svg)
                }
            }.also { 
                if (it == null) {
                    System.err.println("No se pudo cargar el ícono: $path")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Error al cargar el ícono $path: ${e.message}")
            null
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = JPanel(GridLayout(0, 1, 5, 5))

        // Carga de íconos
        val iconProject = loadIcon("icons/project.svg")
        val iconModel = loadIcon("icons/model.svg")
        val iconUseCase = loadIcon("icons/usecase.svg")
        val iconAdapter = loadIcon("icons/adapter.svg")
        val iconEntry = loadIcon("icons/entry.svg")
        val iconHelper = loadIcon("icons/helper.svg")
        val iconPipeline = loadIcon("icons/pipeline.svg")
        val iconAcceptance = loadIcon("icons/acceptance.svg")
        val iconPerformance = loadIcon("icons/performance.svg")
        val iconDelete = loadIcon("icons/delete.svg")
        val iconUpdate = loadIcon("icons/update.svg")
        val iconstruct = loadIcon("icons/struct.svg")

        // Botones con íconos
        panel.add(createTwoInputButton("Generar Proyecto", "Type (reactive/imperative)", "Nombre del proyecto", iconProject) { type, name ->
            val output = GradleRunner.runGradleCommand("cleanArchitecture --type=$type --name=$name", project.basePath ?: return@createTwoInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Generar Modelo", "Nombre del modelo", iconModel) { name ->
            val output = GradleRunner.runGradleCommand("generateModel --name=$name", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Generar Caso de Uso", "Nombre del caso de uso", iconUseCase) { name ->
            val output = GradleRunner.runGradleCommand("generateUseCase --name=$name", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createButton("Validar Estructura", iconstruct) {
            val output = GradleRunner.runGradleCommand("validateStructure", project.basePath ?: return@createButton)
            showResult(project, output)
        })

        panel.add(createTwoInputButton("Generate Driven Adapter", "Type (generic, asynceventbus, binstash, cognitokenprovider, dynamodb, jpa, kms, mongodb, mq, r2dbc, redis, restconsumer, rsocket, s3, secrets, sqs)", "Name", iconAdapter) { type, name ->
            val output = GradleRunner.runGradleCommand("generateDrivenAdapter --type=$type --name=$name", project.basePath ?: return@createTwoInputButton)
            showResult(project, output)
        })

        panel.add(createTwoInputButton("Generate Entry Point", "Type (generic, asynceventhandler, graphql, kafka, mq, restmvc, rsocket, sqs, webflux)", "Name", iconEntry) { type, name ->
            val output = GradleRunner.runGradleCommand("generateEntryPoint --type=$type --name=$name", project.basePath ?: return@createTwoInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Generate Helper", "Nombre del helper", iconHelper) { name ->
            val output = GradleRunner.runGradleCommand("generateHelper --name=$name", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createTwoInputButton("Generate Pipeline", "Type (azure, circleci, github, jenkins)", "Nombre del pipeline", iconPipeline) { type, name ->
            val output = GradleRunner.runGradleCommand("generatePipeline --type=$type --name=$name", project.basePath ?: return@createTwoInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Generate Acceptance Test", "Nombre del test", iconAcceptance) { name ->
            val output = GradleRunner.runGradleCommand("generateAcceptanceTest --name=$name", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Generate Performance Test", "Type (jmeter)", iconPerformance) { type ->
            val output = GradleRunner.runGradleCommand("generatePerformanceTest --type=$type", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createInputButton("Delete Module", "Nombre del módulo a eliminar", iconDelete) { name ->
            val output = GradleRunner.runGradleCommand("deleteModule --module=$name", project.basePath ?: return@createInputButton)
            showResult(project, output)
        })

        panel.add(createButton("Update Project", iconUpdate) {
            val output = GradleRunner.runGradleCommand("updateCleanArchitecture", project.basePath ?: return@createButton)
            showResult(project, output)
        })

        val scrollPane = JBScrollPane(panel)
        val content = ContentFactory.getInstance().createContent(scrollPane, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createInputButton(
        text: String,
        label: String,
        icon: Icon? = null,
        action: (String) -> Unit
    ): JButton {
        return JButton(text, icon).apply {
            addActionListener {
                val input = Messages.showInputDialog(null, label, text, Messages.getQuestionIcon())
                if (input.isNullOrBlank()) return@addActionListener
                action(input)
            }
        }
    }

    private fun createButton(
        text: String,
        icon: Icon? = null,
        action: () -> Unit
    ): JButton {
        return JButton(text, icon).apply {
            addActionListener { action() }
        }
    }

    private fun createTwoInputButton(
        text: String,
        firstLabel: String,
        secondLabel: String,
        icon: Icon? = null,
        action: (String, String) -> Unit
    ): JButton {
        return JButton(text, icon).apply {
            addActionListener {
                val first = Messages.showInputDialog(null, firstLabel, text, Messages.getQuestionIcon())
                if (first.isNullOrBlank()) return@addActionListener
                val second = Messages.showInputDialog(null, secondLabel, text, Messages.getQuestionIcon())
                if (second.isNullOrBlank()) return@addActionListener
                action(first, second)
            }
        }
    }

    private fun showResult(project: Project, result: String) {
        Messages.showMessageDialog(project, result, "Resultado", Messages.getInformationIcon())
    }
}
