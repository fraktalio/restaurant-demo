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

