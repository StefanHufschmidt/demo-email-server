package info.deckermail.demoemailserver.emails;

import lombok.Getter;

@Getter
abstract class EmailException extends Exception {

    protected final Long emailId;

    EmailException(String message, Long emailId) {
        super(message);
        this.emailId = emailId;
    }
}
