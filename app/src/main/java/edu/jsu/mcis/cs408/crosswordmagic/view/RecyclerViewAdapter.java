package edu.jsu.mcis.cs408.crosswordmagic.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.PuzzleListItem;
import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.databinding.PuzzleListEntryBinding;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final MenuActivity activity;
    private List<PuzzleListItem> puzzleList;

    public RecyclerViewAdapter(MenuActivity activity, List<PuzzleListItem> puzzles) {
        super();
        this.puzzleList = puzzles;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PuzzleListEntryBinding binding = PuzzleListEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().setOnClickListener(activity.getItemClick()); // the click handler
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setEntry(puzzleList.get(position));
        holder.bindData();
    }

    @Override
    public int getItemCount() {return puzzleList.size();}

    public PuzzleListItem getEntryAtPosition(int position) {
        return puzzleList.get(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PuzzleListItem entry;
        private TextView entryLabel;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public PuzzleListItem getEntry() { return entry; }

        public void setEntry(PuzzleListItem entry) {
            this.entry = entry;
        }

        public void bindData() {

            if (entryLabel == null) {
                entryLabel = (TextView) itemView.findViewById(R.id.entryLabel);
            }
            StringBuilder builder = new StringBuilder().append(entry.getId()).append(": ").append(entry.toString());
            entryLabel.setText(builder.toString());

        }

    }

}