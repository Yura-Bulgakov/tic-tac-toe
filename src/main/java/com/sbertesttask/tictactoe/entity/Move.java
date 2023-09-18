package com.sbertesttask.tictactoe.entity;

import com.sbertesttask.tictactoe.game_utils.Pos;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "move")
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    @Column(name = "turn")
    private int turn;

    @Column(name = "actor")
    private boolean actor;

    @Column(name = "row")
    private int row;

    @Column(name = "col")
    private int col;

    public Pos toPos() {
        return new Pos(this.row, this.col);
    }
}
