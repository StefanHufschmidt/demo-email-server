package info.deckermail.demoemailserver.emails;

import java.util.Collection;

record EmailResponse(
    Long id,
    String subject,
    String body,
    EmailState state,
    String from,
    Collection<String> to
) {}
