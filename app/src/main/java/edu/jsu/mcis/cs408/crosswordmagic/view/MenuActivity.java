package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.ActivityMenuBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class MenuActivity extends AppCompatActivity implements AbstractView, View.OnClickListener {

    private final String TAG = "MenuActivity";
    private ActivityMenuBinding binding;
    private CrosswordMagicController controller;
    private final PuzzleListEntryClickHandler itemClick = new PuzzleListEntryClickHandler();
    private Integer lastId = null;

    PuzzleListEntryClickHandler getItemClick() { return itemClick; }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.downloadButton.setOnClickListener(this);

        /* Create Controller and Model */

        controller = new CrosswordMagicController();

        CrosswordMagicModel model = new CrosswordMagicModel(this);

        /* Register View(s) and Model(s) with Controller */

        controller.addModel(model);
        controller.addView(this);

        // Load puzzle list
        controller.getPuzzleList();

    }

    @Override
    public void onClick(View v){
        // auto-play the selected puzzle after it is downloaded
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("puzzleid", 1); // Change this value to a dynamically generated one later
        startActivity(i);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt){
        /*
         * This method is called by the "propertyChange()" method of AbstractController
         * when a change is made to an element of a Model.  It identifies the element that
         * was changed and updates the View accordingly.
         */

        String propertyName = evt.getPropertyName();
        String propertyValueString = evt.getNewValue().toString();

        Log.i(TAG, "New " + propertyName + " Value from Model: " + propertyValueString);
        if (propertyName.equals(CrosswordMagicController.PUZZLE_LIST_PROPERTY)){
            List<PuzzleListItem> list = Arrays.asList((PuzzleListItem[]) evt.getNewValue());
            updateRecyclerView(list);
        }

    }

    private class PuzzleListEntryClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = binding.puzzleListRecycler.getChildLayoutPosition(v);
            RecyclerViewAdapter adapter = (RecyclerViewAdapter)binding.puzzleListRecycler.getAdapter();
            if (adapter != null) {
                PuzzleListItem entry = adapter.getEntryAtPosition(position);
                lastId = entry.getId();
                Toast.makeText(v.getContext(), String.valueOf(lastId), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateRecyclerView(List<PuzzleListItem> puzzleList) {
        if (puzzleList == null){
            System.out.println("----ERROR IN MenuActivity.java IN updateRecyclerView----");
            System.out.println("ERROR: PUZZLE LIST IS NULL");
            return;
        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, puzzleList);
        binding.puzzleListRecycler.setHasFixedSize(true);
        binding.puzzleListRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.puzzleListRecycler.setAdapter(adapter);
    }

}