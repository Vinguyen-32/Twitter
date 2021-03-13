package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Tweet> tweetList) {
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        ImageView postImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            postImage = itemView.findViewById(R.id.postImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(16)))
                    .into(ivProfileImage);
            tvTimestamp.setText(tweet.getFormattedTimestamp());

            class RetrieveOGImageTask extends AsyncTask<String, Void, String> {

                private Exception exception;

                protected String doInBackground(String... urls) {
                    try {
                        Connection con = Jsoup.connect(urls[0]);
                        Document doc = con.get();

                        String imageUrl = null;
                        Elements metaOgImage = doc.select("meta[property=og:image]");
                        if (metaOgImage != null) {
                            imageUrl = metaOgImage.attr("content");
                        }
                        return imageUrl;
                    } catch (Exception e) {
                        this.exception = e;
                        return null;
                    }
                }

                protected void onPostExecute(String url) {
                    if (url != null && url != ""){
                        Picasso.get()
                                .load(url)
                                .transform(new RoundedCornersTransformation(30, 30))
                                .into(postImage);
                    }
                }
            }

            try {
//                new RetrieveOGImageTask().execute(tweet.source);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
