package com.antonioejemplo.casarozas;

import android.content.pm.ActivityInfo;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import modelos.CasaRozas;
import utilidades.Conexiones;

public class ActivityFoto extends AppCompatActivity {

    private static final String DEBUG_TAG = "GESTOS";
    private ImageView fotogrande;

    //Para detectar el gesto del pellizco en el imageview
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private ScaleGestureDetector detectarPellizco;//Para gestos como el pellizco
    private GestureDetector detectorOtrosGestos;
    private boolean dobleclick=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fotogrande = (ImageView) findViewById(R.id.fotogrande);

        Bundle bundle = getIntent().getExtras();
        //Se recoge un entero y al Editext hay que pasarle un string en el método setText. Es necesario el String.valueof para que no dé error.
        //String idrecogido = String.valueOf(bundle.getInt("Id"));
        //String imagen = bundle.getString("Imagen");
        String traerFotografias=Conexiones.TRAER_IMAGENES_OON_GLIDE;

        CasaRozas casaRozas=null;
        //int idrecogido=0 ;
        String imagen = null;
        if(bundle!=null){
            casaRozas= (CasaRozas) bundle.getSerializable("Objeto_CasaRozas");
            int idrecogido = casaRozas.getId();
            imagen = casaRozas.getImagen();


        }



        //Descargamos la imagen con Glide:
        Glide.with(this)
                .load(traerFotografias + imagen)//Desde dónde cargamos las imágenes
                //.load("http://petylde.esy.es/CasaRozas/" + imagen)//Desde dónde cargamos las imágenes
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

        @Override // Doble click
        public boolean onDoubleTap(MotionEvent e) {


            Toast.makeText(ActivityFoto.this, "Doble click", Toast.LENGTH_SHORT).show();


            if(!dobleclick) {
                scale *= Math.max(2.0f, Math.min(scale, 5.0f));
                matrix.setScale(scale, scale);
                dobleclick=true;
            }
            else{
                //scale *= Math.max(-2.0f, Math.min(scale, 5.0f));//No permitimos disminuir el tamaño original(1.0f)
                matrix.setScale(1.0f, 1.0f);
                dobleclick=false;
            }
            //matrix.setScale(scale, scale);
            fotogrande.setImageMatrix(matrix);


            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Toast.makeText(ActivityFoto.this, "Pulsación prolongada", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Toast.makeText(ActivityFoto.this, "Evento scroll", Toast.LENGTH_SHORT).show();
            Log.v(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
          /*  if (e1.getY() < e2.getY()){
                Log.v("Gesture ", " Scroll Down");
                matrix.setTranslate(2.0f, 1.0f);
            }
            if(e1.getY() > e2.getY()){
                Log.v("Gesture ", " Scroll Up");
                matrix.setTranslate(1.0f, 2.0f);
            }*/

            //Matrix matrixscroll = new Matrix();
            //float p[] = new float[9];
            //matrixscroll.getValues(p);
            if(e1.getY() > e2.getY()){
                Log.v("Gesture ", " Scroll Up");
                //matrix.setScale(1.0f, 2.0f);//Amplia la imagen
                //matrix.setSkew(1.0f, 2.0f);//REcorta la imagen//Crea una porción de la imagen
                //matrix.postTranslate(-(fotogrande.getMaxWidth() - (2.0f / 2)) * scale, 0);
                //matrix.postTranslate(0, -(fotogrande.getMaxHeight() - (2.0f / 2)) * scale);
                //matrix.setTranslate(1.0f, 2.0f);
                //matrix.mapPoints(p);

            }
            if (e1.getY() < e2.getY()){
                Log.v("Gesture ", " Scroll Down");
                //matrix.setTranslate(2.0f, 1.0f);
                //matrix.setSkew(2.0f, 1.0f);
            }

            fotogrande.setImageMatrix(matrix);
            //return super.onScroll(e1, e2, distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Toast.makeText(ActivityFoto.this, "Evento onFling", Toast.LENGTH_SHORT).show();
            //return super.onFling(e1, e2, velocityX, velocityY);
            return true;
        }
    }

}