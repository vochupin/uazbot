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

    @Column(name = "firstname", nullable = true)
    private String firstName;

    @Column(name = "lastname", nullable = true)
    private String lastName;

    @Column(name = "username", nullable = true)
    private String userName;

    @Column(name = "fromwhere", nullable = true)
    private String fromWhere;

    @Column(name = "placename", nullable = true)
    private String placeName;

    private Point geom;

    @Column(name = "text", nullable = true)
    private String text;
}