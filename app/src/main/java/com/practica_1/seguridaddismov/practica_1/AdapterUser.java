package com.practica_1.seguridaddismov.practica_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    public void clear() {
        users.clear();
    }

    public void addAll(ArrayList<Usuario> category) {
        for (int i = 0; i < category.size(); i++) {
            users.add(category.get(i));
        }
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
            //v = LayoutInflater.from(getContext()).inflate(R.layout.list_users, parent, false);
        }

        final Usuario dir = users.get(position);

        ImageView imagenPerfil = (ImageView) v.findViewById(R.id.imageView);
        try{
            URL url = new URL(dir.getImagenPerfil());
            Bitmap bmp = new obtenerImagen().execute(url).get();
            imagenPerfil.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView nombreLista = (TextView) v.findViewById(R.id.nombreLista);
        nombreLista.setText(dir.getNombre());

        TextView registroLista = (TextView) v.findViewById(R.id.registroLista);
        registroLista.setText(dir.getFechaRegistro());

        TextView generoLista = (TextView) v.findViewById(R.id.generoLista);
        generoLista.setText(dir.getGenero());



            TextView passwordLista = (TextView) v.findViewById(R.id.passwordLista);
            TextView usuarioLista = (TextView) v.findViewById(R.id.usuarioLista);
            if(usuarioLista != null || passwordLista != null){
                usuarioLista.setText(dir.getUsuario());
                passwordLista.setText(dir.getContrasena());
            }


            ImageButton mapa = (ImageButton)v.findViewById(R.id.buttonMaps);
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



    // Esta clase se ha creado para que no de error al intentar ejecutar un proceso en backgroud en el thread principal (Acceso a Internet)
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
