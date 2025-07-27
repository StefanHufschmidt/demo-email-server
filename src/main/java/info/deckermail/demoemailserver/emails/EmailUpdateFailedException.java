package info.deckermail.demoemailserver.emails;

class EmailUpdateFailedException extends EmailException {
    EmailUpdateFailedException(Long emailId) {
        super("Could not update email with ID " + emailId, emailId);
    }
}
