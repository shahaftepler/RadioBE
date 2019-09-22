package com.example.radiobe.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.radiobe.R;
import com.example.radiobe.database.FirebaseItemsDataSource;
import com.example.radiobe.database.UpdateServer;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.RadioItem;
import com.example.radiobe.models.User;
import java.util.List;
import java.util.Map;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> implements UpdateServer {
    //properties
    private List<Comment> commentList;
    private Context context;
    private Map<String , User> commentSenders;
    private Activity activity;

    //constructor
    CommentsAdapter(List<Comment> commentList, Map<String, User> commentSenders, Context context, Activity activity){
        this.commentList = commentList;
        this.context = context;
        this.commentSenders = commentSenders;
        this.activity = activity;
        FirebaseItemsDataSource.getInstance().registerServerObserver(this);
        System.out.println("----+++++"+commentList);
    }


    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_comment_user
        , parent, false);

        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        User user = commentSenders.get(comment.getUid());
        if(user != null) {
            holder.tvUserNameComment.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            holder.imageUserComment.setImageBitmap(user.getProfileImage());
            holder.tvCommentText.setText(comment.getDescription());
            holder.tvCommentTime.setText(comment.getCreationDateString());
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public void updateLikes(RadioItem item) {

    }

    @Override
    public void updateComments(RadioItem item) {
        commentList = item.getCommentsArray();
        commentSenders = item.getCommentSenders();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updateViews(RadioItem item) {

    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUserComment;
        TextView tvUserNameComment;
        TextView tvCommentText;
        TextView tvCommentTime;

        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserComment = itemView.findViewById(R.id.ivImageComment);
            tvUserNameComment = itemView.findViewById(R.id.tvUserName);
            tvCommentText = itemView.findViewById(R.id.tvCommentDescription);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
        }
    }
}