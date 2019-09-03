package com.example.radiobe.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.radiobe.R;
import com.example.radiobe.StreamDAO;
import com.example.radiobe.database.AddCommentListener;
import com.example.radiobe.database.ChangeLikesListener;
import com.example.radiobe.database.ChangeViewsListener;
import com.example.radiobe.database.ChangeViewsTask;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.example.radiobe.database.UpdateServer;
import com.example.radiobe.fragments.MainScreen;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.RadioItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.threeten.bp.LocalDate;

public class RadioItemsAdapter extends RecyclerView.Adapter<RadioItemsAdapter.RadioViewHolder> implements Filterable {
    //    List<RadioItem> items;
    List<RadioItem> streams;
    RecyclerView recyclerView;
//    ProgressBar progressBar;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Context context;
    List<RadioItem> filteredStreams;
    UpdateServer updateServer;

    public RadioItemsAdapter(List<RadioItem> streams, RecyclerView recyclerView, Context context) {
        this.streams = streams;
        //change 1
        this.filteredStreams = streams;
        this.recyclerView = recyclerView;
//        this.progressBar = pb;
        this.context = context;
    }




    @NonNull
    @Override
    public RadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View viewItem = inflater.inflate(R.layout.item_radio, parent, false);

        RadioViewHolder holder = new RadioViewHolder(viewItem);

