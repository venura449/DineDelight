INSERT INTO dining_table (table_code, capacity) VALUES
  ('T1', 2),
  ('T2', 2),
  ('T3', 4),
  ('T4', 4),
  ('T5', 6),
  ('T6', 6);

INSERT INTO event_location (name) VALUES
  ('Main Venue'),
  ('Annex Center');

-- Assign spaces to locations (assuming IDs 1/2). Adjust if needed.
INSERT INTO event_space (name, capacity, location_id) VALUES
  ('Ballroom A', 100, 1),
  ('Ballroom B', 80, 1),
  ('Conference Room 1', 40, 2);


