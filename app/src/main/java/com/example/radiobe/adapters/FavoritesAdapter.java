package com.example.radiobe.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.example.radiobe.database.RefreshFavorites;
import com.example.radiobe.models.RadioItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> implements RefreshFavorites {
    //Properties
    private List<RadioItem> favoriteItemList;
    private Context context;
    private Activity activity;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private RecyclerView recyclerView;


    /*Constructor*/
    public FavoritesAdapter(List<RadioItem> favoriteItemList, Activity activity) {
        this.favoriteItemList = favoriteItemList;
        this.context = activity;
        this.activity = activity;
        CurrentUser.getInstance().registerFavoriteObserver(this);
        BroadcastReceiver mediaBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String streamId = intent.getStringExtra("stream_id");
                changeToggles(streamId);
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(mediaBroadcastReceiver, new IntentFilter("play_song"));
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_favorite, parent, false);

        return new FavoritesViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {

        RadioItem favoriteRecommendedItem = favoriteItemList.get(position);
        holder.radioItem = favoriteRecommendedItem;
        holder.tvFavoriteTitle.setText(favoriteRecommendedItem.getItemName());
        holder.toggleButtonFavorite.setOnClickListener(v -> {
            boolean b = holder.toggleButtonFavorite.isChecked();
            Intent intent = new Intent("play_song");
            intent.putExtra("stream_name", favoriteRecommendedItem.getItemName());
            intent.putExtra("stream_url", favoriteRecommendedItem.getFilePath());
            intent.putExtra("stream_id", favoriteRecommendedItem.getUid());
            intent.putExtra("play", b);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            if (b) {
                FirebaseItemsDataSource.getInstance().addView(favoriteRecommendedItem);
                System.out.println("Viewed");

            }
            changeToggles(favoriteRecommendedItem.getUid());

        });


        holder.deleteFavorite.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete " + favoriteRecommendedItem.getItemName() + " from your favorites?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        ref.child("favorites").child(CurrentUser.getInstance().getFireBaseID()).child(favoriteRecommendedItem.getUid()).removeValue();
                        Toast.makeText(context, favoriteRecommendedItem.getItemName() + " Deleted from favorites!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Nope", (dialog, id) -> Toast.makeText(context, "No change!", Toast.LENGTH_SHORT).show());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    private void changeToggles(String streamId) {

        if (recyclerView != null) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {

                FavoritesAdapter.FavoritesViewHolder holder = (FavoritesAdapter.FavoritesViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    if (!holder.radioItem.getUid().equals(streamId)) {
                        if (holder.toggleButtonFavorite.isChecked()) {
                            holder.toggleButtonFavorite.setChecked(false);
                            System.out.println("CHANGED STATE" + holder.radioItem.getItemName());
                        }
                        System.out.println("NEW");
                    } else {
                        if (!holder.toggleButtonFavorite.isChecked())
                            holder.toggleButtonFavorite.setChecked(true);

                        else {
                            holder.toggleButtonFavorite.setChecked(false);
                        }
                    }

                }

            }

            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    @Override
    public void refresh(List<RadioItem> favorites) {
        favoriteItemList = favorites;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Suppose to update");
                notifyDataSetChanged();
            }
        });

    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder {
        /*Properties*/
        ToggleButton toggleButtonFavorite;
        TextView tvFavoriteTitle;
        ImageButton deleteFavorite;
        RadioItem radioItem;


        /*Constructor*/
        private FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            toggleButtonFavorite = itemView.findViewById(R.id.toggleButtonFavorite);
            tvFavoriteTitle = itemView.findViewById(R.id.tvItemFavoriteTitle);
            deleteFavorite = itemView.findViewById(R.id.deleteFavorite);

        }

    }

}
