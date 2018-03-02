package com.usersdb.sdm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.usersdb.sdm.R;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rafa on 08/02/2018.
 */

public class AdapterUser extends BaseAdapter {
    protected Activity activity;
    protected ArrayList<Usuario> users;


    public AdapterUser (Activity activity, ArrayList<Usuario> users){
        this.activity = activity;
        this.users = users;

    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int arg0) {
        return users.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.fila_lista, null);
        }

        // Se accede al usuario que se va a cargar en la lista
        final Usuario dir = users.get(position);

        // Se carga la imagen de perfil del usuario
        ImageView imagenPerfil = v.findViewById(R.id.imageView);
        try{
            URL url = new URL(dir.getImagenPerfil());
            Bitmap bmp = new obtenerImagen().execute(url).get();
            imagenPerfil.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Se carga el nombre, fecha de registro y genero del usuario
        TextView nombreLista = v.findViewById(R.id.nombreLista);
        nombreLista.setText(dir.getNombre());
        TextView registroLista = v.findViewById(R.id.registroLista);
        registroLista.setText(dir.getFechaRegistro());
        TextView generoLista = v.findViewById(R.id.generoLista);
        generoLista.setText(dir.getGenero());

        // Se carga la contraseña y el usuario si existe el campo en el XML (Modo Landscape)
        TextView passwordLista = v.findViewById(R.id.passwordLista);
        TextView usuarioLista = v.findViewById(R.id.usuarioLista);
        if(usuarioLista != null || passwordLista != null){
            usuarioLista.setText(dir.getUsuario());
            passwordLista.setText(dir.getContrasena());
        }

        // Se asigna a un boton la calle del usuario si existe dicho boton en el XML (Modo Portrait)
        ImageButton mapa = v.findViewById(R.id.buttonMaps);
        if(mapa!=null) {
            mapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + dir.getLocalizacion());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapIntent);
                }
            });
        }

        return v;
    }


    // Esta clase se crea para descargar la imagen de perfil del usuario.
    // La conexion a Internet no puede hacerse desde el Thread principal, por eso se usa AsyncTask
    class obtenerImagen extends AsyncTask<URL, Integer, Bitmap> {
        protected Bitmap doInBackground(URL ... urls) {
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (Exception e){
                e.printStackTrace();
            }
            return bmp;
        }
    }
}
