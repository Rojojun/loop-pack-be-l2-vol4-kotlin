package com.loopers

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.Architectures

@AnalyzeClasses(packages = ["com.loopers"], importOptions = [DoNotIncludeTests::class])
class ArchitectureTest {
    @ArchTest
    fun architecture(classes: JavaClasses?) {
        val application = "application"
        val domain = "domain"
        val infrastructure = "infrastructure"
        val interfaces = "interfaces"
        val support = "support"

        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .withOptionalLayers(true)
            .layer(application).definedBy(pkg(application))
            .layer(domain).definedBy(pkg(domain))
            .layer(infrastructure).definedBy(pkg(infrastructure))
            .layer(interfaces).definedBy(pkg(interfaces))
            .layer(support).definedBy(pkg(support))
            .whereLayer(interfaces).mayOnlyAccessLayers(application, domain, support)
            .whereLayer(application).mayOnlyAccessLayers(domain, support)
            .whereLayer(infrastructure).mayOnlyAccessLayers(domain, support)
            .whereLayer(domain).mayOnlyAccessLayers(support)
            .whereLayer(support).mayNotAccessAnyLayer()
            .check(classes)
    }

    companion object {
        private const val PREFIX = "com.loopers"

        private fun pkg(layer: String?): String {
            return "$PREFIX.$layer.."
        }
    }
}
