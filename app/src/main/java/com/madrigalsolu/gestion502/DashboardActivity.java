package com.madrigalsolu.gestion502;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.madrigalsolu.gestion502.Clientes.ListaClienteActivity;
import com.madrigalsolu.gestion502.Favoritos.FavoritosActivity;
import com.madrigalsolu.gestion502.Gastos.GastosActivity;
import com.madrigalsolu.gestion502.Lista_Tareas.ListaTareaActivity;
import com.madrigalsolu.gestion502.Mis_Datos.MisDatosActivity;
import com.madrigalsolu.gestion502.Tareas.TareasActivity;

public class DashboardActivity extends AppCompatActivity {
    CardView cardEmpresa, cardGastos, cardListaTareas, cardFavoritos, cardMisDatos, cardTareas;
    TextView tvBienvenida, tvUsuarioInfo, tvIdUsuario;
    ImageView ivAvatar;
    Button btnCerrarSesion, btnDesarrollador;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference Usuarios;
    Dialog dialogoDev;


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
        tvIdUsuario = findViewById(R.id.tvIdUsuario);
        ivAvatar = findViewById(R.id.ivAvatar);

        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, MisDatosActivity.class));
                }
            });
        }

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDesarrollador = findViewById(R.id.btnDesarrollador);

        cardEmpresa = findViewById(R.id.cardEmpresa);
        cardGastos = findViewById(R.id.cardGastos);
        cardFavoritos = findViewById(R.id.cardFavoritos);
        cardTareas = findViewById(R.id.cardTareas);
        cardListaTareas = findViewById(R.id.cardListaTareas);
        cardMisDatos = findViewById(R.id.cardMisDatos);
        dialogoDev=new Dialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        cardEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Clientes", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, ListaClienteActivity.class));
            }
        });

        cardGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Gastos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, GastosActivity.class));
            }
        });

        cardFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Favoritos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, FavoritosActivity.class));
            }
        });

        cardTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, TareasActivity.class));
            }
        });

        cardListaTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Lista de Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, ListaTareaActivity.class));
            }
        });

        cardMisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Mis Datos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, MisDatosActivity.class));
            }
        });

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

        if (btnDesarrollador != null) {
            btnDesarrollador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DashboardActivity.this, "Desarrollado por: Jack Tejada", Toast.LENGTH_SHORT).show();
                    desarrollador();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        comprobarSesion();
    }

    private void comprobarSesion() {
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            cargarDatos();
        } else {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }
    private void desarrollador(){
        Button btnvolverdev;
        ImageButton btntelefonodev, btnyoutubedev;

        dialogoDev.setContentView(R.layout.dialogo_developer);

        if (dialogoDev.getWindow() != null) {
            dialogoDev.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btntelefonodev = dialogoDev.findViewById(R.id.btntelefonodev);
        btnyoutubedev = dialogoDev.findViewById(R.id.btnyoutubedev);
        btnvolverdev = dialogoDev.findViewById(R.id.btnvolverdev);

        btntelefonodev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numero = "942332318";
                Uri uri = Uri.parse("tel:" + numero);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnyoutubedev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.youtube.com/watch?v=-9ZmP35jWFQ");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnvolverdev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoDev.dismiss();
            }
        });

        dialogoDev.show();
        dialogoDev.setCanceledOnTouchOutside(false);

        if (dialogoDev.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.82);
            dialogoDev.getWindow().setLayout(width, height);
        }
    }
    private void cargarDatos() {
        if (firebaseUser == null) return;

        Usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombres = "" + snapshot.child("nombres").getValue();
                    String apellidos = "" + snapshot.child("apellidos").getValue();
                    String correo = "" + snapshot.child("correo").getValue();

                    String nombreCompleto = nombres;
                    if (!apellidos.equals("null") && !apellidos.isEmpty()) {
                        nombreCompleto += " " + apellidos;
                    }

                    if (tvBienvenida != null) {
                        tvBienvenida.setText("¡Hola, " + nombreCompleto + "!");
                    }
                    if (tvUsuarioInfo != null) {
                        if (!correo.equals("null") && !correo.isEmpty()) {
                            tvUsuarioInfo.setText(correo);
                        } else {
                            tvUsuarioInfo.setText(nombreCompleto);
                        }
                    }
                    if (tvIdUsuario != null) {
                        tvIdUsuario.setText("ID: " + uid);
                    }

                    String imagenBase64 = "" + snapshot.child("imagen").getValue();
                    if (imagenBase64.equals("null") || imagenBase64.isEmpty()) {
                        imagenBase64 = "" + snapshot.child("imagen_usario").getValue();
                    }
                    if (!imagenBase64.equals("null") && !imagenBase64.isEmpty() && ivAvatar != null) {
                        try {
                            byte[] bytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap != null) {
                                RoundedBitmapDrawable circularDrawable =
                                        RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                circularDrawable.setCircular(true);
                                ivAvatar.setImageDrawable(circularDrawable);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}

