package com.sbertesttask.tictactoe.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "goes_first")
    private boolean goesFirst;

    @Column(name = "status")
    private Integer status;

    @Column(name = "modified_date")
    private Date modifiedDate;

    public StatusCode getStatusAsEnum() {
        for (StatusCode statusCode : StatusCode.values()) {
            if (statusCode.getCode().equals(this.status)) {
                return statusCode;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + this.status);
    }
}
