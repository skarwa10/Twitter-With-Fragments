package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by skarwa on 9/28/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HAS_MEDIA= 1, NO_MEDIA = 0;
    private List<Tweet> mTweets;
    private Context mContext;
    private OnTweetSelectedListener listener;

    public interface OnTweetSelectedListener {
        void onTweetSelected(Tweet article);
    }

    // Pass in the contact array into the constructor
    public TweetAdapter(Context context, ArrayList<Tweet> tweets) {
        mTweets = tweets;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        System.out.print("viewType:" + viewType);
        switch (viewType) {
            case HAS_MEDIA:
                View v1 = inflater.inflate(R.layout.item_tweet_with_media, viewGroup, false);
                viewHolder = new ViewHolderWithMedia(v1);
                break;
            case NO_MEDIA:
                View v2 = inflater.inflate(R.layout.item_tweet_no_media, viewGroup, false);
                viewHolder = new ViewHolderNoMedia(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.item_tweet_no_media, viewGroup, false);
                viewHolder = new ViewHolderNoMedia(v);
                break;
        }
        return viewHolder;
    }


    /**
     * This method internally calls onBindViewHolder(ViewHolder, int) to update the
     * RecyclerView.ViewHolder contents with the item at the given position
     * and also sets up some private fields to be used by RecyclerView.
     *
     * @param viewHolder The type of RecyclerView.ViewHolder to populate
     * @param position   Item position in the viewgroup.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case HAS_MEDIA:
                ViewHolderWithMedia vh1 = (ViewHolderWithMedia) viewHolder;
                configureViewHolderWithMedia(vh1, position);
                break;
            case NO_MEDIA:
                ViewHolderNoMedia vh2 = (ViewHolderNoMedia) viewHolder;
                configureViewHolderWithNoMedia(vh2, position);
                break;
            default:
                ViewHolderNoMedia vh = (ViewHolderNoMedia) viewHolder;
                configureViewHolderWithNoMedia(vh, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mTweets.get(position)!=null){
            return mTweets.get(position).isTweetWithMedia();  //TODO
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    private Context getContext() {
        return mContext;
    }

    public class ViewHolderNoMedia extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;

        View view;

        ViewHolderNoMedia(View itemView) {
            super(itemView);
            view= itemView;
            ButterKnife.bind(this, itemView);

        }
    }

    class ViewHolderWithMedia extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvBody)
        TextView tvBody;

        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;

        //TODO add media holder

        View view;


        ViewHolderWithMedia(View itemView) {
            super(itemView);
            view= itemView;
            ButterKnife.bind(this, itemView);

        }
    }

    private void configureViewHolderWithMedia(ViewHolderWithMedia vh1, int position) {
        Tweet tweet = mTweets.get(position);
        if (tweet != null) {
            User user = tweet.getUser();
            String relativeTimestamp = getRelativeTimeAgo(tweet.getCreatedAt());

            vh1.tvUserName.setText(user.getScreenName());
            vh1.tvBody.setText(tweet.getBody());
            vh1.tvName.setText(user.getName());
            vh1.tvTimestamp.setText(relativeTimestamp);

            Glide.with(getContext()).load(user.getProfileImageUrl()).fitCenter()
                    .into(vh1.ivProfileImage);

            vh1.view.setOnClickListener(v -> {
                listener.onTweetSelected(tweet);
            });
        }
    }

    private void configureViewHolderWithNoMedia(ViewHolderNoMedia vh2, int position) {
        Tweet tweet = mTweets.get(position);
        if (tweet != null) {
            User user = tweet.getUser();
            String relativeTimestamp = getRelativeTimeAgo(tweet.getCreatedAt());

            vh2.tvUserName.setText("@"+user.getScreenName());
            vh2.tvBody.setText(tweet.getBody());
            vh2.tvName.setText(user.getName());
            vh2.tvTimestamp.setText(relativeTimestamp);

            Glide.with(getContext()).load(user.getProfileImageUrl()).fitCenter()
                    .into(vh2.ivProfileImage);

            vh2.view.setOnClickListener(v -> {
                listener.onTweetSelected(tweet);
            });
        }
    }

    public void setOnTweetClickListener(OnTweetSelectedListener listener) {
        this.listener = listener;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE).toString();

            relativeDate = relativeDate.replaceAll("\\s+","");
            if(relativeDate.matches("(.*)min.ago")){
                relativeDate =  relativeDate.replace("min.ago","m");
            } else if(relativeDate.matches("(.*)hr.ago")){
                relativeDate = relativeDate.replace("hr.ago","h");
            } else if(relativeDate.matches("(.*)sec.ago")){
                relativeDate = relativeDate.replace("sec.ago","s");
            } else if(relativeDate.matches("In(.*)sec")){
                relativeDate = relativeDate.replace("In","");
                relativeDate = relativeDate.replace("sec","s");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        int size = mTweets.size();
        mTweets.clear();
        notifyItemRangeRemoved(0,size);
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        int curSize =  mTweets.size();
        mTweets.addAll(list);
        notifyItemRangeInserted(curSize,mTweets.size());
    }

}
