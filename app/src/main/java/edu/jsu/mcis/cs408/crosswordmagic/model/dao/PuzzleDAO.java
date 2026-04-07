package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.opencsv.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;
import edu.jsu.mcis.cs408.crosswordmagic.model.WordDirection;

public class PuzzleDAO {

    private final DAOFactory daoFactory;

    PuzzleDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /* add a new puzzle entry to the database */

    public int create(Puzzle newPuzzle) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        int result = create(db, newPuzzle);
        db.close();
        return result;

    }

    public int create(SQLiteDatabase db, Puzzle newPuzzle) {

        int key = 0;

        /* use this method if there IS already a SQLiteDatabase open */

        String name = daoFactory.getProperty("sql_field_name");
        String description = daoFactory.getProperty("sql_field_description");
        String height = daoFactory.getProperty("sql_field_height");
        String width = daoFactory.getProperty("sql_field_width");

        ContentValues values = new ContentValues();
        values.put(name, newPuzzle.getName());
        values.put(description, newPuzzle.getDescription());
        values.put(height, newPuzzle.getHeight());
        values.put(width, newPuzzle.getWidth());

        key = (int)db.insert(daoFactory.getProperty("sql_table_puzzles"), null, values);

        return key;

    }

    /* return an existing puzzle entry from the database */

    public Puzzle find(int puzzleid) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Puzzle result = find(db, puzzleid);
        db.close();
        return result;

    }

    public Puzzle find(SQLiteDatabase db, int puzzleid) {

        /* use this method if there IS already a SQLiteDatabase open */

        Puzzle puzzle = null;

        String query = daoFactory.getProperty("sql_get_puzzle");
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});

        if (cursor.moveToFirst()) {

            cursor.moveToFirst();

            /* get data for puzzle */

            HashMap<String, String> params = new HashMap<>();

            /* get data for new puzzle */

            /*
            INSERT YOUR CODE HERE
            */

            String[] columns = cursor.getColumnNames();
            for (int i = 0; i < columns.length; i++){
                params.put(columns[i], cursor.getString(i));
            }

            /*
            END OF MY CODE
             */

            if ( !params.isEmpty() )
                puzzle = new Puzzle(params);

            /* get list of words (if any) to add to puzzle */

            WordDAO wordDao = daoFactory.getWordDAO();

            ArrayList<Word> words = wordDao.list(db, puzzleid);

            if ( !words.isEmpty() && puzzle != null ) {
                puzzle.addWordsToPuzzle(words);
            }

            cursor.close();

            /* get already-guessed words (if any) for puzzle */

            query = daoFactory.getProperty("sql_get_guesses");

            int boxColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_box_column_index"));
            int directionColumnIndex = Integer.parseInt(daoFactory.getProperty("sql_guesses_direction_column_index"));

            cursor = db.rawQuery(query, new String[]{String.valueOf(puzzleid)});

            if (cursor.moveToFirst()) {

                cursor.moveToFirst();

                do {

                    Integer box = cursor.getInt(boxColumnIndex);
                    WordDirection direction = WordDirection.values()[cursor.getInt(directionColumnIndex)];
                    if (puzzle != null) {
                        puzzle.addWordToGuessed(box + direction.toString());
                    }

                }
                while ( cursor.moveToNext() );

                cursor.close();

            }

        }

        return puzzle;

    }

    // New methods added in version two
    public PuzzleListItem[] list (){
        // Use this method if there is NOT a database already open

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        PuzzleListItem[] result = list(db);
        db.close();
        return result;
    }

    public PuzzleListItem[] list (SQLiteDatabase db){
        // Use this method if there IS a database already open
        ArrayList<PuzzleListItem> puzzles = new ArrayList<>();
        String query = daoFactory.getProperty("sql_get_puzzle_list");
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            cursor.moveToFirst();

            do{
                // create a new puzzleListItem and add it to an arrayList
                PuzzleListItem newItem = new PuzzleListItem(cursor.getInt(0), cursor.getString(1));
                puzzles.add(newItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return puzzles.toArray(new PuzzleListItem[]{});
    }

}