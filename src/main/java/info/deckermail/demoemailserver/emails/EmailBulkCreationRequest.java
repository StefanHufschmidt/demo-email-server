package info.deckermail.demoemailserver.emails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.Collection;

public record EmailBulkCreationRequest(
    @Valid
    @Size(min = 1, max = 1_000) // Ensure at least one email is provided, up to 1000 to avoid excessive bulk creation
    Collection<EmailCreationRequest> emails
) {
}
