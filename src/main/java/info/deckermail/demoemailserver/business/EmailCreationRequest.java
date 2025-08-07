package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.EmailState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collection;

public record EmailCreationRequest(
        @Schema(description = "The email subject", example = "Your Invoice")
        String subject,
        @Schema(description = "The email body", example = "Please find attached your invoice.")
        String body,
        @Schema(description = "The email state", example = "SENT")
        @NotNull
        EmailState state,
        @Schema(description = "The email sender address", example = "foo@bar.com")
        @Size(max = 255, message = "From address must be between 0 and 255 characters") // Avoid database errors for too long email addresses
        String from,
        @Schema(description = "The email recipients")
        @Size(max = 1_000, message = "Sending emails to more than 1000 recipients is not allowed") // Limit added to prevent abuse, limit can be discussed
        Collection<String> to
) {}
