package com.javon.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.overview) TextView overview;
    @Bind(R.id.date) TextView dateView;
    @Bind(R.id.ratingBar) RatingBar rating;
    @Bind(R.id.movie_poster) ImageView poster;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(Movie.KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
             movie = getArguments().getParcelable(Movie.KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);

        if (movie != null) {
            Picasso.with(getContext())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                    .into(poster);

            try {
                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date dateFormat = myFormat.parse(movie.getReleaseDate());

                DateFormat outputFormatter1 = new SimpleDateFormat("MMMM dd yyyy",Locale.US);
                String date = outputFormatter1.format(dateFormat);

                dateView.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            title.setText(movie.getTitle() != null ? movie.getTitle():movie.getOriginalTitle());
            overview.setText(movie.getOverview());

            float value = movie.getVoteAverage();

            rating.setRating(value/2);


        }

        LinearLayout root = (LinearLayout) rootView.findViewById(R.id.root);
        new LoadVideosTask(getActivity(),root).execute(movie);
        return rootView;
    }

}
