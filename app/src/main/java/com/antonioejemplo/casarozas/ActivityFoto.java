package com.antonioejemplo.casarozas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ActivityFoto extends AppCompatActivity {

    private ImageView fotogrande;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);


        fotogrande=(ImageView)findViewById(R.id.fotogrande);

        Bundle bundle = getIntent().getExtras();
        //Se recoge un entero y al Editext hay que pasarle un string en el método setText. Es necesario el String.valueof para que no dé error.
        String idrecogido = String.valueOf(bundle.getInt("Id"));
        String imagen=bundle.getString("Imagen");
        //Descargamos la imagen con Glide:
        Glide.with(this)
                .load("http://petty.hol.es/CasaRozas/"+imagen)//Desde dónde cargamos las imágenes
                //.placeholder(R.drawable.image_susti)//Imagen de sustitución mientras carga la imagen final. Contiene transición fade.
                .error(R.drawable.image_susti)//Imagen de sustitución si se ha producido error de carga
                //.override(600,400)//Tamaño aplicado a la imagen. Tamaño en px. cuidado con los tamaños de las pantallas de los dispositivos.
                .centerCrop()//Escalado de imagen para llenar siempre los límites establecidos en diseño
                //.skipMemoryCache(true)//Omitiría la memoria caché. Por defecto está activada.
                //.diskCacheStrategy(DiskCacheStrategy.ALL)//Gestión de la caché de disco.
                .into(fotogrande);//dónde vamos a mostrar las imágenes


    }
}
