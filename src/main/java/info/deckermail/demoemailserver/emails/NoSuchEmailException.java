package info.deckermail.demoemailserver.emails;

import lombok.Getter;

@Getter
class NoSuchEmailException extends Exception {
    private final Long emailId;

    NoSuchEmailException(Long emailId) {
        super("No email found with ID: " + emailId);
        this.emailId = emailId;
    }
}
