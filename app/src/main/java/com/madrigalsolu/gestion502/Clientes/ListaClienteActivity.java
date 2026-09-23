package com.madrigalsolu.gestion502.Clientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.madrigalsolu.gestion502.Clases.Cliente;
import com.madrigalsolu.gestion502.R;
import com.madrigalsolu.gestion502.ViewHolder.ViewHolderCliente;

public class ListaClienteActivity extends AppCompatActivity {
    RecyclerView recyclerViewClientes;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BD_usuarios;
    FirebaseRecyclerAdapter<Cliente, ViewHolderCliente> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Cliente>firebaseRecyclerOptions;
    FloatingActionButton btnagregarcliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cliente);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewClientes=findViewById(R.id.recylerviewClientes);
        recyclerViewClientes.setHasFixedSize(true);
        firebaseDatabase=FirebaseDatabase.getInstance();
        BD_usuarios=firebaseDatabase.getReference("Usuarios");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            listarClientes();
        } else {
            Toast.makeText(this, "Inicia sesión para ver tus clientes", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnagregarcliente=findViewById(R.id.btnagregarcliente);
        btnagregarcliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListaClienteActivity.this, "Hola Jovenes Bienvenidos ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ListaClienteActivity.this, AgregarClienteActivity.class));
            }
        });
    }
    private void listarClientes(){

        Query query=BD_usuarios.child(firebaseUser.getUid()).child("clientes").orderByChild("nombres");
        firebaseRecyclerOptions=new FirebaseRecyclerOptions.Builder<Cliente>().setQuery(query,Cliente.class).build();
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Cliente, ViewHolderCliente>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCliente viewHolderCliente, int i, @NonNull Cliente cliente) {
                viewHolderCliente.setearDatosCliente(
                        getApplicationContext(),
                        cliente.getId_cliente(),
                        cliente.getUid_cliente(),
                        cliente.getNombres(),
                        cliente.getApellidos(),
                        cliente.getCorreo(),
                        cliente.getTelefono(),
                        cliente.getDni(),
                        cliente.getDireccion()
                );
            }

            @NonNull
            @Override
            public ViewHolderCliente onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
                ViewHolderCliente viewHolderCliente=new ViewHolderCliente(view);
                viewHolderCliente.setMclicklistener(new ViewHolderCliente.clicklistener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ListaClienteActivity.this, "on Item click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLonClick(View view, int position) {
                        Toast.makeText(ListaClienteActivity.this, "on Item long click", Toast.LENGTH_SHORT).show();
                    }
                });
                return viewHolderCliente;
            }
        };
        recyclerViewClientes.setLayoutManager(new GridLayoutManager(ListaClienteActivity.this,2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewClientes.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }
}
