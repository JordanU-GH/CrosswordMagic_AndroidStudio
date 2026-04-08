package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.GuessDAO;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.WebServiceDAO;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.WordDAO;

public class CrosswordMagicModel extends AbstractModel {

    private final int DEFAULT_PUZZLE_ID = 1;

    private Puzzle puzzle;

    private DAOFactory daoFactory;

    public CrosswordMagicModel(Context context) {

        daoFactory = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();

        this.puzzle = puzzleDAO.find(DEFAULT_PUZZLE_ID);

    }

    public CrosswordMagicModel(Context context, Integer puzzleid) {

        daoFactory = new DAOFactory(context);
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();

        this.puzzle = puzzleDAO.find(puzzleid);

    }

    public void getTestProperty() {

        String wordCount = (this.puzzle != null ? String.valueOf(puzzle.getSize()) : "none");
        firePropertyChange(CrosswordMagicController.TEST_PROPERTY, null, wordCount);

    }
    // **********************
    // Methods added in version_two
    // Get the list of puzzle names in the database
    public String[] getPuzzleNames(){
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        PuzzleListItem[] list = puzzleDAO.list();

        ArrayList<String> names = new ArrayList<>();
        for (PuzzleListItem puzzle : list){
            names.add(puzzle.toString());
        }
        return names.toArray(new String[]{});
    }
    public void getNewPuzzleList(){
        WebServiceDAO webServiceDAO = daoFactory.getWebServiceDAO();
        JSONArray puzzleArray = webServiceDAO.list();
        ArrayList<PuzzleListItem> list = new ArrayList<>();
        try{
            for (int i = 0; i < puzzleArray.length(); i++) {
                JSONObject puzzle = (JSONObject) puzzleArray.get(i);
                int id = (int) puzzle.get("id");
                String name = (String) puzzle.get("name");
                PuzzleListItem newItem = new PuzzleListItem(id, name);
                list.add(newItem);
            }
        }
        catch (JSONException j){
            Log.i("getNewPuzzleList", j.toString());
        }
        catch (Error e){
            Log.i("getNewPuzzleList", e.toString());
        }

        PuzzleListItem[] itemList = list.toArray(new PuzzleListItem[0]);
        firePropertyChange(CrosswordMagicController.NEW_PUZZLE_LIST_PROPERTY, null, itemList);
    }
    public void getSavedPuzzleList(){
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        PuzzleListItem[] list = puzzleDAO.list();
        firePropertyChange(CrosswordMagicController.SAVED_PUZZLE_LIST_PROPERTY, null, list);
    }

    // **********************
    public void getCluesAcross() {
        firePropertyChange(CrosswordMagicController.CLUES_ACROSS_PROPERTY, null, puzzle.getCluesAcross());
    }

    public void getCluesDown() {
        firePropertyChange(CrosswordMagicController.CLUES_DOWN_PROPERTY, null, puzzle.getCluesDown());
    }

    public void getGridLetters() {
        firePropertyChange(CrosswordMagicController.GRID_LETTERS_PROPERTY, null, puzzle.getLetters());
    }

    public void getGridNumbers() {
        firePropertyChange(CrosswordMagicController.GRID_NUMBERS_PROPERTY, null, puzzle.getNumbers());
    }

    public void getGridDimensions() {

        Integer[] dimension = new Integer[2];
        dimension[0] = puzzle.getHeight();
        dimension[1] = puzzle.getWidth();
        firePropertyChange(CrosswordMagicController.GRID_DIMENSION_PROPERTY, null, dimension);

    }

    public void setDownload(Integer webId) {
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        WordDAO wordDAO = daoFactory.getWordDAO();

        // get selected puzzle and associated data from web service
        WebServiceDAO webServiceDAO = daoFactory.getWebServiceDAO();
        JSONObject puzzleObject = webServiceDAO.getPuzzle(webId);

        // parse the puzzle information and add it to the database
        HashMap<String, String> puzzleParams = parsePuzzle(puzzleObject);
        Puzzle newPuzzle = new Puzzle(puzzleParams);
        int databaseId = puzzleDAO.create(newPuzzle);

        // parse the word information and add them to the database
        // also, associate the words with the correct puzzle
        ArrayList<HashMap<String, String>> wordParams = parseWords(puzzleObject, databaseId);
        for (HashMap<String, String> paramSet : wordParams) {
            Word word = new Word(paramSet);
            wordDAO.create(word);
        }

        firePropertyChange(CrosswordMagicController.DOWNLOAD_PROPERTY, null, databaseId);
    }

    // helper methods for the setDownload method
    private HashMap<String, String> parsePuzzle(JSONObject obj){
        // parse the puzzle and create a new puzzle entry and its associated words
        HashMap<String, String> puzzleParams = new HashMap<>();
        try {
            puzzleParams.put("name", (String) obj.get("name"));
            puzzleParams.put("description", (String) obj.get("description"));
            puzzleParams.put("height", Integer.toString(obj.getInt("height")));
            puzzleParams.put("width", Integer.toString(obj.getInt("width")));
        } catch (JSONException e) {
            Log.i("Error", e.toString());
            throw new RuntimeException(e);
        }
        return puzzleParams;
    }
    private ArrayList<HashMap<String, String>> parseWords(JSONObject obj, int id){
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        try{
            JSONArray entries = obj.getJSONArray("puzzle");
            for(int i = 0; i < entries.length(); i++){
                JSONObject word = entries.getJSONObject(i);
                HashMap<String, String> wordParams = new HashMap<>();

                wordParams.put("puzzleid", Integer.toString(id));
                wordParams.put("row", Integer.toString(word.getInt("row")));
                wordParams.put("column", Integer.toString(word.getInt("column")));
                wordParams.put("box", Integer.toString(word.getInt("box")));
                wordParams.put("word", (String) word.get("word"));
                wordParams.put("clue", (String) word.get("clue"));
                wordParams.put("direction", Integer.toString(word.getInt("direction")));

                wordList.add(wordParams);
            }
        } catch (JSONException e){
            Log.i("Error", e.toString());
            throw new RuntimeException();
        }
        return wordList;
    }

    public void setGuess(HashMap<String, String> params) {

        if (params != null) {

            Integer num = Integer.parseInt(Objects.requireNonNull(params.get("num")));
            String guess = Objects.requireNonNull(params.get("guess")).toUpperCase().trim();

            WordDirection result = puzzle.checkGuess(num, guess);

            int messageid = R.string.message_guess_incorrect;

            /* if guess was correct, add word to database */

            if (result != null) {

                messageid = R.string.message_guess_correct;

                Word word = puzzle.getWord(String.valueOf(num) + result);

                GuessDAO guessDao = daoFactory.getGuessDAO();
                guessDao.create(word.getPuzzleid(), word.getId());

                getGridLetters();

            }

            /* if puzzle is solved, notify View; otherwise, notify View if guess was correct or not */

            if (puzzle.isSolved())
                firePropertyChange(CrosswordMagicController.SOLVED_PROPERTY, null, true);
            else
                firePropertyChange(CrosswordMagicController.GUESS_PROPERTY, null, messageid);

        }

    }
}