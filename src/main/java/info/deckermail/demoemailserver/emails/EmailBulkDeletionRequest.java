package info.deckermail.demoemailserver.emails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

record EmailBulkDeletionRequest(
        @Schema(description = "The IDs of emails to delete.") @NotEmpty Collection<Long> emailIds
) {
}
