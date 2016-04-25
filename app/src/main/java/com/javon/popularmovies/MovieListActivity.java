package com.javon.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

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
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements MovieDetailFragment.OnFavoritesUpdatedListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;
    private boolean update = true;
    @Bind(R.id.movie_list) GridView homeGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);

        assert homeGrid != null;
        homeGrid.setAdapter(movieAdapter);

        homeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = movies.get(i);
                movieClicked(movie);
            }
        });

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        new UpdateMovieTask().execute();
        update = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            new UpdateMovieTask().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    private void checkIfLocal()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MovieListActivity.this);

        String order = sharedPref.getString("order",getString(R.string.order_popular));

        if(order.equals(getString(R.string.order_favorites)) && update)
            new UpdateMovieTask().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfLocal();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                startActivityForResult(settingsIntent,1);
                break;
        }
        return true;
    }

    private void movieClicked(@NonNull Movie movie)
    {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(Movie.KEY, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            update = true;
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(Movie.KEY, movie);

            startActivity(intent);
        }
    }

    @Override
    public void onFavoritesUpdated() {
        checkIfLocal();
    }

    private class UpdateMovieTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            String key = getString(R.string.API_KEY);
            HttpURLConnection urlConnection = null;
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MovieListActivity.this);

                String order = getString(R.string.order_popular);
                if(sharedPref.contains("order"))
                    order = sharedPref.getString("order",getString(R.string.order_popular));
                else
                {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("order", order);
                    editor.apply();
                }

                if(!order.equals(getString(R.string.order_favorites))) {

                    URL url = new URL("http://api.themoviedb.org/3" + order + "?api_key=" + key);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    Reader reader = new InputStreamReader(in);

                    JsonElement jsonElement = new JsonParser().parse(reader);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    GsonBuilder gsonBuilder = new GsonBuilder();//.excludeFieldsWithModifiers(Modifier.PRIVATE);
                    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                    Gson gson = gsonBuilder.create();

                    Type listType = new TypeToken<List<Movie>>() {
                    }.getType();
                    movies.clear();

                    //JsonArray jsonArray = jsonObject.getAsJsonArray("results");
                    ArrayList<Movie> results = gson.fromJson(jsonObject.get("results"), listType);

                    movies.addAll(results);
                }
                else
                {
                    movies.clear();
                    List<Movie> results = Movie.listAll(Movie.class);
                    movies.addAll(results);
                }
//                for(int i =0; i< jsonArray.size(); i++)
//                {
//                    JsonObject object = (JsonObject) jsonArray.get(i);
//                    movies.get(i).setMovieId(object.getAsJsonPrimitive("id").getAsInt());
//                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            movieAdapter.notifyDataSetChanged();
        }
    }
}
