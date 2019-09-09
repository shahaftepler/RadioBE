////package com.example.radiobe.allPrograms;
////
////import com.example.radiobe.R;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class RadioItemsDataSource {
////
////    // here we will use the API
////    public static List<RadioItem> getItems(){
////        List<RadioItem> items = new ArrayList<>();
////
////                                        //TODO solve the local date issue.
////        for (int i = 0; i < 50; i++) {
////
////        items.add(new RadioItem("Shahaf Tepler","23:20", null ,0,0,0, R.id.tbPlayStop));
////        items.add(new RadioItem("Yarden Swisa","1:30:20", null ,0,0,0,R.id.tbPlayStop));
////        items.add(new RadioItem("Daniel Mizrahi","40:21",null,0,0,0,R.id.tbPlayStop));
////        items.add(new RadioItem("Amitay a gever","30:21",null ,0,0,0,R.id.tbPlayStop));
////        }
////
////        return items;
////    }
////}
//
//package com.example.radiobe.database;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.provider.MediaStore;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.example.radiobe.StreamDAO;
//import com.example.radiobe.adapters.RadioItemsAdapter;
//import com.example.radiobe.models.RadioItem;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class RadioItemsDataSource extends AsyncTask<Void, Void, List<RadioItem>> {
//
//    private WeakReference<RecyclerView> rvRadioItems;
//    private WeakReference<ProgressBar> progressBar;
//    public RadioItemsAdapter adapter;
//
//    public RadioItemsDataSource(RecyclerView rvRadioItems, ProgressBar progressBar) {
//        this.rvRadioItems = new WeakReference<>(rvRadioItems);
//        this.progressBar = new WeakReference<>(progressBar);
//    }
//
//
//    @Override
//    protected List<RadioItem> doInBackground(Void... voids) {
//
//
//        //may change it to sort by date..
//        List<RadioItem> streams = StreamDAO.getInstance().getStreamsFromDataBase();
//        Collections.sort(streams, new Comparator<RadioItem>() {
//            @Override
//            public int compare(RadioItem o1, RadioItem o2) {
//                return o1.getVodName().compareToIgnoreCase(o2.getVodName()); // To compare string values
//            }
//        });
//        return streams;
//    }
//
//
//    @Override
//    protected void onPostExecute(List<RadioItem> streams) {
//
//        //Weak ref: test if the recycler is not null (Only if they switched fragment)
//        System.out.println(streams);
//        RecyclerView recycler = this.rvRadioItems.get(); //check if what rvSongs is pointing on , is not null
//        ProgressBar pb = this.progressBar.get();
//
//
//        if (pb == null) return;
//        pb.setVisibility(View.GONE);
//
//        if (recycler == null) return;
//
//        Context context = recycler.getContext();
//        adapter = new RadioItemsAdapter(streams,recycler , pb, context);
//        recycler.setAdapter(adapter);
//
//        recycler.setLayoutManager(new LinearLayoutManager(context));//may change to a different manager (grid, staggered..)
//        System.out.println("Refresh");
//
//        //may update UI
//    }
//}
//
//
//
