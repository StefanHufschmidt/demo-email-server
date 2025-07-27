package info.deckermail.demoemailserver.emails;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collection;

record EmailUpdateRequest(
        String subject,
        String body,
        @NotNull
        EmailState state,
        @Size(max = 255, message = "From address must be between 0 and 255 characters") // Avoid database errors for too long email addresses
        String from,
        @Size(max = 1_000, message = "Sending emails to more than 1000 recipients is not allowed") // Limit added to prevent abuse, limit can be discussed
        Collection<String> to
) {
}
