package com.uazbot.repository;

import com.uazbot.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findAllByFirstName(String firstName);

    @Query(value = "select * from person where text ilike (concat('%', :name, '%'))", nativeQuery = true)
    List<Person> findByName(@Param("name") String name);

    @Query(value = "select * from person where osm_place_name ilike (concat('%', :address, '%'))", nativeQuery = true)
    List<Person> findByAddress(@Param("address") String address);

    @Modifying
    @Query(value = "delete from person", nativeQuery = true)
    void deleteAll();

}
