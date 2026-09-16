package com.madrigalsolu.gestion502.Mis_Datos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madrigalsolu.gestion502.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class EditarFotoDatosActivity extends AppCompatActivity {

    private ImageView ivfotoactualizarEF;
    private Button btnelegirimagen, btnactualizarEF;

    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;

    private Bitmap bitmapSeleccionado = null;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_foto_datos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        ivfotoactualizarEF = findViewById(R.id.ivfotoactualizarEF);
        btnelegirimagen = findViewById(R.id.btnelegirimagen);
        btnactualizarEF = findViewById(R.id.btnactualizarEF);

        registrarLaunchers();
        cargarFotoAnteriorBase64();

        btnelegirimagen.setOnClickListener(v -> mostrarDialogoElegir());
        btnactualizarEF.setOnClickListener(v -> actualizarFotoBase64());
    }

    private void cargarFotoAnteriorBase64() {
        if (firebaseUser != null) {
            usuariosRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imagenBase64 = "" + snapshot.child("imagen").getValue();
                        if (imagenBase64.equals("null") || imagenBase64.isEmpty()) {
                            imagenBase64 = "" + snapshot.child("imagen_usario").getValue();
                        }
                        if (!imagenBase64.equals("null") && !imagenBase64.isEmpty()) {
                            try {
                                byte[] bytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                if (bitmap != null) {
                                    aplicarImagenCircular(ivfotoactualizarEF, bitmap);
                                    bitmapSeleccionado = bitmap;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditarFotoDatosActivity.this, "Error al cargar foto previa", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void registrarLaunchers() {
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                InputStream is = getContentResolver().openInputStream(uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                if (bitmap != null) {
                                    Bitmap cuadrado = recortarCuadrado(bitmap);
                                    bitmapSeleccionado = escalarBitmap(cuadrado, 500);
                                    aplicarImagenCircular(ivfotoactualizarEF, bitmapSeleccionado);
                                }
                            } catch (Exception e) {
                                Toast.makeText(EditarFotoDatosActivity.this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap bitmap = (Bitmap) extras.get("data");
                            if (bitmap != null) {
                                Bitmap cuadrado = recortarCuadrado(bitmap);
                                bitmapSeleccionado = escalarBitmap(cuadrado, 500);
                                aplicarImagenCircular(ivfotoactualizarEF, bitmapSeleccionado);
                            }
                        }
                    }
                }
        );
    }

    private void mostrarDialogoElegir() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogo_foto_datos);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnGaleria = dialog.findViewById(R.id.btnelegirgaleriDiag);
        Button btnCamara = dialog.findViewById(R.id.btncamaraDiag);
        Button btnCancelar = dialog.findViewById(R.id.btncancelarDiag);

        btnGaleria.setOnClickListener(v -> {
            dialog.dismiss();
            abrirGaleria();
        });

        btnCamara.setOnClickListener(v -> {
            dialog.dismiss();
            abrirCamara();
        });

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galeriaLauncher.launch(intent);
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraLauncher.launch(intent);
    }

    private void actualizarFotoBase64() {
        if (firebaseUser == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bitmapSeleccionado == null) {
            Toast.makeText(this, "Por favor, elige o toma una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Actualizando foto de perfil...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapSeleccionado.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String fotoBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imagen", fotoBase64);
        hashMap.put("imagen_usario", fotoBase64);

        usuariosRef.child(firebaseUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditarFotoDatosActivity.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditarFotoDatosActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private Bitmap recortarCuadrado(Bitmap bitmap) {
        if (bitmap == null) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newEdge = Math.min(width, height);
        int xOffset = (width - newEdge) / 2;
        int yOffset = (height - newEdge) / 2;
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, newEdge, newEdge);
    }

    private Bitmap escalarBitmap(Bitmap bitmap, int maxDimension) {
        if (bitmap == null) return null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        if (originalWidth <= maxDimension && originalHeight <= maxDimension) {
            return bitmap;
        }
        float ratio = (float) originalWidth / (float) originalHeight;
        int newWidth;
        int newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxDimension;
            newHeight = Math.round(maxDimension / ratio);
        } else {
            newHeight = maxDimension;
            newWidth = Math.round(maxDimension * ratio);
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private void aplicarImagenCircular(ImageView imageView, Bitmap bitmap) {
        if (imageView != null && bitmap != null) {
            RoundedBitmapDrawable circularDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            circularDrawable.setCircular(true);
            imageView.setImageDrawable(circularDrawable);
        }
    }
}