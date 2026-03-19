package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;

public class DAOFactory extends SQLiteOpenHelper {

    private final Context context;
    private final DAOProperties properties;

    private static final String DATABASE_NAME = "cwmagic.db";
    private static final int DATABASE_VERSION = 1;

    private static final int CSV_HEADER_FIELDS = 4;
    private static final int CSV_DATA_FIELDS = 6;

    public DAOFactory(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        properties = new DAOProperties(context, DATABASE_NAME);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(properties.getProperty("sql_create_puzzles_table"));
        db.execSQL(properties.getProperty("sql_create_words_table"));
        db.execSQL(properties.getProperty("sql_create_guesses_table"));

        PuzzleDAO puzzleDAO = new PuzzleDAO(this);

        addInitialDataFromCSV(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(properties.getProperty("sql_drop_guesses_table"));
        db.execSQL(properties.getProperty("sql_drop_words_table"));
        db.execSQL(properties.getProperty("sql_drop_puzzles_table"));

        onCreate(db);

    }

    public PuzzleDAO getPuzzleDAO() {
        return new PuzzleDAO(this);
    }

    public WordDAO getWordDAO() {
        return new WordDAO(this);
    }

    public String getProperty(String key) {
        return (properties.getProperty(key));
    }

    public void addInitialDataFromCSV(SQLiteDatabase db) {

        /* populate initial database with puzzle from the CSV data file */

        try {

            /* acquire DAO objects */

            WordDAO wordDAO = getWordDAO();
            PuzzleDAO puzzleDAO = getPuzzleDAO();

            /* prepare CSV reader for tab-delimited data */

            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.puzzle)));
            CSVParser parser = (new CSVParserBuilder()).withSeparator('\t').withIgnoreQuotations(true).build();
            CSVReader reader = (new CSVReaderBuilder(br)).withCSVParser(parser).build();
            List<String[]> csv = reader.readAll();

            /* get header row; prepare empty parameter map for field data */

            String[] fields = csv.get(0);

            HashMap<String, String> params;

            /* check number of fields in CSV data; proceed if number is valid */

            if (fields.length == CSV_HEADER_FIELDS) {

                /* add new puzzle to the database */

                params = new HashMap<>();

                params.put(properties.getProperty("sql_field_name"), fields[0]);
                params.put(properties.getProperty("sql_field_description"), fields[1]);

                params.put(properties.getProperty("sql_field_height"), fields[2]);
                params.put(properties.getProperty("sql_field_width"), fields[3]);

                Puzzle newPuzzle = new Puzzle(params);

                int puzzleid = puzzleDAO.create(db, newPuzzle);

                /* add words for new puzzle to the database */

                for (int i = 1; i < csv.size(); ++i) {

                    fields = csv.get(i);

                    if (fields.length == CSV_DATA_FIELDS) {

                        params = new HashMap<>();

                        /*

                        INSERT YOUR CODE HERE

                        */

                        Word newWord = new Word(params);

                        int id = wordDAO.create(db, newWord);

                    }

                }

            }

            br.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}