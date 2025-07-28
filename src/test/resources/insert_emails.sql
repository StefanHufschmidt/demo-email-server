-- Insert test data into participants table
INSERT INTO participants ("id", "address") VALUES
  (1, 'admin@example.com'),
  (2, 'billing@example.com'),
  (3, 'support@example.com'),
  (4, 'security@example.com');
SELECT setval('participants_id_seq', 4);

-- Insert test data into emails table
INSERT INTO emails ("id", "state", "subject", "body", "from", "to") VALUES
  (1, 'DRAFT', 'Welcome!', 'Hello and welcome to our service.', 1, '{user1@example.com, user2@example.com, billing@example.com}'),
  (2, 'SENT', 'Your Invoice', 'Please find attached your invoice.', 2, '{user2@example.com}'),
  (3, 'DELETED', 'Delivery Issue', 'We could not deliver your email.', 3, '{user3@example.com}'),
  (4, 'SPAM', 'Password Reset', 'Click here to reset your password.', 4, '{user4@example.com, user2@example.com}');
SELECT setval('emails_id_seq', 4);