package edu.jsu.mcis.cs408.crosswordmagic.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.DAOFactory;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.GuessDAO;
import edu.jsu.mcis.cs408.crosswordmagic.model.dao.PuzzleDAO;

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
    public void getPuzzleList(){
        PuzzleDAO puzzleDAO = daoFactory.getPuzzleDAO();
        PuzzleListItem[] list = puzzleDAO.list();
        firePropertyChange(CrosswordMagicController.PUZZLE_LIST_PROPERTY, null, list);
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