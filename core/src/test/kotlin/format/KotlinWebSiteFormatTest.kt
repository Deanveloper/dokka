package org.jetbrains.dokka.tests

import org.jetbrains.dokka.*
import org.jetbrains.kotlin.utils.addToStdlib.singletonOrEmptyList
import org.junit.Test

class KotlinWebSiteFormatTest {
    private val kwsService = KotlinWebsiteFormatService(InMemoryLocationService, KotlinLanguageService(), listOf(), DokkaConsoleLogger)

    @Test fun sample() {
        verifyKWSNodeByName("sample", "foo")
    }

    @Test fun returnTag() {
        verifyKWSNodeByName("returnTag", "indexOf")
    }

    @Test fun overloadGroup() {
        verifyKWSNodeByName("overloadGroup", "magic")
    }

    @Test fun dataTags() {
        val module = buildMultiplePlatforms("dataTags")
        verifyMultiplatformPackage(module, "dataTags")
    }

    @Test fun dataTagsInGroupNode() {
        val path = "dataTagsInGroupNode"
        val module = buildMultiplePlatforms(path)
        verifyModelOutput(module, ".md", "testdata/format/website/$path/multiplatform.kt") { model, output ->
            kwsService.createOutputBuilder(output, tempLocation).appendNodes(model.members.single().members.find { it.kind == NodeKind.GroupNode }.singletonOrEmptyList())
        }
        verifyMultiplatformPackage(module, path)
    }

    private fun verifyKWSNodeByName(fileName: String, name: String) {
        verifyOutput("testdata/format/website/$fileName.kt", ".md", format = "kotlin-website") { model, output ->
            kwsService.createOutputBuilder(output, tempLocation).appendNodes(model.members.single().members.filter { it.name == name })
        }
    }

    private fun buildMultiplePlatforms(path: String): DocumentationModule {
        val module = DocumentationModule("test")
        val options = DocumentationOptions("", "html", generateIndexPages = false)
        appendDocumentation(module, contentRootFromPath("testdata/format/website/$path/jvm.kt"), defaultPlatforms = listOf("JVM"), options = options)
        appendDocumentation(module, contentRootFromPath("testdata/format/website/$path/jre7.kt"), defaultPlatforms = listOf("JVM", "JRE7"), options = options)
        appendDocumentation(module, contentRootFromPath("testdata/format/website/$path/js.kt"), defaultPlatforms = listOf("JS"), options = options)
        return module
    }

    private fun verifyMultiplatformPackage(module: DocumentationModule, path: String) {
        verifyModelOutput(module, ".package.md", "testdata/format/website/$path/multiplatform.kt") { model, output ->
            kwsService.createOutputBuilder(output, tempLocation).appendNodes(model.members)
        }
    }

}
