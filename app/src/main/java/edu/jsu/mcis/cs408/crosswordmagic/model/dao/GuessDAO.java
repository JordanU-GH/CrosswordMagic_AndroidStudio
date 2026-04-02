package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class GuessDAO {

    private final DAOFactory daoFactory;

    GuessDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public void create(int puzzleid, int wordid){
        SQLiteDatabase db = daoFactory.getWritableDatabase();
        String puzzlefield = daoFactory.getProperty("sql_field_puzzleid");
        String wordfield = daoFactory.getProperty("sql_field_wordid");

        ContentValues values = new ContentValues();
        values.put(puzzlefield, puzzleid);
        values.put(wordfield, wordid);
        db.insert(daoFactory.getProperty("sql_table_guesses"), null, values);
    }

}
