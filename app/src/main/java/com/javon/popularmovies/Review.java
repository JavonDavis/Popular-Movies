package com.javon.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * @author Javon Davis
 *         Created by Javon Davis on 22/04/16.
 */
public class Review implements Parcelable {

    String author;
    String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}