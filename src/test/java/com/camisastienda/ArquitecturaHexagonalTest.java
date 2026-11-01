package com.camisastienda;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** Verifica las reglas de dependencia de la arquitectura hexagonal. */
class ArquitecturaHexagonalTest {

    private final JavaClasses clases = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.camisastienda");

    @Test
    void dominioNoDependeDeAplicacionNiInfraestructuraNiFrameworks() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..", "..infrastructure..", "org.springframework..", "jakarta.persistence..")
                .allowEmptyShould(true)
                .check(clases);
    }

    @Test
    void aplicacionNoDependeDeInfraestructura() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "jakarta.persistence..")
                .allowEmptyShould(true)
                .check(clases);
    }
}
