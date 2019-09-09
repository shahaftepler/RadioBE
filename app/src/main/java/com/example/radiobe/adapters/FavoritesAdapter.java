package com.example.radiobe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.radiobe.R;
import com.example.radiobe.models.RadioItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {
    /*Properties*/
    private List<RadioItem> favoriteItemList;
    private Context context;

    /*Constructor*/
    public FavoritesAdapter(List<RadioItem> favoriteItemList, Context context) {
        this.favoriteItemList = favoriteItemList;
        this.context = context;
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

        holder.ivFavoriteItemImage.setImageResource(favoriteRecommendedItem.getResImage());
        holder.tvFavoriteTitle.setText(favoriteRecommendedItem.getItemName());
        holder.tvFavoriteDescription.setText(favoriteRecommendedItem.getItemName());
    }

    @Override
    public int getItemCount() {
        return favoriteItemList.size();
    }

    class FavoritesViewHolder extends RecyclerView.ViewHolder {
        /*Properties*/
        ImageView ivFavoriteItemImage;
        TextView tvFavoriteTitle;
        TextView tvFavoriteDescription;


        /*Constructor*/
        private FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFavoriteItemImage = itemView.findViewById(R.id.civFavoriteItemImage);
            tvFavoriteTitle = itemView.findViewById(R.id.tvItemFavoriteTitle);
            tvFavoriteDescription = itemView.findViewById(R.id.tvItemFavoriteDescription);
        }





    }

}
