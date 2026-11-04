package com.camisastienda;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** Verifica las reglas de dependencia de la arquitectura hexagonal. */
class ArquitecturaHexagonalTest {

    private final JavaClasses clases = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.camisastienda");

    // ── Dirección de las dependencias ───────────────────────────────────────────

    @Test
    void dominioNoDependeDeAplicacionNiInfraestructuraNiFrameworks() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..application..", "..infrastructure..", "org.springframework..", "jakarta.persistence..")
                .check(clases);
    }

    @Test
    void aplicacionNoDependeDeInfraestructura() {
        noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "jakarta.persistence..")
                .check(clases);
    }

    // ── Puertos de entrada: los adaptadores de entrada solo conocen la interfaz ─

    @Test
    void adaptadoresDeEntradaUsanPuertosYNoCasosDeUsoNiAdaptadoresDeSalida() {
        noClasses().that().resideInAPackage("..adapter.in..")
                .should().dependOnClassesThat().resideInAnyPackage("..application.usecase..", "..adapter.out..")
                .check(clases);
    }

    @Test
    void puertosDeEntradaYSalidaSonInterfaces() {
        classes().that().resideInAnyPackage("..application.port.in..", "..application.port.out..")
                .should().beInterfaces()
                .check(clases);
    }

    @Test
    void casosDeUsoImplementanUnPuertoDeEntrada() {
        classes().that().resideInAPackage("..application.usecase..")
                .and().areTopLevelClasses()
                .should().implement(JavaClass.Predicates.resideInAPackage("..application.port.in.."))
                .check(clases);
    }

    // ── Puertos de salida: los adaptadores de salida implementan un puerto ─────

    @Test
    void adaptadoresDeSalidaImplementanUnPuertoDeSalida() {
        classes().that().resideInAPackage("..adapter.out..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().implement(JavaClass.Predicates.resideInAPackage("..application.port.out.."))
                .check(clases);
    }
}
