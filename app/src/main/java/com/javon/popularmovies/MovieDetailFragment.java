package com.javon.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        setHasOptionsMenu(true);

        if (getArguments().containsKey(Movie.KEY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
             movie = getArguments().getParcelable(Movie.KEY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        super.onCreateOptionsMenu(menu, inflater);
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
        new LoadMovieDataTask(getActivity(),root).execute(movie);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_item_share)
        {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            ArrayList<Video> trailers = (ArrayList<Video>) movie.getTrailers();
            if(trailers!= null && !trailers.isEmpty()) {
                Video trailer = trailers.get(0);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailer.getKey());
                startActivity(Intent.createChooser(shareIntent,"Share with..."));
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
