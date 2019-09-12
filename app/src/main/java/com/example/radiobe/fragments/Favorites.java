package com.example.radiobe.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.adapters.FavoritesAdapter;
import com.example.radiobe.database.RefreshFavorites;
import com.example.radiobe.models.RadioItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment implements RefreshFavorites {
    RecyclerView recyclerView;
    FavoritesAdapter favoritesAdapter;
    TextView noFavorites;

    public Favorites() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        noFavorites = view.findViewById(R.id.noFavorites);
        recyclerView = view.findViewById(R.id.rvFavoriteTop);

        favoritesAdapter = new FavoritesAdapter(
        CurrentUser.getInstance().getFavorites() , getActivity());
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(layoutManagerTop);
        recyclerView.setAdapter(favoritesAdapter);
//        favoritesAdapter.initRefreshListener(getActivity());

        CurrentUser.getInstance().registerFavoriteObserver(this);

        if(CurrentUser.getInstance().getFavorites().size() > 0){
            noFavorites.setVisibility(View.GONE);
        } else {
            noFavorites.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public void refresh(List<RadioItem> favorites) {
        if(favorites.size() > 0){
            getActivity().runOnUiThread(()->{
                noFavorites.setVisibility(View.GONE);
            });
        } else {
            getActivity().runOnUiThread(()->{
                noFavorites.setVisibility(View.VISIBLE);
            });
        }
    }
}
