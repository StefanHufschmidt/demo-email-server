-- Insert test data into emails table
INSERT INTO emails ("id", "state", "subject", "body", "from", "to") VALUES
  (1, 'DRAFT', 'Welcome!', 'Hello and welcome to our service.', 'admin@example.com', '{user1@example.com, user2@example.com}'),
  (2, 'SENT', 'Your Invoice', 'Please find attached your invoice.', 'billing@example.com', '{user2@example.com}'),
  (3, 'DELETED', 'Delivery Issue', 'We could not deliver your email.', 'support@example.com', '{user3@example.com}'),
  (4, 'SPAM', 'Password Reset', 'Click here to reset your password.', 'security@example.com', '{user4@example.com, user2@example.com}');
