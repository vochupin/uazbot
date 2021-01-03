delete from person p where p.user_place is null or p.osm_place_name is null;

update person p set text = concat(p.first_name, ' ', last_name, ' ', p.user_name, ' ', p.osm_place_name, ' ', p.user_place);