package com.javon.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Menu mMenu;
    private OnFavoritesUpdatedListener mCallback;

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
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFavoritesUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFavoritesUpdatedListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(Movie.findById(Movie.class,movie.getId()) != null)
        {
            inflater.inflate(R.menu.menu_detail_favorite,menu);
        }
        else
        {
            inflater.inflate(R.menu.menu_detail_unfavorite, menu);
        }

        mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private File getMovieFile()
    {
        return new File(Environment.getExternalStorageDirectory().getPath() + "/" + movie.getPosterPath());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this,rootView);

        if (movie != null) {

            File file = getMovieFile();

            if(!file.exists()) {
                Picasso.with(getContext())
                        .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                        .into(loadImageAndSave(poster, file));
            }
            else
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                poster.setImageBitmap(myBitmap);
            }

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

        switch (id)
        {
            case R.id.menu_item_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                ArrayList<Video> trailers = (ArrayList<Video>) movie.getTrailers();
                if(trailers!= null && !trailers.isEmpty()) {
                    Video trailer = trailers.get(0);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + trailer.getKey());
                    startActivity(Intent.createChooser(shareIntent,"Share with..."));
                }
                else
                {
                    Toast.makeText(getActivity(),"No Trailers to share",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menu_item_favorite:
                movie.delete();
                File file = getMovieFile();
                if(file.exists())
                    file.delete();
                mMenu.clear();
                getActivity().getMenuInflater().inflate(R.menu.menu_detail_unfavorite,mMenu);
                Toast.makeText(getActivity(),movie.getTitle()+" removed from favorites",Toast.LENGTH_LONG).show();
                mCallback.onFavoritesUpdated();
                break;
            case R.id.menu_item_unfavorite:
                movie.save();
                mMenu.clear();
                getActivity().getMenuInflater().inflate(R.menu.menu_detail_favorite,mMenu);
                Toast.makeText(getActivity(),movie.getTitle()+" added to favorites",Toast.LENGTH_LONG).show();
                mCallback.onFavoritesUpdated();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Container Activity must implement this interface
    public interface OnFavoritesUpdatedListener {
        void onFavoritesUpdated();
    }

    private static Target loadImageAndSave(final ImageView imageView, final File file){
        return new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }
}
