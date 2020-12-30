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

    public String getShortDescription() {
        return firstName + " " + lastName + " [" + userName + "]: " + osmPlaceName;
    }

    public String getLongDescription() {
        StringBuilder sb = new StringBuilder();

        sb.append("Имя: ").append(firstName).append("\n")
            .append("Фамилия: ").append(lastName).append("\n")
            .append("username: ").append(userName).append("\n")
            .append("Откуда(как сам ввел): ").append(userPlace).append("\n")
            .append("Откуда(из OSM): ").append(osmPlaceName).append("\n")
            .append("Координаты места из OSM:\n")
            .append("Долгота: ").append(osmMapPoint != null ? osmMapPoint.getCoordinate().x : "...").append("\n")
            .append("Широта: ").append(osmMapPoint != null ? osmMapPoint.getCoordinate().y : "...").append("\n");


        return sb.toString();
    }
}