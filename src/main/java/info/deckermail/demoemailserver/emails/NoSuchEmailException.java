package info.deckermail.demoemailserver.emails;

import lombok.Getter;

@Getter
public class NoSuchEmailException extends Exception {
    private final Long emailId;

    public NoSuchEmailException(Long emailId) {
        super("No email found with ID: " + emailId);
        this.emailId = emailId;
    }
}
