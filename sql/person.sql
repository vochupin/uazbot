CREATE TABLE person (
    pid         bigint PRIMARY KEY,
    firstname   varchar(64),
    lastname    varchar(64),
    username    varchar(32),
    fromwhere   varchar(128),
    geom geometry(Point, 4326),
    placename   varchar(4096)
);