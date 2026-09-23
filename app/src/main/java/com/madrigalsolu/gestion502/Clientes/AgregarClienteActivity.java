package com.madrigalsolu.gestion502.Clientes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.madrigalsolu.gestion502.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import com.google.firebase.database.FirebaseDatabase;
import com.madrigalsolu.gestion502.Clases.Cliente;

public class AgregarClienteActivity extends AppCompatActivity {
    private TextInputEditText etNombres, etApellidos, etCorreo, etDni, etTelefono, etDireccion;
    private DatabaseReference clientes;
    private FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);
        usuario = FirebaseAuth.getInstance().getCurrentUser();
        etNombres = findViewById(R.id.etNombresCliente);
        etApellidos = findViewById(R.id.etApellidosCliente);
        etCorreo = findViewById(R.id.etCorreo);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        TextView tvUidUsuario = findViewById(R.id.tvUidUsuario);
        TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        if (usuario != null) {
            tvUidUsuario.setText("UID: " + usuario.getUid());
            clientes = FirebaseDatabase.getInstance().getReference("Clientes").child(usuario.getUid());
            FirebaseDatabase.getInstance().getReference("Usuarios").child(usuario.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String nombres = snapshot.child("nombres").getValue(String.class);
                            String apellidos = snapshot.child("apellidos").getValue(String.class);
                            String nombreCompleto = ((nombres == null ? "" : nombres) + " " +
                                    (apellidos == null ? "" : apellidos)).trim();
                            tvNombreUsuario.setText("Nombre: " + nombreCompleto);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarCliente();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void guardarCliente() {
        if (usuario == null || clientes == null) {
            Toast.makeText(this, "Inicia sesión para registrar un cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        if (nombres.isEmpty() || apellidos.isEmpty()) {
            Toast.makeText(this, "Ingresa nombres y apellidos", Toast.LENGTH_SHORT).show();
            return;
        }
        String idCliente = clientes.push().getKey();
        if (idCliente == null) return;
        Cliente cliente = new Cliente(idCliente, usuario.getUid(), nombres, apellidos,
                etCorreo.getText().toString().trim(), etTelefono.getText().toString().trim(),
                etDni.getText().toString().trim(), etDireccion.getText().toString().trim());
        clientes.child(idCliente).setValue(cliente).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Cliente registrado", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(error ->
                Toast.makeText(this, "No se pudo registrar el cliente", Toast.LENGTH_SHORT).show());
    }
}
