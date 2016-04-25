package com.javon.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
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

        final Movie movie = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_list_content, parent, false);

        }

        final ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

        final View finalConvertView = convertView;

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + movie.getPosterPath());

        if(!file.exists()) {
            Picasso.with(getContext())
                    .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            TextView titleView = (TextView) finalConvertView.findViewById(R.id.textView);
                            titleView.setText(movie.getTitle());
                            titleView.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                        }
                    });
        }
        else
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);
        }

        return convertView;
    }
}
