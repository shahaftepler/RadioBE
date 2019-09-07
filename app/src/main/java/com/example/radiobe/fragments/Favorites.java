package com.example.radiobe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.favorites.MyList.FavoriteDataSourceMyList;
import com.example.radiobe.favorites.MyList.FavoriteMyListAdapter;
import com.example.radiobe.favorites.RecentlyAdd.FavoriteRecentlyAddDataSource;
import com.example.radiobe.favorites.RecentlyAdd.FavoriteLastAddAdapter;
import com.example.radiobe.favorites.Recommended.FavoritesRecommendedAdapter;
import com.example.radiobe.favorites.Recommended.FavoritesRecommendedDataSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment {


    public Favorites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //recycleView Top
        RecyclerView recyclerViewTop = view.findViewById(R.id.rvFavoriteTop);

        FavoritesRecommendedAdapter favoritesRecommendedAdapter = new FavoritesRecommendedAdapter(
        CurrentUser.getInstance().getFavorites(), getContext());
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);

        recyclerViewTop.setLayoutManager(layoutManagerTop);
        recyclerViewTop.setAdapter(favoritesRecommendedAdapter);

        //recycleView Middle
        RecyclerView recyclerViewMiddle = view.findViewById(R.id.rvFavoriteMiddle);

        FavoriteLastAddAdapter favoriteLastAddAdapter = new FavoriteLastAddAdapter(
                FavoriteRecentlyAddDataSource.lastAddItems(), getContext());
        LinearLayoutManager layoutManagerMiddle = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);

        recyclerViewMiddle.setLayoutManager(layoutManagerMiddle);
        recyclerViewMiddle.setAdapter(favoriteLastAddAdapter);

        //recycleView Bottom
        RecyclerView recyclerViewBottom = view.findViewById(R.id.rvFavoriteBottom);

        FavoriteMyListAdapter favoriteMyListAdapter = new FavoriteMyListAdapter(
                FavoriteDataSourceMyList.myListItems(),getContext());
        LinearLayoutManager layoutManagerBottom = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);

        recyclerViewBottom.setLayoutManager(layoutManagerBottom);
        recyclerViewBottom.setAdapter(favoriteMyListAdapter);



    }
}
