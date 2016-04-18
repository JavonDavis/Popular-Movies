package com.javon.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
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

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    private Movie movie;

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

        if (movie != null) {
            ImageView poster = (ImageView) rootView.findViewById(R.id.movie_poster);

            Picasso.with(getContext())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                    .into(poster);

            TextView title = (TextView) rootView.findViewById(R.id.title);
            TextView overview = ((TextView) rootView.findViewById(R.id.overview));
            TextView dateView = (TextView) rootView.findViewById(R.id.date);
            RatingBar rating = (RatingBar) rootView.findViewById(R.id.ratingBar);

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
        new LoadVideosTask(root).execute(movie.getId());
        return rootView;
    }

    private class LoadVideosTask extends AsyncTask<Integer, Void, ArrayList<Video>>
    {
        private ViewGroup rootView;

        public LoadVideosTask(ViewGroup root)
        {
            rootView = root;
        }

        @Override
        protected ArrayList<Video> doInBackground(Integer... values) {
            String key = getString(R.string.API_KEY);
            HttpURLConnection urlConnection = null;
            ArrayList<Video> videos = new ArrayList<>();
            try {
                int id = values[0];
                URL url = new URL("http://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+key);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                Reader reader = new InputStreamReader(in);

                JsonElement jsonElement = new JsonParser().parse(reader);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                Type listType = new TypeToken<List<Video>>(){}.getType();
                ArrayList<Video> results = gson.fromJson(jsonObject.get("results"), listType);

                videos.addAll(results);
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> videos) {
            //movieAdapter.notifyDataSetChanged();
            if(videos != null)
            {
                for(final Video video: videos)
                {
                    Button button = new Button(getContext());
                    button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    button.setText(video.getName());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+video.getKey())));
                        }
                    });
                    rootView.addView(button);
                }
            }
        }
    }
}
