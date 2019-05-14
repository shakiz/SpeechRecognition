package app.com.speechrecognization;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.languageid.IdentifiedLanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textView,languageTXT;
    private ImageButton imageButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private FirebaseLanguageIdentification languageIdentifier;
    //private final String API_KEY="AIzaSyB2vUCUSWFWGfmw1Pp6_7p7kTNUngUECMA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //This method will be used to initialize all the attributes with xml
        init();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This method will be used when a user press this button and acts with a google speak intent
                askForUserSpeech();
            }
        });
    }

    // Here we are Showing google speech input dialog
    private void askForUserSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    // When we are back again into the main activity we need to receive speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText("Text : "+result.get(0));
                    //After getting the data or text the app will auto search through the web by the help of google
//                    Intent actionWebSearch=new Intent(Intent.ACTION_WEB_SEARCH);
//                    actionWebSearch.putExtra(SearchManager.QUERY,result.get(0));
//                    startActivity(actionWebSearch);
                    //Here we are passing the text that we got from the user and passing it with language identifier to identify the language
                    try{
                        FirebaseApp.initializeApp(this);
                        languageIdentifier= FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
                        languageIdentifier.identifyLanguage(result.get(0)).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                languageTXT.setText("Language : "+s.toUpperCase());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("MainActivity : ",e.getMessage());
                                Toast.makeText(getApplicationContext(),"Can not identify language",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    catch (Exception e){
                        Log.v("Exception : ",e.getMessage());
                    }
                }
                break;
            }
        }
    }

    private void init() {
        textView = findViewById(R.id.textXML);
        languageTXT=findViewById(R.id.languageXML);
        imageButton = findViewById(R.id.imageButton);
    }

}
