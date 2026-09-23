package com.madrigalsolu.gestion502.ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.madrigalsolu.gestion502.R;

public class ViewHolderCliente extends RecyclerView.ViewHolder {
    View mview;
    private ViewHolderCliente.clicklistener mclicklistener;
    public interface clicklistener{
        void onItemClick(View view, int position);
        void onItemLonClick(View view, int position);
    }
    public void setMclicklistener(ViewHolderCliente.clicklistener clicklistener){
        mclicklistener=clicklistener;

    };
    public ViewHolderCliente(@NonNull View itemView) {
        super(itemView);
        mview=itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mclicklistener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mclicklistener.onItemClick(view, getAdapterPosition());
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public  boolean onLongClick(View view){
                if (mclicklistener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    mclicklistener.onItemLonClick(view, getAdapterPosition());
                    return true;
                }
                return false;
            }
        });

    }
    public void setearDatosCliente(Context context, String id_cliente, String uid_cliente, String nombres,
                                   String apellidos, String correo, String telefono, String dni, String direccion){
        ImageView ivclientefotoI;
        TextView tvidclienteI, tvuidclienteI, tvnombreI, tvapellidosI, tvcorreoI, tvdniI, tvtelefonoI, tvdireccionI;

        ivclientefotoI=mview.findViewById(R.id.ivclientefotoI);
        tvidclienteI=mview.findViewById(R.id.tvidclienteI);
        tvuidclienteI=mview.findViewById(R.id.tvuidclienteI);
        tvnombreI=mview.findViewById(R.id.tvnombresI);
        tvapellidosI=mview.findViewById(R.id.tvapellidosI);
        tvcorreoI=mview.findViewById(R.id.tvcorreoI);
        tvdniI=mview.findViewById(R.id.tvdniI);
        tvtelefonoI=mview.findViewById(R.id.tvtelefonoI);
        tvdireccionI=mview.findViewById(R.id.tvdireccionI);

        tvidclienteI.setText(id_cliente);
        tvuidclienteI.setText(uid_cliente);
        tvnombreI.setText(nombres);
        tvapellidosI.setText(apellidos);
        tvcorreoI.setText(correo);
        tvdniI.setText(dni);
        tvtelefonoI.setText(telefono);
        tvdireccionI.setText(direccion);

    }

}
