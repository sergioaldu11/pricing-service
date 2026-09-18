package com.inditex.pricing;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
        packages = "com.inditex.pricing",
        importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule domainHasNoFrameworkDependencies =
            classes()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage("java..", "..domain..");

    @ArchTest
    static final ArchRule applicationDependsOnlyOnTheCore =
            classes()
                    .that()
                    .resideInAPackage("..application..")
                    .should()
                    .onlyDependOnClassesThat()
                    .resideInAnyPackage("java..", "..domain..", "..application..");

    @ArchTest
    static final ArchRule adaptersAreIndependent =
            noClasses()
                    .that()
                    .resideInAPackage("..adapters.in..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..adapters.out..");

    @ArchTest
    static final ArchRule persistenceDoesNotDependOnHttp =
            noClasses()
                    .that()
                    .resideInAPackage("..adapters.out..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..adapters.in..", "org.springframework.http..");

    @ArchTest
    static final ArchRule topLevelPackagesAreAcyclic =
            slices().matching("com.inditex.pricing.(*)..").should().beFreeOfCycles();
}
