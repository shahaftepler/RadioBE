package com.example.radiobe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.radiobe.R;
import com.example.radiobe.models.Comment;
import com.example.radiobe.models.User;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    List<Comment> commentList;
    Context context;

    //constructor
    public CommentsAdapter(List<Comment> commentList,Context context){
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_comment_user
        , parent, false);
        CommentsViewHolder commentsViewHolder = new CommentsViewHolder(view);

        return commentsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment comment = commentList.get(position);
//        User user = commentSenders.get(commentItem.getUid());
//        holder.tvUserNameComment.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
//        holder.imageUserComment.setImageBitmap(user.getProfileImage());
        holder.tvCommentText.setText(comment.getDescription());
        holder.tvCommentTime.setText(comment.getCreationDateString());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageUserComment;
        TextView tvUserNameComment;
        TextView tvCommentText;
        TextView tvCommentTime;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageUserComment = itemView.findViewById(R.id.ivImageComment);
            tvUserNameComment = itemView.findViewById(R.id.tvUserName);
            tvCommentText = itemView.findViewById(R.id.tvCommentDescription);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
        }
    }
}