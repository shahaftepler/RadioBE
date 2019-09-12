package com.example.radiobe.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.radiobe.MainActivity;
import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.example.radiobe.database.RefreshFavorites;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.RadioItem;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> implements RefreshFavorites{
    /*Properties*/
    private List<RadioItem> favoriteItemList;
    private Context context;
    Activity activity;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//    RefreshFavorites refreshFavorites;


    /*Constructor*/
    public FavoritesAdapter(List<RadioItem> favoriteItemList, Activity activity) {
        this.favoriteItemList = favoriteItemList;
        this.context = activity;
        this.activity = activity;
        CurrentUser.getInstance().registerFavoriteObserver(this);
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

        holder.tvFavoriteTitle.setText(favoriteRecommendedItem.getItemName());
        holder.toggleButtonFavorite.setOnCheckedChangeListener((v,b)->{
            Intent intent = new Intent("play_song");
            intent.putExtra("stream_name", favoriteRecommendedItem.getItemName());
            intent.putExtra("stream_url", favoriteRecommendedItem.getFilePath());
            intent.putExtra("play", b);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            if (b) {
                FirebaseItemsDataSource.getInstance().addView(favoriteRecommendedItem);
                System.out.println("Viewed");
//                changeToggles();
            }
        });
//        holder.tvFavoriteDescription.setText(favoriteRecommendedItem.getItemName());


        holder.deleteFavorite.setOnClickListener((v)->{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to delete " + favoriteRecommendedItem.getItemName() + " from your favorites?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ref.child("favorites").child(CurrentUser.getInstance().getFireBaseID()).child(favoriteRecommendedItem.getUid()).removeValue();
                            Toast.makeText(context, favoriteRecommendedItem.getItemName() + " Deleted from favorites!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(context, "No change!", Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

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



//    public void initRefreshListener(Activity activity){
//        refreshFavorites = new RefreshFavorites() {
//            @Override
//            public void refresh(List<RadioItem> favorites) {
//                favoriteItemList = favorites;
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("Suppose to update");
//                        notifyDataSetChanged();
//                    }
//                });
//
//            }
//        };
//        System.out.println(refreshFavorites + "LISTENER");
//        CurrentUser.getInstance().setRefreshFavoritesListener(refreshFavorites);
//    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder {
        /*Properties*/
        ToggleButton toggleButtonFavorite;
        TextView tvFavoriteTitle;
        ImageButton deleteFavorite;


        /*Constructor*/
        private FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            toggleButtonFavorite = itemView.findViewById(R.id.toggleButtonFavorite);
            tvFavoriteTitle = itemView.findViewById(R.id.tvItemFavoriteTitle);
            deleteFavorite = itemView.findViewById(R.id.deleteFavorite);
        }





    }

}
