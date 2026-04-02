package edu.jsu.mcis.cs408.crosswordmagic.model;

import androidx.annotation.NonNull;

public class PuzzleListItem {

    private final Integer id;
    private final String name;

    public PuzzleListItem(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    @NonNull
    public String toString() {
        return name;
    }

}