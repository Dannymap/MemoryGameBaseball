package com.example.memorygamebaseball;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.Executors;

public class ContactActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, messageEditText;
    private Spinner problemSpinner;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inicializar vistas
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        messageEditText = findViewById(R.id.messageEditText);
        problemSpinner = findViewById(R.id.problemSpinner);
        sendButton = findViewById(R.id.sendButton);

        // Configurar el spinner de problemas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problems, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemSpinner.setAdapter(adapter);

        // Manejar clic en el botón "Enviar"
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    // Método para enviar el mensaje por correo electrónico
    private void sendMessage() {
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String problem = problemSpinner.getSelectedItem().toString();
        final String message = messageEditText.getText().toString().trim();

        // Verificar si todos los campos están completos
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar correo electrónico en un hilo separado
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("dmapmemorymlb@gmail.com", "lgtrmajxuxynkwda");
                    sender.sendMail(
                            "Mensaje de " + name + " - " + problem,
                            "Nombre: " + name + "\n\nEmail: " + email + "\n\nTipo de problema: " + problem + "\n\nMensaje: " + message,
                            "dmapmemorymlb@gmail.com",
                            "dmapmemorymlb@gmail.com"
                    );

                    // Si se envía el correo correctamente, regresar a la MainActivity
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContactActivity.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Para cerrar la actividad actual
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ContactActivity.this, "Error al enviar el mensaje: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
