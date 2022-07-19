package com.emin.hardroad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {
    ImageView imgVibration;
    TextView txtLanguage, txtBestScore;
    Button btnNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_end_game);
        int score = getIntent().getIntExtra("score", 0);
        TextView txtScore = findViewById(R.id.txtScore);
        txtBestScore = findViewById(R.id.txtBestScore);
        txtLanguage = findViewById(R.id.txtLanguage);
        imgVibration = findViewById(R.id.imgVibration);
        btnNewGame = findViewById(R.id.btnNewGame);
        Database db = new Database(this);

        txtScore.setText(String.valueOf(score));

        if (score > db.getScore()) {
            db.updateScore(score);
            txtBestScore.setVisibility(View.VISIBLE);
        }

        if (db.getVibration() == 0) {
            imgVibration.setImageDrawable(getResources().getDrawable(R.drawable.ic_vibration_off));
        }

        txtLanguage.setText(db.getLanguage().equals("en") ? "EN" : "TR");
        btnNewGame.setText(db.getLanguage().equals("en") ? "NEW GAME" : "YENİ OYUN");
        txtBestScore.setText(db.getLanguage().equals("en") ? "BEST SCORE" : "YENİ REKOR");
    }

    public void newGame(View view) {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StartGameActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void language(View view) {
        Database db = new Database(this);
        if (db.getLanguage().equals("en")) {
            db.updateLanguage("tr");
            txtLanguage.setText("TR");
            btnNewGame.setText("YENİ OYUN");
            txtBestScore.setText("YENİ REKOR");
        } else {
            db.updateLanguage("en");
            txtLanguage.setText("EN");
            btnNewGame.setText("NEW GAME");
            txtBestScore.setText("BEST SCORE");
        }
    }
}