package info.deckermail.demoemailserver.business;

import info.deckermail.demoemailserver.EmailState;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record EmailResponse(
    @Schema(description = "The unique identifier of the email.")
    Long id,
    @Schema(description = "The email subject", example = "Your Invoice")
    String subject,
    @Schema(description = "The email body", example = "Please find attached your invoice.")
    String body,
    @Schema(description = "The email state", example = "SENT")
    EmailState state,
    @Schema(description = "The email sender address", example = "foo@bar.com")
    String from,
    @Schema(description = "The email recipients")
    Set<String> to
) {}
