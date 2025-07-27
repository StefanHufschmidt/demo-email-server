package info.deckermail.demoemailserver.emails;

import lombok.Getter;

@Getter
class NoSuchEmailException extends EmailException {

    NoSuchEmailException(Long emailId) {
        super("No email found with ID: " + emailId, emailId);
    }
}
