INSERT INTO roles (role_id, role_name, description) VALUES
                                                        ('admin', 'ADMIN', 'Administrator with full access'),
                                                        ('owner', 'OWNER', 'Room owner'),
                                                        ('user', 'USER', 'Regular user');

INSERT INTO payment_methods (payment_method_id, payment_method_name, description) VALUES
                                                                                      ('cash', 'Cash', 'Pay at property'),
                                                                                      ('momo', 'MoMo', 'MoMo E-wallet');

INSERT INTO notification_types (type_id, type_name) VALUES
                                                        ('booking', 'Booking'),
                                                        ('payment', 'Payment'),
                                                        ('system', 'System'),
                                                        ('review', 'Review');
