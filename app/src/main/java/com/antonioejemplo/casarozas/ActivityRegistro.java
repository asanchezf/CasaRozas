package com.antonioejemplo.casarozas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import utilidades.Conexiones;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ActivityRegistro extends AppCompatActivity {

    EditText txtLugar, txtComentario;
    Spinner spinnerAnio, spinnerMes;
    ImageView imgImagen;
    Button btnGuardar;
    private static final int TRAER_DE_GALERÍA = 100;
    private final int TRAER_DE_CAMARA = 20;
    private final int PERMISOS = 200;
    private final String NUEVA_CARPETA_RAIZ = "ImagenesAppCasaRozas/";
    private final String RUTA_IMAGEN = NUEVA_CARPETA_RAIZ + "misFotos";
    private Bitmap bitmap;
    //Parámetros para el WS:
    private String encoded_string, image_name, anio, mes, path, lugar, descripcion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        inicializarControles();


        imgImagen.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                recogerImagen();

            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lugar = txtLugar.getText().toString();
                descripcion = txtComentario.getText().toString();

                if (lugar.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(ActivityRegistro.this, "Debes rellenar el lugar y la descripción para poder guardar la imagen.", Toast.LENGTH_SHORT).show();
                    return;
                }
                subirImagen();
            }
        });
        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mes = (String) parent.getItemAtPosition(position);
                //Toast.makeText(ActivityRegistro.this, "Se ha seleccionado mes: "+parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerAnio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                anio = (String) parent.getItemAtPosition(position);
                //Toast.makeText(ActivityRegistro.this, "Se ha seleccionado año: "+parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void validarPermisos() {

        //Versión anterior a M. NO hacen falta los permisos en tiempo de ejecución
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return;
        }

        //YA TIENE LOS PERMISOS
        if((ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            recogerImagen();
        }

        //NO TIENE LOS PERMISOS. RECOMENDACIÓN Y PERMISOS
        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            recomendaciónPermisos();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }


    }

    private void recomendaciónPermisos() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(ActivityRegistro.this);
        alertDialog.setTitle("Permisos desactivados");
        alertDialog.setMessage("Debes aceptar los permisos solicitados para el correcto funcionamiento de la App");
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{CAMERA,WRITE_EXTERNAL_STORAGE},PERMISOS);

            }
        });
        alertDialog.show();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISOS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){

                recogerImagen();
            }


            else{
                solicitarPermisosManualmente();
            }
        }

    }

    private void solicitarPermisosManualmente() {
        final CharSequence[] opciones={"Si","No"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(ActivityRegistro.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados. La aplicación no funcionará correctamente.",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();

    }



    private void recogerImagen() {

        if ((ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            //Gestionamos las opciones creando un array....
            final CharSequence[] opciones = {"Hacer una fotografía", "Cargar una imagen de la galería", "Cancelar"};

            //En el AlertDialog incluimos las opciones...
            final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(ActivityRegistro.this);
            alertOpciones.setIcon(R.drawable.camera);
            alertOpciones.setTitle("Selecciona una opción...");
            alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (opciones[i].equals("Cargar una imagen de la galería")) {
                        // 1- Traer las imágenes de la Galería o de otros directorios :Intent.ACTION_PICK-----ACTION_GET_CONTENT
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        //Nombre que va a tener el archivo de imagen
                        image_name = (System.currentTimeMillis() / 1000) + ".jpg";
                        btnGuardar.setEnabled(true);
                        startActivityForResult(intent.createChooser(intent, "Selecciona una aplicación para realizar la acción"), TRAER_DE_GALERÍA);

                    } else if (opciones[i].equals("Hacer una fotografía")) {

                        hacerFotografia();
                        btnGuardar.setEnabled(true);
                    } else {
                        //btnGuardar.setEnabled(false);
                        dialogInterface.dismiss();
                    }

                }
            });

            alertOpciones.show();
            //FIN DEL ALERTDIALOG

        }//Fin if si tiene permisos

    else
        {//Todavía no tiene permisos. Le volvemos a mostrar el diálogo....
            validarPermisos();
        }
    }



    private void hacerFotografia() {

        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();
        //String nombreImagen="";
        if (isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada == true) {
            image_name = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        //Ruta de almacenamiento
        path = Environment.getExternalStorageDirectory() +
                File.separator + RUTA_IMAGEN + File.separator + image_name;

        File imagen = new File(path);

        //NECESARIO PARA DIFERENCIAR DE ANDROID 7 EN ADELANTE
//https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed&usg=ALkJrhhQhXdNXh3XREiEgvkith2kH-UVvw
        //https://www.youtube.com/watch?v=At0UmHXMMU8&index=100&list=PLAg6Lv5BbjjdvIcLQdVg4ROZnfuuQcqXB
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        //ANTES DE ANDROID 7
        else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent, TRAER_DE_CAMARA);

    }


    private void inicializarControles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Botón actividad padre
        txtLugar = (EditText) findViewById(R.id.txt_lugar);
        txtComentario = (EditText) findViewById(R.id.txtComentario);
        spinnerAnio = (Spinner) findViewById(R.id.spinnerAnio);
        spinnerMes = (Spinner) findViewById(R.id.spinnerMes);
        imgImagen = (ImageView) findViewById(R.id.imgImagen);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setEnabled(false);


        //CREANDO EL ARRAY PARA EL ADAPTADOR DEL SPINNER DESDE CÓDIGO:
       /* String itemsMeses[]=getResources().getStringArray(R.array.meses);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,itemsMeses);*/

        //CREANDO EL ARRAY PARA EL ADAPTADOR DEL SPINNER DESDE LOS RECURSOS:
        ArrayAdapter adaptadorMeses = ArrayAdapter.createFromResource(this, R.array.meses, R.layout.support_simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adaptadorMeses);


        ArrayAdapter adaptadorAnios = ArrayAdapter.createFromResource(this, R.array.anios, R.layout.support_simple_spinner_dropdown_item);
        spinnerAnio.setAdapter(adaptadorAnios);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case TRAER_DE_GALERÍA:

                    Uri miPath = data.getData();
                    imgImagen.setImageURI(miPath);

                    try {

                        //DEBEMOS REDUCIR EL TAMAÑO DE LA IMAGEN ANTES PORQUE DA ERROR DE FUERA DE MEMORIA...
                        bitmap = getThumbnail(miPath);
                        //ANTES SIN REDUCIR DABA ERROR OUT-RANGE-MEMORY
                        // bitmap = getBitmapFromUri(miPath);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;

                case TRAER_DE_CAMARA:
                    //BitmapFactory.Options creamos un BitmapFactory.Options para evitar el error out-range-memory:
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();//NUEVO
                    bitmapOptions.inJustDecodeBounds = false;//Si es true no muestra la imagen en el imageview
                    bitmapOptions.outHeight = 225;//Alto
                    bitmapOptions.outWidth = 225;//Ancho
                    bitmapOptions.inSampleSize = 20;
                    bitmapOptions.inPurgeable = true;//solo Hasta kit-kat

                    //http://gpmess.com/blog/2013/10/02/como-cargar-fotos-en-una-aplicacion-android-desde-camara-galeria-y-otras-aplicaciones/
                    /*double ratioWidth = ((float) 300) / (float) bitmapOptions.outWidth;
                    double ratioHeight = ((float) 300) / (float) bitmapOptions.outHeight;
                    double ratio = Math.min(ratioWidth, ratioHeight);
                    int dstWidth = (int) Math.round(ratio * bitmapOptions.outWidth);
                    int dstHeight = (int) Math.round(ratio * bitmapOptions.outHeight);
                    ratio = Math.floor(1.0 / ratio);
                    int sample = nearest2pow((int) ratio);
                    bitmapOptions.inJustDecodeBounds = false;
                    if (sample <= 0) {
                        sample = 1;
                    }
                    bitmapOptions.inSampleSize = (int) sample;
                    bitmapOptions.inPurgeable = true;*/

                    /*bitmapOptions.outHeight = 300;
                    bitmapOptions.outWidth = 300;*/
                    /*bitmapOptions.inSampleSize = 4; // 1/4
                    bitmapOptions.inPurgeable = true;*/

                    bitmap = BitmapFactory.decodeFile(path, bitmapOptions);


                    //bitmap= BitmapFactory.decodeFile(path);//ANTES

                    //Permitimos que la foto se guarde en la galería...
                    //Forma 1- NO funciona para kit-kat
                 /*   MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);
                                }
                            });*/

                    //Permitimos que la foto se guarde en la galería...
                    //Forma 2- funciona para kit-kat y para Lollipod
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, bitmap);


                   /* imgImagen.setImageBitmap(bitmap);//ANTES*/
                    imgImagen.setImageBitmap(bitmap);
                    //bitmap.recycle();//Nuevo

                    /*if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap = null; }*/


                    break;

            }


        }
    }


    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        //REDUCE EL TAMAÑO DE LA IMAGEN TRAIDA DE LA GALERÍA
        //https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri&usg=ALkJrhgBTjGYLe5qrs2KOuiJZB7HUyJ6zw
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 300) ? (originalSize / 300) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        //LLAMADO POR getThumbnail
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    public static int nearest2pow(int value) {
        return value == 0 ? 0
                : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        //RECIBE UNA URI Y LA CONVIERTE A BITMAP.
        //SUSTITUIDO POR getThumbnail PARA REDUCIR EL PESO DE LA IMAGEN PORQUE DABA ERROR DE MEMORIA....
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void subirImagen() {
        new Codificar_image().execute();
    }


    private class Codificar_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            //bitmap = BitmapFactory.decodeFile(file_uri.getPath());//Convertimos a bitmap
            Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, 400, 225, false);//establecemos el tamaño EXACTO PARA CUANDO SE VAYA A MOSTRAR EN EL RECYCLERVIEW
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);//0-100. 100 calidad máxima. SE REDUCE PARA QUE AL GUARDA EN SERVIDOR EL ARCHIVO TENGA MENOS PESO
            resized_bitmap.recycle();//reciclamos memoria
            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            //PARA RECICLAR EL BITMAP SIN QUE DE ERROR...
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            solicitud_Volley();
        }
    }

    private void solicitud_Volley() {

        Long fechaHora = System.currentTimeMillis();

        //Formato de fech elegido:
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        final String stringfechahora = sdf.format(fechaHora);
        Log.v("", "Fecha del sistema: " + stringfechahora);


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String patronUrl = Conexiones.CREAR_NUEVO_REGISTRO;
        String uri = String.format(patronUrl);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Transfiriendo datos al Servidor... espera por favor.");
        pDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject respuestaJSON = null;   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                        try {
                            respuestaJSON = new JSONObject(response.toString());
                            String resultJSON = respuestaJSON.getString("estado");
                            if (resultJSON.equals("1")) {
                                pDialog.dismiss();
                                Toast.makeText(ActivityRegistro.this, "La imagen ha sido alojada en el servidor", Toast.LENGTH_SHORT).show();

                             /*   Snackbar snack = Snackbar.make(btnGuardar, "La fotografía ha sido alojada en el servidor", Snackbar.LENGTH_LONG);
                                ViewGroup group = (ViewGroup) snack.getView();
                                group.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                snack.show();
*/
                                cerrarActivity();

                            } else if (resultJSON.equals("2")) {
                                pDialog.dismiss();
                                Toast.makeText(ActivityRegistro.this, "La fotografía no ha podido ser alojada en el servidor. Inténtalo de nuevo más tarde",
                                        Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityRegistro.this, "Ha ocurrido el siguiente error: " + error.getMessage().toString(), Toast.LENGTH_LONG).show();
                Log.d("ERROR", error.getMessage().toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("encoded_string", encoded_string);
                map.put("image_name", image_name);
                map.put("lugar", lugar);
                map.put("anio", anio);
                map.put("mes", mes);
                //map.put("path",image_name);Se construye en el WS
                map.put("descripcion", descripcion);
                map.put("fechacreacion", stringfechahora);
                return map;
            }
        };
        requestQueue.add(request);

        //cerrarActivity();

    }

    private void cerrarActivity() {

        Intent intent = new Intent(ActivityRegistro.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
