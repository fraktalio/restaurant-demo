create sequence hibernate_sequence;


create table if not exists association_value_entry
(
	id bigint not null
		constraint association_value_entry_pkey
			primary key,
	association_key varchar(255) not null,
	association_value varchar(255),
	saga_id varchar(255) not null,
	saga_type varchar(255)
);


create index if not exists idxk45eqnxkgd8hpdn6xixn8sgft
	on association_value_entry (saga_type, association_key, association_value);

create index if not exists idxgv5k1v2mh6frxuy5c0hgbau94
	on association_value_entry (saga_id, saga_type);

create table if not exists menu_entity
(
	id varchar(255) not null
		constraint menu_entity_pkey
			primary key,
	active boolean not null,
	menu_version integer not null,
	restaurant_id varchar(255)
);


create table if not exists menu_entity_menu_items
(
	menu_entity_id varchar(255) not null
		constraint fkh3k81m5ejhj6qhedy1k3qaytk
			references menu_entity,
	menu_id varchar(255),
	name varchar(255),
	price numeric(19,2)
);


create table if not exists restaurant_entity
(
	id varchar(255) not null
		constraint restaurant_entity_pkey
			primary key,
	aggregate_version bigint not null,
	name varchar(255)
);


create table if not exists restaurant_entity_menu_items
(
	restaurant_entity_id varchar(255) not null
		constraint fktfxmwmsre3lv2h8ot29yq510n
			references restaurant_entity,
	menu_id varchar(255),
	name varchar(255),
	price numeric(19,2)
);


create table if not exists restaurantorder_entity
(
	id varchar(255) not null
		constraint restaurantorder_entity_pkey
			primary key,
	aggregate_version bigint not null,
	customer varchar(255),
	restaurant_id varchar(255),
	state varchar(255)
);


create table if not exists restaurantorder_entity_line_items
(
	restaurantorder_entity_id varchar(255) not null
		constraint fkhl5ib1apfx55h61je9sqipfgd
			references restaurantorder_entity,
	menu_id varchar(255),
	name varchar(255),
	quantity integer not null
);

create table if not exists saga_entry
(
	saga_id varchar(255) not null
		constraint saga_entry_pkey
			primary key,
	revision varchar(255),
	saga_type varchar(255),
	serialized_saga oid
);


create table if not exists token_entry
(
	processor_name varchar(255) not null,
	segment integer not null,
	owner varchar(255),
	timestamp varchar(255) not null,
	token oid,
	token_type varchar(255),
	constraint token_entry_pkey
		primary key (processor_name, segment)
);


CREATE TABLE IF NOT EXISTS dead_letter_entry
(
    dead_letter_id character varying(255) NOT NULL,
    cause_message character varying(255),
    cause_type character varying(255),
    diagnostics oid,
    enqueued_at timestamp without time zone NOT NULL,
    last_touched timestamp without time zone,
    aggregate_identifier character varying(255),
    event_identifier character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    sequence_number bigint,
    time_stamp character varying(255) NOT NULL,
    token oid,
    token_type character varying(255),
    type character varying(255),
    processing_group character varying(255) NOT NULL,
    processing_started timestamp without time zone,
    sequence_identifier character varying(255) NOT NULL,
    sequence_index bigint NOT NULL,
    CONSTRAINT dead_letter_entry_pkey PRIMARY KEY (dead_letter_id),
    CONSTRAINT ukhlr8io86j74qy298xf720n16v UNIQUE (processing_group, sequence_identifier, sequence_index)
);

CREATE INDEX IF NOT EXISTS idxe67wcx5fiq9hl4y4qkhlcj9cg
    ON dead_letter_entry USING btree
        (processing_group ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS idxrwucpgs6sn93ldgoeh2q9k6bn
    ON dead_letter_entry USING btree
        (processing_group ASC NULLS LAST, sequence_identifier ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS domain_event_entry
(
    global_index bigint NOT NULL,
    event_identifier character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    time_stamp character varying(255) NOT NULL,
    aggregate_identifier character varying(255) NOT NULL,
    sequence_number bigint NOT NULL,
    type character varying(255),
    CONSTRAINT domain_event_entry_pkey PRIMARY KEY (global_index),
    CONSTRAINT uk8s1f994p4la2ipb13me2xqm1w UNIQUE (aggregate_identifier, sequence_number),
    CONSTRAINT uk_fwe6lsa8bfo6hyas6ud3m8c7x UNIQUE (event_identifier)
);

CREATE TABLE IF NOT EXISTS snapshot_event_entry
(
    aggregate_identifier character varying(255) NOT NULL,
    sequence_number bigint NOT NULL,
    type character varying(255) NOT NULL,
    event_identifier character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    time_stamp character varying(255) NOT NULL,
    CONSTRAINT snapshot_event_entry_pkey PRIMARY KEY (aggregate_identifier, sequence_number, type),
    CONSTRAINT uk_e1uucjseo68gopmnd0vgdl44h UNIQUE (event_identifier)
);

