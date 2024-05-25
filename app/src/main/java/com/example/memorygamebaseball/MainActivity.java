package com.example.memorygamebaseball;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    EditText editTextNomJugador;
    Button buttonStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTextNomJugador = findViewById(R.id.editTextNomJugador);
        buttonStartGame = findViewById(R.id.buttonStartGame);

        buttonStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = editTextNomJugador.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, FirstLevelActivity.class);
                    intent.putExtra("JUGADOR", playerName); // Pasar el nombre del jugador al siguiente Activity
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.contacto) {
            // Abrir la actividad de contacto
            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_exit) {
            finishAffinity(); // Finalizar la actividad actual (cerrar la aplicación)
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pauseItem = menu.findItem(R.id.action_pause_resume);
        MenuItem playerItem = menu.findItem(R.id.action_player);
        // Si es la FirstLevelActivity, muestra el elemento de menú de pausa
        if (pauseItem != null && playerItem != null) {
            pauseItem.setVisible(false);
            playerItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}