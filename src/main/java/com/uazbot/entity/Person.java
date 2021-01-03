package com.uazbot.entity;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.springframework.util.StringUtils;

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
        StringBuilder sb =  new StringBuilder();
        sb.append("*");
        if (!StringUtils.isEmpty(firstName)) {
            sb.append(firstName);
        }

        if (!StringUtils.isEmpty(lastName)) {
            if (sb.length() != 1) {
                sb.append(" ");
            }
            sb.append(lastName);
        }

        if (!StringUtils.isEmpty(userName)) {
            if (sb.length() != 1) {
                sb.append(" ");
            }
            sb.append("[").append(userName).append("]");
        }
        sb.append(":*\n");

        if (!StringUtils.isEmpty(osmPlaceName)) {
            for (String placeStr : osmPlaceName.split(",")) {
                sb.append(placeStr.trim()).append("\n");
            }
        }

        return sb.toString();
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