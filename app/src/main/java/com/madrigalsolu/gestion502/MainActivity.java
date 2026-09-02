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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextView txt_registrar;
    Button btn_ingresar;
    EditText etcorreo, etpassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txt_registrar = findViewById(R.id.txt_registrar);
        btn_ingresar = findViewById(R.id.btnIngresar);
        etcorreo = findViewById(R.id.editTextUsuario);
        etpassword = findViewById(R.id.editTextPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        txt_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistroActivity2.class));
            }
        });

        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
    }

    private void validarDatos() {
        String correo = etcorreo.getText().toString().trim();
        String password = etpassword.getText().toString().trim();

        if (TextUtils.isEmpty(correo)) {
            etcorreo.setError("Ingrese su correo");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etcorreo.setError("Ingrese un correo válido");
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            etpassword.setError("La contraseña debe tener al menos 8 caracteres");
        } else {
            loginUsuario(correo, password);
        }
    }

    private void loginUsuario(String correo, String password) {
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String mensajeError;

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            String errorCode = ((FirebaseAuthInvalidCredentialsException) e).getErrorCode();
                            if ("ERROR_WRONG_PASSWORD".equals(errorCode)) {
                                mensajeError = "Contraseña incorrecta";
                            } else if ("ERROR_INVALID_EMAIL".equals(errorCode)) {
                                mensajeError = "El formato del correo electrónico es inválido";
                            } else {
                                mensajeError = "Usuario incorrecto o contraseña incorrecta";
                            }
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();
                            if ("ERROR_USER_DISABLED".equals(errorCode)) {
                                mensajeError = "Esta cuenta ha sido inhabilitada";
                            } else {
                                mensajeError = "El usuario no existe";
                            }
                        } else if (e instanceof FirebaseNetworkException) {
                            mensajeError = "Sin conexión a internet. Verifica tu red";
                        } else if (e instanceof FirebaseAuthException) {
                            String errorCode = ((FirebaseAuthException) e).getErrorCode();
                            if ("ERROR_WRONG_PASSWORD".equals(errorCode)) {
                                mensajeError = "Contraseña incorrecta";
                            } else if ("ERROR_USER_NOT_FOUND".equals(errorCode)) {
                                mensajeError = "El usuario no existe";
                            } else if ("ERROR_USER_DISABLED".equals(errorCode)) {
                                mensajeError = "Esta cuenta ha sido inhabilitada";
                            } else if ("ERROR_INVALID_EMAIL".equals(errorCode)) {
                                mensajeError = "El formato del correo electrónico es inválido";
                            } else if ("ERROR_TOO_MANY_REQUESTS".equals(errorCode)) {
                                mensajeError = "Demasiados intentos fallidos. Intenta más tarde";
                            } else {
                                mensajeError = "Usuario incorrecto o contraseña incorrecta";
                            }
                        } else {
                            String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                            if (errorMsg.contains("invalid_login_credentials") ||
                                    errorMsg.contains("invalid credential") ||
                                    errorMsg.contains("invalid-credential") ||
                                    errorMsg.contains("credentials")) {
                                mensajeError = "Usuario incorrecto o contraseña incorrecta";
                            } else if (errorMsg.contains("network")) {
                                mensajeError = "Sin conexión a internet. Verifica tu red";
                            } else {
                                mensajeError = "Usuario incorrecto o contraseña incorrecta";
                            }
                        }

                        Toast.makeText(MainActivity.this, mensajeError, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}