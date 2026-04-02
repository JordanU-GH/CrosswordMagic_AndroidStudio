package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.ActivityWelcomeBinding;
import edu.jsu.mcis.cs408.crosswordmagic.model.CrosswordMagicModel;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;

public class WelcomeActivity extends AppCompatActivity implements AbstractView, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final String TAG = "WelcomeActivity";

    private ActivityWelcomeBinding binding;

    private CrosswordMagicController controller;

    private Integer puzzleid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.button.setOnClickListener(this);
        binding.spinner.setOnItemSelectedListener(this);

        /* Create Controller and Model */

        controller = new CrosswordMagicController();

        CrosswordMagicModel model = new CrosswordMagicModel(this);

        /* Register View(s) and Model(s) with Controller */

        controller.addModel(model);
        controller.addView(this);

        /* Request Puzzle List */

        controller.getPuzzleList();

    }

    @Override
    public void onClick(View view) {

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("puzzleid", puzzleid);
        startActivity(i);

    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        Object value = evt.getNewValue();

        if (name.equals(CrosswordMagicController.PUZZLE_LIST_PROPERTY)) {

            if (value instanceof PuzzleListItem[]) {

                PuzzleListItem[] puzzles = (PuzzleListItem[])value;

                ArrayAdapter<PuzzleListItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, puzzles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                binding.spinner.setAdapter(adapter);

            }

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        puzzleid = ((PuzzleListItem)adapterView.getItemAtPosition(i)).getId();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

}