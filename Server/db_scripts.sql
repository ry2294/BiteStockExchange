-- Table: menu_item_tbl

-- DROP TABLE menu_item_tbl;

CREATE TABLE menu_item_tbl
(
  item_id character varying(50) NOT NULL,
  item_name character varying(50),
  high_price real,
  low_price real,
  order_count integer,
  item_category text,
  CONSTRAINT menu_item_pkey PRIMARY KEY (item_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE menu_item_tbl
  OWNER TO postgres;


-- Table: order_tbl

-- DROP TABLE order_tbl;

CREATE TABLE order_tbl
(
  order_id character varying(50) NOT NULL,
  item_id character varying(50) NOT NULL,
  user_id character varying(50),
  price character varying(50),
  CONSTRAINT order_pkey PRIMARY KEY (order_id, item_id),
  CONSTRAINT order_item_id_fkey FOREIGN KEY (item_id)
      REFERENCES menu_item_tbl (item_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT order_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES user_tbl (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE order_tbl
  OWNER TO postgres;


-- Table: user_connections_tbl

-- DROP TABLE user_connections_tbl;

CREATE TABLE user_connections_tbl
(
  user_id character varying(50) NOT NULL,
  friend_id character varying(50) NOT NULL,
  CONSTRAINT user_connections_pkey PRIMARY KEY (user_id, friend_id),
  CONSTRAINT user_connections_friend_id_fkey FOREIGN KEY (friend_id)
      REFERENCES user_tbl (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT user_connections_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES user_tbl (user_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_connections_tbl
  OWNER TO postgres;


-- Table: user_tbl

-- DROP TABLE user_tbl;

CREATE TABLE user_tbl
(
  user_id character varying(50) NOT NULL,
  user_name character varying(100),
  fb_token text,
  CONSTRAINT user_pkey PRIMARY KEY (user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE user_tbl
  OWNER TO postgres;
