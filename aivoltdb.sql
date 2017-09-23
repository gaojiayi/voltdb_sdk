--create role ops with SQL;
--SET DR=ACTIVE;
create table dual
(
	id varchar(10) not null,
	PRIMARY KEY (id)
);

create table PM_OFFER_PRICEPLAN_REL
(
  rel_id            INTEGER not null,
  offer_id          INTEGER not null,
  role_id           INTEGER,
  pricing_target_id INTEGER,
  price_plan_id     INTEGER,
  select_flag       VARCHAR(8),
  done_code         INTEGER,
  create_date       TIMESTAMP,
  op_id             VARCHAR(12),
  done_date         TIMESTAMP,
  org_id            VARCHAR(12),
  create_op_id      VARCHAR(12),
  create_org_id     VARCHAR(12),
  region_id         VARCHAR(6) not null,
  valid_date        TIMESTAMP,
  expire_date       TIMESTAMP,
  status            VARCHAR(8)
);


