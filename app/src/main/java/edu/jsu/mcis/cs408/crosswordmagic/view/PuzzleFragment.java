package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.beans.PropertyChangeEvent;

import edu.jsu.mcis.cs408.crosswordmagic.controller.CrosswordMagicController;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.FragmentPuzzleBinding;

public class PuzzleFragment extends Fragment implements TabFragment, AbstractView {

    public static final String TAG = "PuzzleFragment";

    private String title;

    private CrosswordMagicController controller;

    private FragmentPuzzleBinding binding;

    public PuzzleFragment() { super(); }

    public PuzzleFragment(String title) {
        super();
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPuzzleBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        /* get controller, register Fragment as a View */

        this.controller = ((MainActivity)getContext()).getController();

        controller.addView(this);

    }

    @Override
    public void modelPropertyChange(final PropertyChangeEvent evt) {

        String name = evt.getPropertyName();
        Object value = evt.getNewValue();

        if (name.equals(CrosswordMagicController.SAVED_PUZZLE_LIST_PROPERTY)) {

            if (value instanceof Character[][]) {

                Character[][] letters = (Character[][]) value;

            }

        }

    }

    @Override
    public String getTabTitle() { return title; }

}