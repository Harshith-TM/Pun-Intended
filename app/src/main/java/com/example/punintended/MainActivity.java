package com.example.punintended;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
    MaterialButton buttonCopyJoke, buttonShareJoke, buttonNewJoke;
    String generatedJoke;
    boolean isJokeLoaded = false;

    private static final String API_URL = "https://official-joke-api.appspot.com/random_joke";

    private void initViews() {
        textCategory = findViewById(R.id.textCategory);
        textSetup = findViewById(R.id.textSetup);
        textPunchLine = findViewById(R.id.textPunchline);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        buttonCopyJoke = findViewById(R.id.buttonCopyJoke);
        buttonShareJoke = findViewById(R.id.buttonShareJoke);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        buttonNewJoke.setOnClickListener(v -> fetchJoke());
        buttonCopyJoke.setOnClickListener(v -> copyJoke());
        buttonShareJoke.setOnClickListener(v -> shareJoke());
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
                    buttonCopyJoke.setEnabled(true);
                    buttonShareJoke.setEnabled(true);
                    try {
                        String type = response.getString("type");
                        String setup = response.getString("setup");
                        String punchline = response.getString("punchline");

                        textCategory.setText(type);
                        textSetup.setText(setup);
                        textPunchLine.setText(punchline);

                        isJokeLoaded = true;
                    } catch (JSONException e) {
                        Log.e("Parsing Error", Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(MainActivity.this, "Failed to parse joke.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBarLayout.setVisibility(View.GONE);
                    buttonNewJoke.setEnabled(true);
                    buttonCopyJoke.setEnabled(true);
                    buttonShareJoke.setEnabled(true);
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(MainActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                    isJokeLoaded = false;
                }
        );
        queue.add(request);
    }

    private void copyJoke() {
        if (!isJokeLoaded) {
            Toast.makeText(MainActivity.this, "Generate a joke first", Toast.LENGTH_SHORT).show();
            return;
        }
        generatedJoke = textSetup.getText().toString() + "\n\n" + textPunchLine.getText().toString();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Joke", generatedJoke);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(MainActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
    }

    private void shareJoke() {
        if (!isJokeLoaded) {
            Toast.makeText(MainActivity.this, "Generate a joke first", Toast.LENGTH_SHORT).show();
            return;
        }
        generatedJoke = textSetup.getText().toString() + "\n\n" + textPunchLine.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, generatedJoke);
        startActivity(Intent.createChooser(intent, "Share Joke"));
    }
}