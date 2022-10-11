package com.mobileproject.valhalla.utils.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobileproject.valhalla.R;
import com.mobileproject.valhalla.utils.Models.Rank;

import java.util.ArrayList;


// this class is an adapter for the leaderboard page's recycler view
// used to collect the data from the arraylist and bind it to the rank.xml file for every element in the arraylist
// then bind all of it in the recycler view in the profile page
public class LeaderboardRankAdapter extends RecyclerView.Adapter<LeaderboardRankAdapter.RankHolder> {
    // this list will holds the data that will be placed within the recycler view
    private ArrayList<Rank> ranks;

    public LeaderboardRankAdapter(ArrayList<Rank> ranks) {
        this.ranks = ranks;
    }

    // this is a holder for the view that select the components we need to modify from the rank.xml file
    public class RankHolder extends RecyclerView.ViewHolder{
        TextView numbering, username, rank;
        public RankHolder(@NonNull View itemView) {
            super(itemView);
            numbering = itemView.findViewById(R.id.numbering);
            username = itemView.findViewById(R.id.username);
            rank = itemView.findViewById(R.id.rank);
        }
    }

    // when the holder start working we need to pass the xml file to it
    @NonNull
    @Override
    public RankHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rank = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank, parent, false);
        return new RankHolder(rank);
    }

    // on binding we will fill the components within the rank.xml file
    // NOTE: this method will be called for every element in the arraylist of this class
    @Override
    public void onBindViewHolder(@NonNull RankHolder holder, int position) {
        // get the username and the rank from arraylist
        String username = ranks.get(position).getRankHolder();
        String rank = ranks.get(position).getRank();

        // set the components
        holder.numbering.setText("" + ( position+1 ) );
        holder.username.setText(username);
        holder.rank.setText(rank);
    }

    // return the number of items within the arraylist
    @Override
    public int getItemCount() {
        return ranks.size();
    }


}
