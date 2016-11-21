package com.antonioejemplo.casarozas;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ActivityFoto extends AppCompatActivity {

    private ImageView fotogrande;

    //Para detectar el gesto del pellizco en el imageview
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector detectarPellizco;//Para gestos como el pellizco
    private GestureDetector detectorOtrosGestos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);


        fotogrande = (ImageView) findViewById(R.id.fotogrande);

        Bundle bundle = getIntent().getExtras();
        //Se recoge un entero y al Editext hay que pasarle un string en el método setText. Es necesario el String.valueof para que no dé error.
        String idrecogido = String.valueOf(bundle.getInt("Id"));
        String imagen = bundle.getString("Imagen");
        //Descargamos la imagen con Glide:
        Glide.with(this)
                .load("http://petty.hol.es/CasaRozas/" + imagen)//Desde dónde cargamos las imágenes
                //.placeholder(R.drawable.image_susti)//Imagen de sustitución mientras carga la imagen final. Contiene transición fade.
                .error(R.drawable.image_susti)//Imagen de sustitución si se ha producido error de carga
                //.override(600,400)//Tamaño aplicado a la imagen. Tamaño en px. cuidado con los tamaños de las pantallas de los dispositivos.
                .centerCrop()//Escalado de imagen para llenar siempre los límites establecidos en diseño
                //.skipMemoryCache(true)//Omitiría la memoria caché. Por defecto está activada.
                //.diskCacheStrategy(DiskCacheStrategy.ALL)//Gestión de la caché de disco.
                .into(fotogrande);//dónde vamos a mostrar las imágenes

        //Para ampliar con pellizco
        detectarPellizco = new ScaleGestureDetector(this, new ScaleListener());

        ListenerGestos lg = new ListenerGestos();
        detectorOtrosGestos = new GestureDetector(lg);
        detectorOtrosGestos.setOnDoubleTapListener(lg);


    }


    //Se debe  este método para que funcionen todos los gestos,,,
    public boolean onTouchEvent(MotionEvent ev) {
        detectarPellizco.onTouchEvent(ev);
        detectorOtrosGestos.onTouchEvent(ev);

        return true;
    }


    //Clase interna para gestionar el pellizco.
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {//Pellizco
            scale *= detector.getScaleFactor();
            scale = Math.max(1.0f, Math.min(scale, 5.0f));//No permitimos disminuir el tamaño original(1.0f)

            matrix.setScale(scale, scale);
            fotogrande.setImageMatrix(matrix);
            return true;
        }

    }


    class ListenerGestos extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // Tratar el evento

            Toast.makeText(ActivityFoto.this, "Doble click", Toast.LENGTH_SHORT).show();
            return true;
        }



    }

}