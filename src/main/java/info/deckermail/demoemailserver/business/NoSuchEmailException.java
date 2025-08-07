package info.deckermail.demoemailserver.business;

import lombok.Getter;

@Getter
public class NoSuchEmailException extends EmailException {

    public NoSuchEmailException(Long emailId) {
        super("No email found with ID: " + emailId, emailId);
    }
}
