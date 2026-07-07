package com.example.punintended;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView textCategory, textSetup, textPunchLine;
    LinearLayout progressBarLayout;
    MaterialButton buttonNewJoke;

    private static final String API_URL = "https://official-joke-api.appspot.com/random_joke";

    private void initViews() {
        textCategory = findViewById(R.id.textCategory);
        textSetup = findViewById(R.id.textSetup);
        textPunchLine = findViewById(R.id.textPunchline);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        buttonNewJoke = findViewById(R.id.buttonNewJoke);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        buttonNewJoke.setOnClickListener(v -> fetchJoke());
    }

    private void fetchJoke() {
        progressBarLayout.setVisibility(View.VISIBLE);
        buttonNewJoke.setEnabled(false);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                response -> {
                    progressBarLayout.setVisibility(View.GONE);
                    buttonNewJoke.setEnabled(true);
                    try {
                        String type = response.getString("type");
                        String setup = response.getString("setup");
                        String punchline = response.getString("punchline");

                        textCategory.setText(type);
                        textSetup.setText(setup);
                        textPunchLine.setText(punchline);
                    } catch (JSONException e) {
                        Log.e("Parsing Error", Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(MainActivity.this, "Failed to parse joke.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBarLayout.setVisibility(View.GONE);
                    buttonNewJoke.setEnabled(true);
                    Log.e("Volley Error Message", error.toString());
                    Toast.makeText(MainActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(request);
    }
}