package com.example.radiobe.adapters;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.radiobe.R;
import com.example.radiobe.database.CurrentUser;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.example.radiobe.database.RefreshFavorites;
import com.example.radiobe.database.UpdateServer;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.RadioItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RadioItemsAdapter extends RecyclerView.Adapter<RadioItemsAdapter.RadioViewHolder> implements Filterable , UpdateServer {
    private List<RadioItem> streams;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<RadioItem> filteredStreams;
    private Activity activity;

    public RadioItemsAdapter(List<RadioItem> streams, RecyclerView recyclerView, Context context, Activity activity) {
        this.streams = streams;
        this.filteredStreams = streams;
        this.recyclerView = recyclerView;
        this.context = context;
        this.activity = activity;
        FirebaseItemsDataSource.getInstance().registerServerObserver(this);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String streamId = intent.getStringExtra("stream_id");
                changeToggles(streamId);
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter("play_song"));
    }




    @NonNull
    @Override
    public RadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View viewItem = inflater.inflate(R.layout.item_radio, parent, false);

        return new RadioViewHolder(viewItem);

    }




    @Override
    public void onBindViewHolder(@NonNull RadioViewHolder holder, int position) {
        RadioItem radioItem = filteredStreams.get(position);
        holder.radioItem = radioItem;
        holder.tvFileName.setText(radioItem.getItemName());
        holder.tvDuration.setText(radioItem.getDurationString());
        holder.tvAdded.setText(String.valueOf(radioItem.getCreationDateString()));
        holder.tvViews.setText(String.valueOf(radioItem.getViews()));
        holder.tvComments.setText(String.valueOf(radioItem.getComments()));
        holder.tvLikes.setText(String.valueOf(radioItem.getLikes()));

        if(CurrentUser.getInstance().getFavorites().contains(radioItem)){
            Drawable d = context.getResources().getDrawable(R.drawable.icons8_heart_red);
            holder.addFavorites.setImageDrawable(d);
        } else {
            Drawable d = context.getResources().getDrawable(R.drawable.icons8_heart_black24);
            holder.addFavorites.setImageDrawable(d);
        }

        holder.addLike.setOnClickListener((v) -> FirebaseItemsDataSource.getInstance().addLikes(radioItem));

        holder.addComment.setOnClickListener((v) -> {
            holder.addCommentEditText.setEnabled(true);
            holder.addCommentEditText.setVisibility(View.VISIBLE);
            holder.closeCommentButton.setVisibility(View.VISIBLE);
            holder.sendButton.setVisibility(View.VISIBLE);

            holder.sendButton.setOnClickListener((button) -> {
                String description = holder.addCommentEditText.getText().toString();
                if (description.length() > 0) {
                    Comment comment = new Comment(firebaseUser.getUid(), new Date().getTime(), description);
                    FirebaseItemsDataSource.getInstance().addComment(comment, radioItem);
                    holder.addCommentEditText.setVisibility(View.GONE);
                    holder.addCommentEditText.setText("");
                    holder.addCommentEditText.setEnabled(false);
                    holder.sendButton.setVisibility(View.GONE);
                    holder.closeCommentButton.setVisibility(View.GONE);

                } else {
                    holder.addCommentEditText.setError("Your comment must include more than 0 characters");
                }
            });

            holder.closeCommentButton.setOnClickListener((b) -> {
                holder.addCommentEditText.setVisibility(View.GONE);
                holder.sendButton.setVisibility(View.GONE);
                holder.closeCommentButton.setVisibility(View.GONE);
                holder.addCommentEditText.setText("");
                holder.addCommentEditText.setEnabled(false);
            });

        });

        holder.tvComments.setOnClickListener((view -> {
            System.out.println(radioItem.getCommentsArray());
            System.out.println(radioItem.getCommentSenders());
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View viewForAlert = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);
            ImageButton close = viewForAlert.findViewById(R.id.idCloseComment);

            builder.setView(viewForAlert);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


            RecyclerView recyclerView = viewForAlert.findViewById(R.id.idRecyclerViewComments);
            recyclerView.setHasFixedSize(true);
            CommentsAdapter commentsAdapter = new CommentsAdapter(radioItem.getCommentsArray(),radioItem.getCommentSenders(),context , activity);
            recyclerView.setAdapter(commentsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            //todo: consider dialog fragment instead.

            close.setOnClickListener((v)->{
                alertDialog.dismiss();
            });



        }));

        holder.tb.setOnClickListener(v -> {
            boolean b = holder.tb.isChecked();
            Intent intent = new Intent("play_song");
            intent.putExtra("stream_name", radioItem.getItemName());
            intent.putExtra("stream_url", radioItem.getFilePath());
            intent.putExtra("stream_id" , radioItem.getUid());
            intent.putExtra("play", b);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            if (b) {
                FirebaseItemsDataSource.getInstance().addView(radioItem);
                System.out.println("Viewed");

            }
            changeToggles(radioItem.getUid());

        });


        holder.shareFacebook.setOnClickListener((v)->{
            Intent intent = new Intent("share_facebook");
            intent.putExtra("stream_url", radioItem.getFilePath());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });

        holder.addFavorites.setOnClickListener((v)->{
            FirebaseItemsDataSource.getInstance().addFavorites(radioItem);
        });

    }


    //TODO : Still need to figure out why i need to press twice for it to work again.
    private void changeToggles(String streamId){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {

            RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if(holder != null) {
                if (!holder.radioItem.getUid().equals(streamId)) {
                    if(holder.tb.isChecked()) {
                        holder.tb.setChecked(false);
                        System.out.println("CHANGED STATE"+holder.radioItem.getItemName());
                    }
                    System.out.println("NEW");
                }
                else {
                    if(!holder.tb.isChecked())
                        holder.tb.setChecked(true);

                    else{
                        holder.tb.setChecked(false);
                    }
                }

            }
        }

        notifyDataSetChanged();

    }


