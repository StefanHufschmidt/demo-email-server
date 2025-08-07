package info.deckermail.demoemailserver.business;

import lombok.Getter;

@Getter
abstract class EmailException extends Exception {

    protected final Long emailId;

    EmailException(String message, Long emailId) {
        super(message);
        this.emailId = emailId;
    }
}
