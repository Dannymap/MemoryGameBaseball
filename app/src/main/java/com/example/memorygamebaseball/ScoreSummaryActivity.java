package com.example.memorygamebaseball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ScoreSummaryActivity extends AppCompatActivity {
    private TextView playerNameTextView;
    private TextView level1ScoreTextView;
    private TextView level2ScoreTextView;
    private TextView level3ScoreTextView;
    private TextView level4ScoreTextView;
    private TextView totalScoreTextView;
    private TextView bestPlayerTextView;
    private TextView bestScoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_summary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playerNameTextView = findViewById(R.id.playerNameTextView);
        level1ScoreTextView = findViewById(R.id.level1ScoreTextView);
        level2ScoreTextView = findViewById(R.id.level2ScoreTextView);
        level3ScoreTextView = findViewById(R.id.level3ScoreTextView);
        level4ScoreTextView = findViewById(R.id.level4ScoreTextView);
        totalScoreTextView = findViewById(R.id.totalScoreTextView);
        bestPlayerTextView = findViewById(R.id.bestPlayerTextView);
        bestScoreTextView = findViewById(R.id.bestScoreTextView);

        String playerName = getIntent().getStringExtra("JUGADOR");
        int level1Score = getIntent().getIntExtra("SCORE_LEVEL_1", 0);
        int level2Score = getIntent().getIntExtra("SCORE_LEVEL_2", 0);
        int level3Score = getIntent().getIntExtra("SCORE_LEVEL_3", 0);
        int level4Score = getIntent().getIntExtra("SCORE_LEVEL_4", 0);

        int totalScore = level1Score + level2Score + level3Score + level4Score;

        playerNameTextView.setText("Jugador: " + playerName);
        level1ScoreTextView.setText("Nivel 1: " + level1Score);
        level2ScoreTextView.setText("Nivel 2: " + level2Score);
        level3ScoreTextView.setText("Nivel 3: " + level3Score);
        level4ScoreTextView.setText("Nivel 4: " + level4Score);
        totalScoreTextView.setText("Total: " + totalScore);

        // Guardar el puntaje si es el mejor
        saveBestScore(playerName, totalScore);

        // Mostrar el mejor puntaje guardado
        displayBestScore();

        findViewById(R.id.restartButton).setOnClickListener(v -> {
            Intent intent = new Intent(ScoreSummaryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Ocultar el ítem del menú para el jugador
        MenuItem playerItem = menu.findItem(R.id.action_player);
        playerItem.setVisible(false);

        // Ocultar el botón de pausa
        MenuItem pauseItem = menu.findItem(R.id.action_pause_resume);
        pauseItem.setVisible(false);

        // Obtener el nombre del jugador
        String playerName = getIntent().getStringExtra("JUGADOR");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            finishAffinity(); // Finaliza la actividad actual (cierra la aplicación)
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para guardar el mejor puntaje
    private void saveBestScore(String playerName, int totalScore) {
        SharedPreferences sharedPreferences = getSharedPreferences("BestScorePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Obtener el mejor puntaje actual
        int bestScore = sharedPreferences.getInt("BestScore", 0);

        // Si el puntaje actual es mejor que el mejor puntaje guardado, actualizarlo
        if (totalScore > bestScore) {
            editor.putInt("BestScore", totalScore);
            editor.putString("BestPlayer", playerName);
            editor.apply();
        }
    }

    // Método para mostrar el mejor puntaje guardado
    private void displayBestScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("BestScorePrefs", Context.MODE_PRIVATE);

        // Obtener el mejor puntaje guardado
        int bestScore = sharedPreferences.getInt("BestScore", 0);
        String bestPlayer = sharedPreferences.getString("BestPlayer", "");

        // Mostrar el mejor puntaje y el nombre del jugador
        if (!bestPlayer.isEmpty()) {
            bestPlayerTextView.setText("Mejor Jugador: " + bestPlayer);
            bestScoreTextView.setText("Mejor Puntaje: " + bestScore);
        } else {
            bestPlayerTextView.setText("No hay mejor puntaje registrado");
            bestScoreTextView.setText("");
        }
    }
}