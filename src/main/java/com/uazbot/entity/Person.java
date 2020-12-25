package com.uazbot.entity;
import lombok.*;

import javax.persistence.*;
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
}