//change 3
    @Override
    public int getItemCount() {
        return filteredStreams.size();
    }

    @Override
    public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        filteredStreams = streams;
                    } else {
                        List<RadioItem> filteredList = new ArrayList<>();
                        for (RadioItem row : streams) {
                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getVodName().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        filteredStreams = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredStreams;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredStreams = (ArrayList<RadioItem>) filterResults.values;
                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            };
    }

    @Override
    public void updateLikes(RadioItem item) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            int finalI = i;
            RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(finalI);
            //holder.radioItem.getItemName()
            if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                System.out.println("Item Changed LIKES---->"+holder.radioItem.getItemName());
                activity.runOnUiThread(()->{
                    holder.tvLikes.setText(String.valueOf(item.getLikes()));
                    holder.tvViews.setText(String.valueOf(item.getViews()));
                    notifyDataSetChanged();
                });
                return;

            }
        }
    }

    @Override
    public void updateComments(RadioItem item) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            int finalI = i;
            RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                System.out.println("Item Changed COMMENTS---->"+holder.radioItem.getItemName());

                activity.runOnUiThread(()->{
                    holder.tvComments.setText(String.valueOf(item.getComments()));
                    notifyDataSetChanged();
                });
                return;
            }
        }

    }

    @Override
    public void updateViews(RadioItem item) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            int finalI = i;
            RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                System.out.println("Item Changed VIEWS---->"+holder.radioItem.getItemName());


                activity.runOnUiThread(()->{
                    holder.tvViews.setText(String.valueOf(item.getViews()));
                    notifyDataSetChanged();
                });
                return;

            }
        }
    }


    class RadioViewHolder extends RecyclerView.ViewHolder implements RefreshFavorites {
        ToggleButton tb;
        TextView tvFileName;
        TextView tvDuration;
        TextView tvAdded;
        FloatingActionButton addFavorites;
        FloatingActionButton shareFacebook;
        ImageButton addLike;
        ImageButton addComment;
        ImageView ivViews;
        TextView tvLikes;
        TextView tvComments;
        TextView tvViews;
        EditText addCommentEditText;
        ImageButton sendButton;
        ImageButton closeCommentButton;
        RadioItem radioItem;


        RadioViewHolder(@NonNull View itemView) {
            super(itemView);
            CurrentUser.getInstance().registerFavoriteObserver(this);
            tb = itemView.findViewById(R.id.tbPlayStop);
            tvFileName = itemView.findViewById(R.id.titleTv);
            tvDuration = itemView.findViewById(R.id.durationTv);
            tvAdded = itemView.findViewById(R.id.addedTv);
            addFavorites = itemView.findViewById(R.id.addFavoriteBtn);
            shareFacebook = itemView.findViewById(R.id.shareFbBtn);
            addComment = itemView.findViewById(R.id.commentBtn);
            ivViews = itemView.findViewById(R.id.viewsIv);
            tvLikes = itemView.findViewById(R.id.likesTv);
            tvComments = itemView.findViewById(R.id.commentsTv);
            tvViews = itemView.findViewById(R.id.viewsTv);
            addLike = itemView.findViewById(R.id.addLike);
            addCommentEditText = itemView.findViewById(R.id.commentEditText);
            sendButton = itemView.findViewById(R.id.sendButton);
            closeCommentButton = itemView.findViewById(R.id.closeCommentButton);

//            tvCloudID = itemView.findViewById(R.id.tvCloudID);
        }



        @Override
        public void refresh(List<RadioItem> favorites) {
            if(favorites.contains(radioItem)){
                Drawable d = context.getResources().getDrawable(R.drawable.icons8_heart_red);
                activity.runOnUiThread(()->{
                    addFavorites.setImageDrawable(d);

                });
            } else {
                Drawable d = context.getResources().getDrawable(R.drawable.icons8_heart_black24);
                activity.runOnUiThread(()->{
                    addFavorites.setImageDrawable(d);

                });
            }
        }
    }
}
