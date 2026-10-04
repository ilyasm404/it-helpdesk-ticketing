-- Optional demo data
USE helpdesk;

INSERT INTO users (name, email, role, specialty) VALUES
    ('Alice Chen',   'alice@example.com', 'END_USER',   NULL),
    ('Bob Singh',    'bob@example.com',   'END_USER',   NULL),
    ('Dana Moore',   'dana@example.com',  'TECHNICIAN', 'Networking'),
    ('Evan Patel',   'evan@example.com',  'TECHNICIAN', 'Hardware / Windows');

INSERT INTO tickets (title, description, priority, status, created_by, assigned_to) VALUES
    ('Cannot connect to Wi-Fi', 'Laptop fails to join the office network since this morning.', 'HIGH',     'IN_PROGRESS', 1, 3),
    ('Password reset',          'Locked out of my account after too many attempts.',            'MEDIUM',   'OPEN',        2, NULL),
    ('Printer offline',         '3rd floor printer shows offline for everyone.',                'LOW',      'OPEN',        1, NULL),
    ('Server room alarm',       'Temperature alarm is going off in the server room.',           'CRITICAL', 'OPEN',        2, NULL);
