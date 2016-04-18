package com.javon.popularmovies;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author Javon Davis
 *         Created by Javon Davis on 18/04/16.
 */
public class VideoAdapter extends ArrayAdapter<Video> {

    public VideoAdapter(Context context, Video[] objects) {
        super(context, 0, objects);
    }


}
