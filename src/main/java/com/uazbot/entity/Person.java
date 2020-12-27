package com.uazbot.entity;
import lombok.*;
import org.locationtech.jts.geom.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "PERSON")
public class Person implements Serializable {

    @Id
    @Column(name = "pid", nullable = false)
    private Long pid;

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "user_name", nullable = true)
    private String userName;

    @Column(name = "user_place", nullable = true)
    private String userPlace;

    @Column(name = "osm_place_name", nullable = true)
    private String osmPlaceName;

    private Point osmMapPoint;

    @Column(name = "text", nullable = true)
    private String text;
}