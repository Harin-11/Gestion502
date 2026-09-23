package com.madrigalsolu.gestion502.Clientes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    TextView uidusuario_l;
    EditText nombrescli, apellidoscli, correocli,dnicli, telefonocli, direccioncli;
    Button btnguardarcliente;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference BD_Usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inicializarVariable();
        btnguardarcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarCliente();
            }
        });
    }

    private void inicializarVariable(){
    btnguardarcliente=findViewById(R.id.btnGuardar);
    uidusuario_l=findViewById(R.id.tvUidUsuario);
    nombrescli=findViewById(R.id.etNombresCliente);
    apellidoscli=findViewById(R.id.etApellidosCliente);
    correocli=findViewById(R.id.etcorreocli);
    dnicli=findViewById(R.id.etDni);
    telefonocli=findViewById(R.id.etTelefono);
    direccioncli=findViewById(R.id.etDireccion);
    firebaseAuth=FirebaseAuth.getInstance();
    firebaseUser=firebaseAuth.getCurrentUser();
    BD_Usuario= FirebaseDatabase.getInstance().getReference("Usuarios");
    if (firebaseUser != null) {
        String uid = firebaseUser.getUid();
        uidusuario_l.setText("UID: " + uid);
        BD_Usuario.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nombres = snapshot.child("nombres").getValue(String.class);
                String apellidos = snapshot.child("apellidos").getValue(String.class);
                String nombreCompleto = ((nombres == null ? "" : nombres) + " " +
                        (apellidos == null ? "" : apellidos)).trim();
                TextView tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
                if (!nombreCompleto.isEmpty()) {
                    tvNombreUsuario.setText("Nombre: " + nombreCompleto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    }
    private void agregarCliente(){
        if (firebaseUser == null) {
            Toast.makeText(this, "Inicia sesión para registrar un cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid=firebaseUser.getUid();
        String nombres=nombrescli.getText().toString().trim();
        String apellidos=apellidoscli.getText().toString().trim();
        String correo= correocli.getText().toString().trim();
        String dni = dnicli.getText().toString().trim();
        String telefono= telefonocli.getText().toString().trim();
        String direccion= direccioncli.getText().toString().trim();

        DatabaseReference clientes=BD_Usuario.child(uid).child("clientes");
        String id_cliente=clientes.push().getKey();
        if (id_cliente == null) {
            Toast.makeText(this, "No se pudo generar el ID del cliente", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nombres.isEmpty()){
            Cliente cliente=new Cliente(
              id_cliente,
                    uid,

                    nombres,
                    apellidos,
                    correo,
                    telefono,
                    dni,
                    direccion
            );
            clientes.child(id_cliente).setValue(cliente).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(error ->
                    Toast.makeText(this, "No se pudo registrar el cliente", Toast.LENGTH_SHORT).show());

        }else {
            Toast.makeText(this, "Complete añ menos el nombre ", Toast.LENGTH_SHORT).show();
        }
    }
}
