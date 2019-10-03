package br.sp.senac.appcmysqlandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Helper.CheckNetworkStatus;
import Helper.HttpJsonParser;

public class AddMovieActivity extends AppCompatActivity {

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_MOVIE_NAME = "movie_name";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_YEAR = "year";
    private static final String KEY_RATING = "rating";
    private static final String BASE_URL = "http://10.23.49.28/movies/";
    private static String STRING_EMPTY = "";
    private EditText movieNameEditText;
    private EditText genreEditText;
    private EditText yearEditText;
    private EditText ratingEditText;
    private String movieName;
    private String genre;
    private String year;
    private String rating;
    private Button addButton;
    private int success;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        movieNameEditText = (EditText) findViewById(R.id.txtMovieNameAdd);
        genreEditText = (EditText) findViewById(R.id.txtGenreAdd);
        yearEditText = (EditText) findViewById(R.id.txtYearAdd);
        ratingEditText = (EditText) findViewById(R.id.txtRatingAdd);
        addButton = (Button) findViewById(R.id.btnAdd);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())) {
                    addMovie();
                } else {
                    Toast.makeText(AddMovieActivity.this,
                            "Não foi possível conectar-se à Internet",
                            Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void addMovie() {
        if (!STRING_EMPTY.equals(movieNameEditText.getText().toString()) &&
                !STRING_EMPTY.equals(genreEditText.getText().toString()) &&
                !STRING_EMPTY.equals(yearEditText.getText().toString()) &&
                !STRING_EMPTY.equals(ratingEditText.getText().toString())) {

            movieName = movieNameEditText.getText().toString();
            genre = genreEditText.getText().toString();
            year = yearEditText.getText().toString();
            rating = ratingEditText.getText().toString();
            new AddMovieAsyncTask().execute();
        } else {
            Toast.makeText(AddMovieActivity.this,
                    "Um ou mais campos deixados em branco!!!",
                    Toast.LENGTH_LONG).show();

        }


    }

    private class AddMovieAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(AddMovieActivity.this);
            pDialog.setMessage("Adicionando filmes, por favor aguarde!!!...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put(KEY_MOVIE_NAME, movieName);
            httpParams.put(KEY_GENRE, genre);
            httpParams.put(KEY_YEAR, year);
            httpParams.put(KEY_RATING, rating);
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(
                    BASE_URL + "add_movie.php", "POST", httpParams);
            try {
                success = jsonObject.getInt(KEY_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (success == 1) {

                        Toast.makeText(AddMovieActivity.this,
                                "Filme adicionado", Toast.LENGTH_LONG).show();
                        Intent i = getIntent();

                        setResult(20, i);

                        finish();

                    } else {
                        Toast.makeText(AddMovieActivity.this,
                                "Ocorreu um erro ao adicionar filme",
                                Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }
}
