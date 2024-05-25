package com.example.memorygamebaseball;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThirdLevelActivity extends AppCompatActivity {
    private static final long GAME_DURATION = 45000; // Duración del juego en milisegundos (45 segundos)
    private static final long TICK_INTERVAL = 1000; // Intervalo de actualización del contador en milisegundos (1 segundo)
    private static final int TOTAL_PAIRS = 6; // Número total de parejas en el juego
    private ImageView lastFlippedCard;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private int pairsFound = 0; // Contador de parejas encontradas
    private boolean canFlipCards = true; // Flag para controlar si se pueden voltear tarjetas
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = GAME_DURATION; // Tiempo restante del temporizador

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_level);

        Toolbar toolbar = findViewById(R.id.toolbar); // Obtener la referencia de la barra de herramientas
        setSupportActionBar(toolbar); // Establecer la barra de herramientas como la barra de acciones

        String playerName = getIntent().getStringExtra("JUGADOR");

        // Obtener las ImageView de las cartas
        List<ImageView> cardViews = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            int resId = getResources().getIdentifier("card" + i, "id", getPackageName());
            cardViews.add(findViewById(resId));
        }

        // Lista de recursos de imágenes aleatorias para el tercer nivel
        List<Integer> imageResources = new ArrayList<>();
        imageResources.add(R.drawable.aron_judge);
        imageResources.add(R.drawable.felix_hernandez);
        imageResources.add(R.drawable.bryce_harper);
        imageResources.add(R.drawable.ronald_acunia_jr);
        imageResources.add(R.drawable.jose_altuve);
        imageResources.add(R.drawable.barry_bonds);

        // Inicializar las cartas
        initializeCards(cardViews, imageResources);

        // Inicializar la barra de progreso y comenzar el temporizador
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax((int) GAME_DURATION);
        progressBar.setProgress((int) GAME_DURATION);
        startTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Obtener el ítem del menú de pausa
        MenuItem pauseResumeItem = menu.findItem(R.id.action_pause_resume);

        // Establecer el texto y el ícono del menú de pausa según el estado del juego
        if (isTimerRunning) {
            pauseResumeItem.setIcon(R.drawable.ic_pause); // Establecer el ícono del menú a pause
            pauseResumeItem.setTitle("Pausa"); // Establecer el texto del menú a "Pausa"
        } else {
            pauseResumeItem.setIcon(R.drawable.ic_play); // Establecer el ícono del menú a play
            pauseResumeItem.setTitle("Reanudar"); // Establecer el texto del menú a "Reanudar"
        }

        // Obtener el ítem del menú para el jugador
        MenuItem playerItem = menu.findItem(R.id.action_player);

        // Obtener el nombre del jugador
        String playerName = getIntent().getStringExtra("JUGADOR");

        // Establecer el nombre del jugador como título del ítem del menú
        playerItem.setTitle(playerName);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.contacto) {
            // Abrir la actividad de contacto
            Intent intent = new Intent(ThirdLevelActivity.this, ContactActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_exit) {
            finishAffinity(); // Finaliza la actividad actual (cierra la aplicación)
            return true;
        } else if (id == R.id.action_pause_resume) {
            if (isTimerRunning) {
                pauseGame();
                item.setIcon(R.drawable.ic_play); // Cambiar el ícono del menú a play
                item.setTitle("Reanudar"); // Cambiar el texto del menú a "Reanudar"
            } else {
                resumeGame();
                item.setIcon(R.drawable.ic_pause); // Cambiar el ícono del menú a pause
                item.setTitle("Pausa"); // Cambiar el texto del menú a "Pausa"
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeCards(List<ImageView> cardViews, List<Integer> imageResources) {
        // Verificar si hay suficientes elementos en la lista de recursos de imágenes
        if (imageResources.size() < TOTAL_PAIRS) {
            Toast.makeText(this, "No hay suficientes imágenes disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mezclar las imágenes para que sean aleatorias
        Collections.shuffle(imageResources);

        // Seleccionar solo las primeras tres imágenes para usar en el juego
        List<Integer> selectedImages = imageResources.subList(0, TOTAL_PAIRS);

        // Duplicar las imágenes seleccionadas para emparejarlas
        List<Integer> duplicateImages = new ArrayList<>(selectedImages);
        duplicateImages.addAll(selectedImages);

        // Mezclar las imágenes duplicadas para que se coloquen de forma aleatoria en las cartas
        Collections.shuffle(duplicateImages);

        // Asignar imágenes a las ImageView
        for (int i = 0; i < cardViews.size(); i++) {
            ImageView cardView = cardViews.get(i);
            assignImage(cardView, duplicateImages.get(i));
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, TICK_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                progressBar.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                showGameOverDialog(progressBar.getProgress());
            }
        }.start();
        isTimerRunning = true;
    }

    private void pauseGame() {
        countDownTimer.cancel();
        isTimerRunning = false;
        canFlipCards = false;
    }

    private void resumeGame() {
        startTimer();
        canFlipCards = true;
    }

    private void showGameOverDialog(long timeRemaining) {
        int score = calculateScore(timeRemaining);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Has perdido!")
                .setMessage("Tu puntaje: " + score + "\n¿Quieres volver a jugar?")
                .setPositiveButton("Sí", (dialog, which) -> restartLevel())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartLevel() {
        String playerName = getIntent().getStringExtra("JUGADOR");
        if (playerName != null && !playerName.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("JUGADOR", playerName);
            startActivity(intent);
            finish();
        }
    }

    private int calculateScore(long timeRemaining) {
        return (int) (timeRemaining / 1000);
    }

    private void assignImage(ImageView imageView, int imageResource) {
        imageView.setTag(imageResource);
        imageView.setImageResource(R.drawable.card_back);
    }

    public void onCardClicked(View view) {
        if (view instanceof ImageView && canFlipCards) {
            ImageView clickedCard = (ImageView) view;
            compareCards(clickedCard);
        }
    }

    private void compareCards(ImageView clickedCard) {
        if (!canFlipCards || clickedCard == lastFlippedCard) {
            return;
        }

        int clickedCardImageResource = (int) clickedCard.getTag();
        clickedCard.setImageResource(clickedCardImageResource);

        if (lastFlippedCard != null) {
            canFlipCards = false; // Deshabilitar la interacción mientras se comparan las cartas
            int lastFlippedCardImageResource = (int) lastFlippedCard.getTag();
            if (clickedCardImageResource == lastFlippedCardImageResource) {
                pairsFound++;
                lastFlippedCard.setEnabled(false);
                clickedCard.setEnabled(false);
                lastFlippedCard = null;
                canFlipCards = true; // Habilitar la interacción después de encontrar una pareja

                if (pairsFound == TOTAL_PAIRS) {
                    countDownTimer.cancel();
                    showSuccessDialog();
                }
            } else {
                new Handler().postDelayed(() -> {
                    lastFlippedCard.setImageResource(R.drawable.card_back);
                    clickedCard.setImageResource(R.drawable.card_back);
                    lastFlippedCard = null;
                    canFlipCards = true; // Habilitar la interacción después de voltear las cartas hacia atrás
                }, 1000);
            }
        } else {
            lastFlippedCard = clickedCard;
        }
    }

    private void showSuccessDialog() {
        int score = calculateScore(progressBar.getProgress());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Felicidades!")
                .setMessage("Has completado el nivel.\nPuntaje: " + score + "\n¿Deseas pasar al siguiente nivel?")
                .setPositiveButton("Sí", (dialog, which) -> startNewLevel())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void startNewLevel() {
        String playerName = getIntent().getStringExtra("JUGADOR");
        if (playerName != null && !playerName.isEmpty()) {
            Intent intent = new Intent(this, FourthLevelActivity.class);
            intent.putExtra("JUGADOR", playerName);
            intent.putExtra("SCORE_LEVEL_1", getIntent().getIntExtra("SCORE_LEVEL_1", 0));
            intent.putExtra("SCORE_LEVEL_2", getIntent().getIntExtra("SCORE_LEVEL_2", 0));
            int level3Score = calculateScore(progressBar.getProgress());
            intent.putExtra("SCORE_LEVEL_3", level3Score);
            startActivity(intent);
            finish();
        }
    }

}
