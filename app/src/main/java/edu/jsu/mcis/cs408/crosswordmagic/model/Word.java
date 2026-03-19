package edu.jsu.mcis.cs408.crosswordmagic.model;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Objects;

public class Word {

    private Integer id, puzzleid, row, column, box;
    private WordDirection direction;
    private String word, clue;

    public Word(HashMap<String, String> params) {

        try {

            this.id = (params.get("_id") != null ? Integer.parseInt(params.get("_id")) : null);
            this.puzzleid = Integer.parseInt(Objects.requireNonNull(params.get("puzzleid")));
            this.row = Integer.parseInt(Objects.requireNonNull(params.get("row")));
            this.column = Integer.parseInt(Objects.requireNonNull(params.get("column")));
            this.box = Integer.parseInt(Objects.requireNonNull(params.get("box")));
            this.word = params.get("word");
            this.clue = params.get("clue");

            this.direction = WordDirection.values()[Integer.parseInt(Objects.requireNonNull(params.get("direction")))];

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isAcross() {
        return direction.equals(WordDirection.ACROSS);
    }

    public boolean isDown() {
        return direction.equals(WordDirection.DOWN);
    }

    public Integer getId() {
        return id;
    }

    public Integer getPuzzleid() {
        return puzzleid;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getColumn() {
        return column;
    }

    public Integer getBox() {
        return box;
    }

    public WordDirection getDirection() {
        return direction;
    }

    public String getWord() {
        return word;
    }

    public String getClue() {
        return clue;
    }

    @NonNull
    public String toString() {

        StringBuilder s = new StringBuilder();

        s.append("ID: ").append(id).append(", ");
        s.append("Word: ").append(word).append(", ");
        s.append("Row/Col: ").append(row).append('/').append(column).append(", ");
        s.append("Direction: ").append(direction).append(", ");
        s.append("Box: ").append(box).append(", ");
        s.append("Clue: ").append(clue);

        return s.toString();

    }

}