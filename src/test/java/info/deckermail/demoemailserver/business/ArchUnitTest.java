package info.deckermail.demoemailserver.business;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import info.deckermail.demoemailserver.business.participants.ParticipantService;
import info.deckermail.demoemailserver.data.ParticipantEntity;

@AnalyzeClasses(packages = "info.deckermail.demoemailserver.business",
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
