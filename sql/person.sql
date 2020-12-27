CREATE TABLE person (
    pid         bigint PRIMARY KEY,
    first_name   varchar(64),
    last_name    varchar(64),
    user_name    varchar(32),
    user_place   varchar(128),
    osm_map_point geometry(Point, 4326),
    osm_place_name   varchar(4096),
    text        varchar(256)
);