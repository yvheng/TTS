package com.example.taruc.tts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent; import android.media.AudioManager; import android.os.Build; import android.os.Bundle; import android.speech.tts.TextToSpeech; import android.speech.tts.UtteranceProgressListener; import android.support.v7.app.AppCompatActivity; import android.util.Log; import android.view.View; import android.widget.Button;import android.widget.EditText; import android.widget.TextView; import android.widget.Toast;

import java.util.HashMap; import java.util.Locale;
public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
            TextToSpeech.OnInitListener{

    private final int CHECK_CODE = 0x1;
    private TextToSpeech tts;
    private UtteranceProgressListener utteranceProgressListener;
    private Button buttonSpeak;
    private EditText editTextInput;
    private TextView textViewOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSpeak = (Button)findViewById(R.id.buttonSpeak);
        buttonSpeak.setOnClickListener(this);

        editTextInput = (EditText)findViewById(R.id.editTextInput);
        textViewOutput = (TextView)findViewById(R.id.textViewOutput);

        checkTTS();
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            // Change this to match your locale
            tts.setLanguage(new Locale("yue","HK"));
            utteranceProgressListener = new UtteranceProgressListener() {
                @Override
                public void onStart(final String utteranceId) {
                    //Display progress of uttering
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewOutput.setText(utteranceId);
                        }
                    });
                }

                @Override
                public void onDone(String utteranceId) {

                }

                @Override
                public void onError(String utteranceId) {

                }
            };
            tts.setOnUtteranceProgressListener(utteranceProgressListener);
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.buttonSpeak){
            String stringInput;

            stringInput = editTextInput.getText().toString();

            if(tts.isSpeaking()){
                Toast.makeText(this, "Don't press so fast leh chill abit cannot meh", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String word:stringInput.split(" ")) {
                    tts.speak(word, TextToSpeech.QUEUE_ADD, null, word);
                }
            }
            else {
                HashMap<String, String> hash = new HashMap<String,String>();
                hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                        String.valueOf(AudioManager.STREAM_NOTIFICATION));
                for (String word:stringInput.split(" ")) {
                    tts.speak(word, TextToSpeech.QUEUE_ADD, hash);
                }
            }
        }
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //success
                tts = new TextToSpeech(this, this);
            }
        }else {
                //failed. install voice data
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop(); //interrupts the current utterance
            tts.shutdown(); //releases the resources
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
