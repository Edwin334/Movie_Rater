package com.edwin.moviereviewer2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private TextClassificationClient client;
    private  Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();
    }
    protected String movieName;
    public void getMovieName(View view){
        EditText m = findViewById(R.id.movieNameInput);
        movieName = m.getText().toString();
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),
                    "Speech not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        handler.post(
                () -> {
                    client.load();
                });
    }
    @Override
    protected void onStop(){
        super.onStop();
        handler.post(
                () -> {
                    client.unload();
                });
    }
    public void classify(View view){
        handler.post(
                () -> {
                    TextView textView = findViewById(R.id.textView3);
                    String review = textView.getText().toString();
                    client.classify(review);
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && data != null) {
                    TextView textView = (TextView) findViewById(R.id.textView3);
                    ArrayList<String> results = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                    String paragraph = "";
                    for (String result : results) {
                        paragraph += result;
                    }
                    //review = paragraph;
                    textView.setText(paragraph);
                }
            }
        }
    }
}