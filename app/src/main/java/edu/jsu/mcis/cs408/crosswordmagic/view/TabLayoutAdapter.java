package edu.jsu.mcis.cs408.crosswordmagic.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabLayoutAdapter extends FragmentStateAdapter {

    public static final int NUM_TABS = 2;

    public TabLayoutAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new PuzzleFragment();
            case 1:
                return new ClueFragment();
            default:
                return  new PuzzleFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }

}