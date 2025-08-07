package info.deckermail.demoemailserver.business;

public class EmailUpdateFailedException extends EmailException {
    public EmailUpdateFailedException(Long emailId) {
        super("Could not update email with ID " + emailId, emailId);
    }
}
