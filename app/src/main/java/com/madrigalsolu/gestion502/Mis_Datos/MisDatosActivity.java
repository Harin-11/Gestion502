package com.madrigalsolu.gestion502.Mis_Datos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madrigalsolu.gestion502.DashboardActivity;
import com.madrigalsolu.gestion502.R;

import java.util.HashMap;

public class MisDatosActivity extends AppCompatActivity {

    TextView tvcorreousuario, tvcodigousuario;
    ImageView ivfotousuario;
    EditText etnombre, etapellido, etfecha_nac, etedad, ettelefono, etdomicilio, ettiktok;


    Button btnguardar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference usuarios;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVariables();
        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarDatos();
            }
        });
        ivfotousuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MisDatosActivity.this, EditarFotoDatosActivity.class));

            }
        });

    }
    private void inicializarVariables(){
        tvcodigousuario=findViewById(R.id.tvcodigoMD);
        tvcorreousuario=findViewById(R.id.tvcorreoMD);
        etnombre=findViewById(R.id.etnombresMD);
        etapellido=findViewById(R.id.etapellidosMD);
        etfecha_nac=findViewById(R.id.etfechanacMD);
        etedad=findViewById(R.id.etedadMD);
        ettelefono=findViewById(R.id.ettelefonoMD);
        etdomicilio=findViewById(R.id.etdomicilioMD);
        ettiktok=findViewById(R.id.ettikotokMD);
        btnguardar=findViewById(R.id.btnguardarMD);
        ivfotousuario=findViewById(R.id.ivfotousuarioMD);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        usuarios= FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    private void  lecturaDatos(){
        usuarios.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String uid=""+snapshot.child("uid").getValue();
                    String nombres=""+snapshot.child("nombres").getValue();
                    String apellido=""+snapshot.child("apellidos").getValue();
                    String correo=""+snapshot.child("correo").getValue();
                    tvcodigousuario.setText(uid);
                    tvcorreousuario.setText(correo);
                    etnombre.setText(nombres);
                    etapellido.setText(apellido);

                    String imagenBase64 = "" + snapshot.child("imagen").getValue();
                    if (imagenBase64.equals("null") || imagenBase64.isEmpty()) {
                        imagenBase64 = "" + snapshot.child("imagen_usario").getValue();
                    }
                    if (!imagenBase64.equals("null") && !imagenBase64.isEmpty()) {
                        try {
                            byte[] bytes = android.util.Base64.decode(imagenBase64, android.util.Base64.DEFAULT);
                            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap != null) {
                                androidx.core.graphics.drawable.RoundedBitmapDrawable circularDrawable =
                                        androidx.core.graphics.drawable.RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                circularDrawable.setCircular(true);
                                ivfotousuario.setImageDrawable(circularDrawable);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }else {
                    Toast.makeText(MisDatosActivity.this, "Esperando Datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MisDatosActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void compobarSesion(){
        if (firebaseUser!=null){
            lecturaDatos();
        }else{
            startActivity(new Intent(MisDatosActivity.this, DashboardActivity.class));
            finish();
        }
    }
    @Override
    protected void onStart(){
        compobarSesion();
        super.onStart();
    }

    private  void actualizarDatos(){
        String nombre_act=etnombre.getText().toString().trim();
        String apellido_act=etapellido.getText().toString().trim();
        String fecha_nacin_act=etfecha_nac.getText().toString().trim();
        String edad_act=etedad.getText().toString().trim();
        String telefono_act=ettelefono.getText().toString().trim();
        String domilicio_act=etdomicilio.getText().toString().trim();
        String tiktok_act=ettiktok.getText().toString().trim();

        HashMap<String, Object>datos_actualizar=new HashMap<>();
        datos_actualizar.put("nombres",nombre_act);
        datos_actualizar.put("apellidos",apellido_act);
        datos_actualizar.put("fecha_nacin", fecha_nacin_act);
        datos_actualizar.put("edad", edad_act);
        datos_actualizar.put("telefono", telefono_act);
        datos_actualizar.put("domicilio", domilicio_act);
        datos_actualizar.put("tiktok", tiktok_act);

        usuarios.child(firebaseUser.getUid()).updateChildren(datos_actualizar)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MisDatosActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MisDatosActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
