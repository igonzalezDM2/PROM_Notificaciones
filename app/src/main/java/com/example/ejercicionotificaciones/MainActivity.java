package com.example.ejercicionotificaciones;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private int aciertos;
    private int op1, op2, res;

    TextView operando1, operando2, operador;
    EditText etResultado;

    Button btnComprobar;

    private NotificationManager notificationManager;
    private static final String CANAL_ID = "canal_operaciones";
    private static final int NOTIFICACION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operando1 = findViewById(R.id.tvOperando1);
        operando2 = findViewById(R.id.tvOperando2);
        operador = findViewById(R.id.tvOperador);

        etResultado = findViewById(R.id.etResultado);

        btnComprobar = findViewById(R.id.btnComprobar);

        aciertos = 0;

        prepararNotificaciones();

        generarOperacion();

        btnComprobar.setOnClickListener(v -> {
            Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            try {

                String txtRes = etResultado.getText().toString();
                int enviado = Integer.parseInt(txtRes);
                if (enviado == res) {
                    aciertos++;
                    if (aciertos == 10) {
                        lanzarNotificacion();
                        aciertos = 0;
                    } else {
                        toast.setText("ACIERTO (Aciertos: " + aciertos + ")");
                    }
                } else {
                    toast.setText("FALLO (Aciertos: " + aciertos + ")");
                }
            } catch (NumberFormatException | NullPointerException e) {
                toast.setText("ALGO SALIÓ MAL");
            } finally {
                toast.show();
                generarOperacion();
                etResultado.setText("");
            }
        });


    }

    private void prepararNotificaciones() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CANAL_ID, "Juego calcular", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Canal de avisos del juego");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void lanzarNotificacion() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(R.drawable.calculator)
                .setContentTitle("Has ganado")
                .setContentText("Has acertado 10");
        notificationManager.notify(NOTIFICACION_ID, notification.build());
    }


    private static int numeroAleatorio(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    private void generarOperacion() {
        op1 = numeroAleatorio(1, 10);
        op2 = numeroAleatorio(1, 10);

        operando1.setText(""+op1);
        operando2.setText(""+op2);


        int numOperador = numeroAleatorio(1, 3);
        switch (numOperador) {
            case 1:
                operador.setText("+");
                res = op1 + op2;
                break;
            case 2:
                operador.setText("-");
                res = op1 - op2;
                break;
            case 3:
                operador.setText("x");
                res = op1 * op2;
                break;
        }

    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast toast = Toast.makeText(this, "No se mostrarán notificaciones", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });



}