package com.practica_1.seguridaddismov.practica_1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        }

        Usuario dir = users.get(position);

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