        return holder;

    }




    @Override
    public void onBindViewHolder(@NonNull RadioViewHolder holder, int position) {
        //change 2

        RadioItem radioItem = filteredStreams.get(position);
        holder.radioItem = radioItem;
//        RadioItem radioItem = streams.get(position);
        holder.tvFileName.setText(radioItem.getItemName());
//        holder.tvDuration.setText(String.valueOf(radioItem.getDuration()));
        holder.tvDuration.setText(radioItem.getDurationString());
        holder.tvAdded.setText(String.valueOf(radioItem.getCreationDateString()));
        holder.tvViews.setText(String.valueOf(radioItem.getViews()));
        holder.tvComments.setText(String.valueOf(radioItem.getComments()));
        holder.tvLikes.setText(String.valueOf(radioItem.getLikes()));
//        holder.tvCloudID.setText(radioItem.get_id());

        //holder.tb.setBackgroundResource(radioItem.getResImage());


        holder.addLike.setOnClickListener((v) -> {
//            System.out.println("Clicked");
//            holder.addLike.setOnClickListener(null);
//            StreamDAO.getInstance().handleLikes(firebaseUser, radioItem, new ChangeLikesListener() {
//                @Override
//                public void done() {
//                    holder.tvLikes.setText(String.valueOf(radioItem.getLikes()));
//                    notifyItemChanged(position);
//
//                }
//            });

            FirebaseItemsDataSource.getInstance().addLikes(radioItem);


//            //TODO: firebaseitemsDatasource.getInstance().addLike();
//            FirebaseItemsDataSource.getInstance().addLikes(radioItem,updateLikes);
//            updateLikes();


        });

        holder.addComment.setOnClickListener((v) -> {
            holder.addCommentEditText.setEnabled(true);
            holder.addCommentEditText.setVisibility(View.VISIBLE);
            holder.closeCommentButton.setVisibility(View.VISIBLE);
            holder.sendButton.setVisibility(View.VISIBLE);

            holder.sendButton.setOnClickListener((button) -> {
                String description = holder.addCommentEditText.getText().toString();
                if (description.length() > 0) {
                    Comment comment = new Comment(firebaseUser.getUid(), new Date().getTime(), description);
//                    StreamDAO.getInstance().handleComments(firebaseUser, radioItem, comment, new AddCommentListener() {
//                        @Override
//                        public void done() {
//                            holder.tvComments.setText(String.valueOf(radioItem.getComments()));
//                            notifyItemChanged(position);
//                        }
//                    });
                    FirebaseItemsDataSource.getInstance().addComment(comment, radioItem);
                    holder.addCommentEditText.setVisibility(View.GONE);
                    holder.addCommentEditText.setText("");
                    holder.addCommentEditText.setEnabled(false);
                    holder.sendButton.setVisibility(View.GONE);

                } else {
                    holder.addCommentEditText.setError("Your comment must include more than 0 characters");
                }
            });
            //TODO open a input box for comment with button to send and to exit

            holder.closeCommentButton.setOnClickListener((b) -> {
                holder.addCommentEditText.setVisibility(View.GONE);
                holder.sendButton.setVisibility(View.GONE);
                holder.closeCommentButton.setVisibility(View.GONE);

                holder.addCommentEditText.setText("");
                holder.addCommentEditText.setEnabled(false);
            });

        });



        holder.tb.setOnCheckedChangeListener((v, b) -> {
            Intent intent = new Intent("play_song");
            intent.putExtra("stream_name", radioItem.getItemName());
            intent.putExtra("stream_url", radioItem.getFilePath());
            intent.putExtra("play", b);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            changeToggles();
            if (b) {
                FirebaseItemsDataSource.getInstance().addView(radioItem);
                System.out.println("Viewed");
            }

//            StreamDAO.getInstance().handleViews(firebaseUser, radioItem, new ChangeViewsListener() {
//                @Override
//                public void done() {
//                    holder.tvViews.setText(String.valueOf(radioItem.getViews()));
//                }
//            });
        });

        holder.shareFacebook.setOnClickListener((v)->{
            Intent intent = new Intent("share_facebook");
            intent.putExtra("stream_url", radioItem.getFilePath());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        });

        //holder.tb.setBackgroundResource(radioItem.getResImage());


        holder.addFavorites.setOnClickListener((v)->{
            FirebaseItemsDataSource.getInstance().addFavorites(radioItem);
        });

    }


    //TODO : Still need to figure out why i need to press twice for it to work again.
    private void changeToggles(){
        for (int i = 0; i < recyclerView.getChildCount(); i++) {

            RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (MainScreen.simpleExoPlayer.getPlaybackState() == Player.STATE_READY &&
                    MainScreen.simpleExoPlayer.getCurrentTag() != holder.tvFileName){
                System.out.println("NEW IF");

                holder.tb.setChecked(false);
                notifyItemChanged(i);
            }
        }
    }

    public void updateLikes(){
        updateServer = new UpdateServer() {
            @Override
            public void updateLikes(RadioItem item) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {

                    RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    //holder.radioItem.getItemName()
                    if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                        System.out.println("Item Changed---->"+holder.radioItem.getItemName());

                        holder.tvLikes.setText(String.valueOf(item.getLikes()));
                        holder.tvViews.setText(String.valueOf(item.getViews()));
                        notifyItemChanged(i);
                        return;
                    }
                }
            }

            @Override
            public void updateComments(RadioItem item) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {

                    RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    //holder.radioItem.getItemName()
                    if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                        System.out.println("Item Changed---->"+holder.radioItem.getItemName());
                        holder.tvComments.setText(String.valueOf(item.getComments()));
                        notifyItemChanged(i);
                        return;
                    }
                }
            }

            @Override
            public void updateViews(RadioItem item) {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {

                    RadioViewHolder holder = (RadioViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    //holder.radioItem.getItemName()
                    if ((holder != null) && (holder.radioItem.getUid().equals(item.getUid()))){
                        System.out.println("Item Changed---->"+holder.radioItem.getItemName());
                        holder.tvViews.setText(String.valueOf(item.getViews()));
                        notifyItemChanged(i);
                        return;
                    }
                }
            }


            };

        FirebaseItemsDataSource.getInstance().setUpdateLikes(updateServer);
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

    class RadioViewHolder extends RecyclerView.ViewHolder {
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
//        TextView tvCloudID;


        public RadioViewHolder(@NonNull View itemView) {
            super(itemView);
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
    }
}

