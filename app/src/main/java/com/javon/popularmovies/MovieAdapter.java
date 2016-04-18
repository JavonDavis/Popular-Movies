package com.javon.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Javon Davis
 *         Created by Javon Davis on 16/04/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Context context, ArrayList<Movie> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_content, parent, false);

        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

        Picasso.with(getContext())
                .load("http://image.tmdb.org/t/p/w185/"+movie.getPosterPath())
                .into(imageView);

        return imageView;
    }
}
