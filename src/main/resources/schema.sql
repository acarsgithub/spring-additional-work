
DROP Table if EXISTS user;
CREATE TABLE user (
	id INTEGER NOT NULL PRIMARY KEY,
	active BOOLEAN,
	password VARCHAR(255),
	roles VARCHAR(255),
	user_name VARCHAR(255),
	bank_account_value double not null

);