package com.emin.hardroad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StartGameActivity extends AppCompatActivity {
    ImageView imgVibration;
    TextView txtLanguage;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_game);

        imgVibration = findViewById(R.id.imgVibration);
        TextView txtScore = findViewById(R.id.txtScore);
        txtLanguage = findViewById(R.id.txtLanguage);
        btnStart = findViewById(R.id.btnStart);
        Database db = new Database(this);

        txtScore.setText(String.valueOf(db.getScore()));


        if (db.getVibration() == 0) {
            imgVibration.setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_off));
        }

        txtLanguage.setText(db.getLanguage().equals("en") ? "EN" : "TR");
        btnStart.setText(db.getLanguage().equals("en") ? "START" : "BAŞLA");
    }

    public void start(View view) {
        startActivity(new Intent(this, GameActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void share(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.emin.hardroad");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void vibration(View view) {
        Database db = new Database(this);
        if (db.getVibration() == 1) {
            db.updateVibration(0);
            imgVibration.setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_off));
        } else {
            db.updateVibration(1);
            imgVibration.setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration));
        }
    }

    public void language(View view) {
        Database db = new Database(this);
        if (db.getLanguage().equals("en")) {
            db.updateLanguage("tr");
            txtLanguage.setText("TR");
            btnStart.setText("BAŞLA");
        } else {
            db.updateLanguage("en");
            txtLanguage.setText("EN");
            btnStart.setText("START");
        }
    }
}