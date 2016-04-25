package com.javon.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Javon Davis
 *         Created by Javon Davis on 22/04/16.
 */
public class LoadMovieDataTask extends AsyncTask<Movie, Void, Movie> {

    private ViewGroup rootView;
    private Context mContext;

    public LoadMovieDataTask(Context context, ViewGroup root)
    {
        this.mContext = context;
        this.rootView = root;
    }

    @Override
    protected Movie doInBackground(Movie... values) {
        String key = mContext.getString(R.string.API_KEY);
        HttpURLConnection urlConnection = null;
        Movie movie = values[0];
        try {
            URL url = new URL("http://api.themoviedb.org/3/movie/"+movie.getId()+"/videos?api_key="+key);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Reader reader = new InputStreamReader(in);

            JsonElement jsonElement = new JsonParser().parse(reader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            Type listType = new TypeToken<List<Video>>(){}.getType();
            ArrayList<Video> trailerResults = gson.fromJson(jsonObject.get("results"), listType);

            url = new URL("http://api.themoviedb.org/3/movie/"+movie.getId()+"/reviews?api_key="+key);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());

            reader = new InputStreamReader(in);

            jsonElement = new JsonParser().parse(reader);
            jsonObject = jsonElement.getAsJsonObject();

            gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();

            listType = new TypeToken<List<Review>>(){}.getType();
            ArrayList<Review> reviewResults = gson.fromJson(jsonObject.get("results"), listType);
            movie.setReviews(reviewResults);
            movie.setTrailers(trailerResults);

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return movie;
    }

    @Override
    protected void onPostExecute(Movie movie) {
        ArrayList<Video> trailers = (ArrayList<Video>) movie.getTrailers();
        ArrayList<Review> reviews = (ArrayList<Review>) movie.getReviews();
        if(trailers != null && !trailers.isEmpty())
        {
            TextView header = new TextView(mContext);
            header.setText(R.string.trailers);
            header.setGravity(Gravity.CENTER);
            int size = mContext.getResources().getInteger(R.integer.heading_size);
            header.setTextSize(size);
            header.setTypeface(Typeface.DEFAULT_BOLD);
            rootView.addView(header);
            for(final Video video: trailers)
            {
                Button button = new Button(mContext);
                button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                button.setText(video.getName());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+video.getKey())));
                    }
                });
                rootView.addView(button);
            }
        }

        if(reviews != null && !reviews.isEmpty())
        {
            TextView header = new TextView(mContext);
            header.setText(R.string.reviews);
            header.setGravity(Gravity.CENTER);
            int size = mContext.getResources().getInteger(R.integer.heading_size);
            header.setTextSize(size);
            header.setTypeface(Typeface.DEFAULT_BOLD);
            rootView.addView(header);
            for(Review review:reviews)
            {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View reviewView = inflater.inflate(R.layout.review_item, rootView, false);
                ButterKnife.bind(mContext,reviewView);

                TextView content = (TextView) reviewView.findViewById(R.id.content);
                TextView author = (TextView) reviewView.findViewById(R.id.author);

                content.setText(review.getContent());
                author.setText("Author:"+review.getAuthor());
                rootView.addView(reviewView);
            }
        }
    }
}
