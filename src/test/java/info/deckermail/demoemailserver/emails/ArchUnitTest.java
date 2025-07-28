package info.deckermail.demoemailserver.emails;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@AnalyzeClasses(packages = "info.deckermail.demoemailserver.emails",
                importOptions = {ImportOption.DoNotIncludeTests.class})
class ArchUnitTest {

    @ArchTest
    static ArchRule should_not_manually_initialize_ParticipantEntity =
            ArchRuleDefinition.noClasses()
                    .that()
                    .doNotBelongToAnyOf(ParticipantService.class)
                    .should()
                    .callConstructor(ParticipantEntity.class, String.class)
                    .orShould()
                    .callConstructor(ParticipantEntity.class)
                    .orShould()
                    .callConstructor(ParticipantEntity.class, Long.class, String.class)
                    .because("ParticipantEntity should only be created by the ParticipantService to ensure " +
                            "consistency in participant management and to avoid manual instantiation that could lead to " +
                            "inconsistencies in the application state.");
}
