package com.madrigalsolu.gestion502;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {
    TextView tvBienvenida, tvUsuarioInfo;
    Button btnCerrarSesion;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvUsuarioInfo = findViewById(R.id.tvUsuarioInfo);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String correo = currentUser.getEmail();
            if (correo != null && tvUsuarioInfo != null) {
                tvUsuarioInfo.setText(correo);
            }

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombres = snapshot.child("nombres").getValue(String.class);
                        String apellidos = snapshot.child("apellidos").getValue(String.class);
                        if (nombres != null && tvBienvenida != null) {
                            String nombreCompleto = nombres + (apellidos != null ? " " + apellidos : "");
                            tvBienvenida.setText("¡Hola, " + nombreCompleto + "!");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseAuth.signOut();
                    Toast.makeText(DashboardActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        Button btnDesarrollador = findViewById(R.id.btnDesarrollador);
        if (btnDesarrollador != null) {
            btnDesarrollador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DashboardActivity.this, "Desarrollado por: Jack Tejada", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}