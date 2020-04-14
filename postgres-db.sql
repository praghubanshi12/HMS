set client_encoding to 'UTF8';

CREATE TABLE "users" ("id" SERIAL PRIMARY KEY, "email" VARCHAR UNIQUE NOT NULL, "password" VARCHAR, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP, "status" BOOLEAN DEFAULT '1', "created_by" INTEGER REFERENCES users, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "roles" ("id" SERIAL PRIMARY KEY, "role_name" VARCHAR, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP, "created_by" INTEGER REFERENCES users, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "user_roles" ("id" SERIAL PRIMARY KEY, "user_id" INTEGER REFERENCES users, "role_id" INTEGER REFERENCES roles, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "staffs" ("id" SERIAL PRIMARY KEY, "user_id" INTEGER DEFAULT NULL, "role_id" INTEGER REFERENCES roles, "hotel_id" INTEGER REFERENCES hotels, "name" VARCHAR, "age" INTEGER, "gender" VARCHAR, "address" VARCHAR , "contact_no" VARCHAR , "email" VARCHAR UNIQUE NOT NULL, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP, "created_by" INTEGER REFERENCES users, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "customers" ("id" SERIAL PRIMARY KEY, "user_id" INTEGER REFERENCES users, "name" VARCHAR, "address" VARCHAR , "contact_no" VARCHAR , "email" VARCHAR UNIQUE NOT NULL, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP) WITHOUT OIDS;

CREATE TABLE "hotels" ("id" SERIAL PRIMARY KEY, "name" VARCHAR, "phone" VARCHAR, "address" VARCHAR , "website" VARCHAR UNIQUE NOT NULL, "manager_id" INTEGER REFERENCES staffs, "longitude" VARCHAR, "latitude" VARCHAR, "stars" INTEGER, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP, "created_by" INTEGER REFERENCES users, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "mst_status" ("id" SERIAL PRIMARY KEY, "name" VARCHAR, "color" VARCHAR, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "rooms" ("id" SERIAL PRIMARY KEY, "room_no" INTEGER, "type" VARCHAR, "hotel_id" INTEGER REFERENCES hotels, "is_smoking" BOOLEAN DEFAULT '0', "is_balcony" BOOLEAN DEFAULT '0', "bed_type" VARCHAR, "status_id" INTEGER REFERENCES mst_status, "is_checked" BOOLEAN DEFAULT '0', "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "updated_date" TIMESTAMP, "created_by" INTEGER REFERENCES users, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "mst_tasks" ("id" SERIAL PRIMARY KEY, "name" VARCHAR, "hotel_id" INTEGER REFERENCES hotels, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "room_tasks" ("id" SERIAL PRIMARY KEY, "room_id" INTEGER REFERENCES rooms, "task_id" INTEGER REFERENCES mst_tasks, "description" VARCHAR, "staff_id" INTEGER REFERENCES staffs, "status_id" INTEGER REFERENCES mst_status, "priority" INTEGER, "occurrence" VARCHAR, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "room_staffs" ("id" SERIAL PRIMARY KEY, "room_id" INTEGER REFERENCES rooms, "staff_id" INTEGER REFERENCES staffs, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "mst_facilities" ("id" SERIAL PRIMARY KEY, "name" VARCHAR, "hotel_id" INTEGER REFERENCES hotels, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users, "updated_date" TIMESTAMP, "updated_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "room_facilities" ("id" SERIAL PRIMARY KEY, "room_id" INTEGER REFERENCES rooms, "facility_id" INTEGER REFERENCES mst_facilities, "image" VARCHAR, "status" BOOLEAN DEFAULT '0', "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "booking_requests" ("id" SERIAL PRIMARY KEY, "customer_id" INTEGER REFERENCES customers, "hotel_id" INTEGER REFERENCES hotels, "booking_start_date" TIMESTAMP, "booking_end_date" TIMESTAMP, "no_of_people" INTEGER, "status_id" INTEGER REFERENCES mst_status, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP) WITHOUT OIDS;

CREATE TABLE "checkins" ("id" SERIAL PRIMARY KEY, "room_id" INTEGER REFERENCES rooms, "booking_id" INTEGER REFERENCES booking_requests, "customer_id" INTEGER REFERENCES customers, "checkin_date" TIMESTAMP, "checkout_date" TIMESTAMP, "no_of_people" INTEGER, "is_checked" DEFAULT '1', "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;

CREATE TABLE "booking_matching_rooms" ("id" SERIAL PRIMARY KEY, "booking_id" INTEGER REFERENCES booking_requests, "room_id" INTEGER REFERENCES rooms, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP) WITHOUT OIDS;

CREATE TABLE "booked_rooms" ("id" SERIAL PRIMARY KEY, "booking_id" INTEGER REFERENCES booking_requests, "room_id" INTEGER REFERENCES rooms, "booking_start_date" TIMESTAMP, "booking_end_date" TIMESTAMP, "created_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "created_by" INTEGER REFERENCES users) WITHOUT OIDS;


