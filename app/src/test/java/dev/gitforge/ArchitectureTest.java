package dev.gitforge;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;

@AnalyzeClasses(packages = "dev.gitforge", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {}
