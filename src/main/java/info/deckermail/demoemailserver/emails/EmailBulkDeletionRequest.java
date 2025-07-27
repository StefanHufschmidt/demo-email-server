package info.deckermail.demoemailserver.emails;

import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;

record EmailBulkDeletionRequest(
        @NotEmpty Collection<Long> emailIds
) {
}
