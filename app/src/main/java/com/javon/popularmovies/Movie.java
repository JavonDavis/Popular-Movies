package com.javon.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Javon Davis
 *         Created by Javon Davis on 16/04/16.
 */
public class Movie implements Parcelable{

    static String KEY = "MOVIE";

    boolean adult;
    String backdropPath;
    int[] genreIds;
    int id;
    String originalLanguage;
    String originalTitle;
    String overview;
    String releaseDate;
    String posterPath;
    String title;
    int popularity;
    boolean video;
    float voteAverage;
    int voteCount;
    List<Video> trailers = new ArrayList<>();
    List<Review> reviews = new ArrayList<>();

    public Movie()
    {
        //Default constructor
    }

    public Movie(Parcel in)
    {
        boolean[] booleanData = new boolean[2];
        String[] stringData = new String[7];

        int genreLength = in.readInt();

        int[] intData = new int[3+genreLength];

        this.voteAverage = in.readFloat();

        in.readBooleanArray(booleanData);
        in.readStringArray(stringData);
        in.readIntArray(intData);

        this.adult = booleanData[0];
        this.video = booleanData[1];
        this.backdropPath = stringData[0];
        this.originalLanguage = stringData[1];
        this.originalTitle = stringData[2];
        this.overview = stringData[3];
        this.releaseDate = stringData[4];
        this.posterPath = stringData[5];
        this.title = stringData[6];
        this.id = intData[0];
        this.popularity = intData[1];
        this.voteCount = intData[2];

        genreIds = new int[genreLength];
        System.arraycopy(intData, 3, genreIds, 0, intData.length - 3);
        in.readTypedList(trailers,Video.CREATOR);
        in.readTypedList(reviews, Review.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        boolean[] booleanValues = new boolean[2];
        booleanValues[0] = adult;
        booleanValues[1] = video;
        String[] stringValues = new String[]{backdropPath,originalLanguage,originalTitle,overview,releaseDate,posterPath,title};
        int[] intValues = new int[3+genreIds.length];
        intValues[0] = id;
        intValues[1] = popularity;
        intValues[2] = voteCount;

        System.arraycopy(genreIds,0,intValues,3,intValues.length-3);

        parcel.writeInt(genreIds.length);
        parcel.writeFloat(voteAverage);
        parcel.writeBooleanArray(booleanValues);
        parcel.writeStringArray(stringValues);
        parcel.writeIntArray(intValues);
        parcel.writeTypedList(trailers);
        parcel.writeTypedList(reviews);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int[] getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        this.genreIds = genreIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public List<Video> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Video> trailers) {
        this.trailers = trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
