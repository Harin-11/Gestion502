package com.madrigalsolu.gestion502;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity2 extends AppCompatActivity {
    TextView txt_volverlogin;
    EditText etnombres, etapellidos, etcorreo, etpassword, etconfirpassword;
    Button btn_registrar;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String nombres = "", apellidos = "", correo = "", password = "", confirpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_volverlogin = findViewById(R.id.txt_volverlogin);
        etnombres = findViewById(R.id.editTextNombres);
        etapellidos = findViewById(R.id.editTextApellidos);
        etcorreo = findViewById(R.id.editTextCorreo);
        etpassword = findViewById(R.id.editTextPasswordRegistro);
        etconfirpassword = findViewById(R.id.editTextConfirmarPassword);
        btn_registrar = findViewById(R.id.btn_registrar);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegistroActivity2.this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        txt_volverlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
    }

    private void validarDatos() {
        nombres = etnombres.getText().toString().trim();
        apellidos = etapellidos.getText().toString().trim();
        correo = etcorreo.getText().toString().trim();
        password = etpassword.getText().toString().trim();
        confirpassword = etconfirpassword.getText().toString().trim();

        if (TextUtils.isEmpty(nombres)) {
            etnombres.setError("Ingrese nombres");
        } else if (TextUtils.isEmpty(apellidos)) {
            etapellidos.setError("Ingrese apellidos");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etcorreo.setError("Correo inválido");
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            etpassword.setError("Mínimo 8 caracteres");
        } else if (!password.equals(confirpassword)) {
            etconfirpassword.setError("Las contraseñas no coinciden");
        } else {
            registrar();
        }
    }

    private void registrar() {
        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        guardarUsuario();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String mensajeError;
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            mensajeError = "Este correo electrónico ya se encuentra registrado";
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            mensajeError = "La contraseña es muy débil. Debe tener al menos 8 caracteres";
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            mensajeError = "El formato del correo electrónico no es válido";
                        } else if (e instanceof FirebaseNetworkException) {
                            mensajeError = "Sin conexión a internet. Verifica tu red";
                        } else {
                            mensajeError = "Error al crear la cuenta. Intenta de nuevo";
                        }
                        Toast.makeText(RegistroActivity2.this, mensajeError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarUsuario() {
        progressDialog.setMessage("Guardando información...");

        String uid = firebaseAuth.getUid();
        HashMap<String, Object> datousuario = new HashMap<>();
        datousuario.put("uid", uid);
        datousuario.put("nombres", nombres);
        datousuario.put("apellidos", apellidos);
        datousuario.put("correo", correo);
        datousuario.put("password", password);
        datousuario.put("fecha_nacin", "");
        datousuario.put("telefono","");
        datousuario.put("domicilio","");
        datousuario.put("tiktok","");
        datousuario.put("imagen_usario","");


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid).setValue(datousuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity2.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistroActivity2.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity2.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}