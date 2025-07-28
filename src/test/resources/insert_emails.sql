-- Insert test data into participants table
INSERT INTO participants ("id", "address") VALUES
  (1, 'admin@example.com'),
  (2, 'billing@example.com'),
  (3, 'support@example.com'),
  (4, 'security@example.com'),
  (5, 'user1@example.com'),
  (6, 'user2@example.com'),
  (7, 'user3@example.com'),
  (8, 'user4@example.com');
SELECT setval('participants_id_seq', 8);

-- Insert test data into emails table
INSERT INTO emails ("id", "state", "subject", "body", "from") VALUES
  (1, 'DRAFT', 'Welcome!', 'Hello and welcome to our service.', 1),
  (2, 'SENT', 'Your Invoice', 'Please find attached your invoice.', 2),
  (3, 'DELETED', 'Delivery Issue', 'We could not deliver your email.', 3),
  (4, 'SPAM', 'Password Reset', 'Click here to reset your password.', 4);
SELECT setval('emails_id_seq', 4);

-- Insert test data into receivers table
INSERT INTO receivers ("participant_id", "email_id") VALUES
  (6, 1),
  (5, 1),
  (2, 1),
  (6, 2),
  (7, 3),
  (6, 4),
  (8, 4);
