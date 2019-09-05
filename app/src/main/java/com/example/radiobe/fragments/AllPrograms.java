package com.example.radiobe.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.radiobe.R;
import com.example.radiobe.adapters.RadioItemsAdapter;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class AllPrograms extends Fragment {
    private RecyclerView recyclerView;
//    private RadioItemsAdapter adapter;

//    RadioItemsDataSource source;
    FirebaseItemsDataSource source = FirebaseItemsDataSource.getInstance();
    private SearchView searchView;

    private TabLayout tabs;
//    private ProgressBar progressBar;
    RadioItemsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_programs, container, false);
        return v;
    }

    public AllPrograms(){

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler);
//        progressBar = view.findViewById(R.id.progressBar);
        adapter = new RadioItemsAdapter(FirebaseItemsDataSource.getInstance().getFireBaseStreams(), recyclerView,  getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.updateLikes();

        searchView = view.findViewById(R.id.searchView);
        if (getActivity() != null) {

            System.out.println("SearchManager Should Work");
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
        }


//        new RadioItemsDataSource(recyclerView, progressBar).execute();
//        source = new RadioItemsDataSource(recyclerView, progressBar);
//        source.execute();


//            source.initGeneral(recyclerView,progressBar);
//        source = new FirebaseItemsDataSource(recyclerView, progressBar , null);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (adapter != null) {
                    System.out.println("Not Null");
                    adapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    System.out.println("Not Null");
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

    }

}

//        new RadioItemsDataSource(recyclerView, progressBar).execute();
