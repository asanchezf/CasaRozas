package com.antonioejemplo.casarozas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import adaptadores.Adaptador;
import modelos.CasaRozas;
import volley.AppController;
import volley.MyJSonRequestImmediate;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "OBTENER_DATOS";
    private RecyclerView lista;
    private RecyclerView.LayoutManager lManager;
    private Context contexto;
    private RequestQueue requestQueue;
    private List<CasaRozas> listdatos;//Se le enviará al Adaptador
    private CasaRozas casaRozas;
    private JsonObjectRequest myjsonObjectRequest;
    private Adaptador adapter;
    //Variable que le pasamos a la llamada del adaptador. Necesita un listener
    private Adaptador.OnItemClickListener listener;
    private static long back_pressed;//Contador para cerrar la app al pulsar dos veces seguidas el btón de cerrar. Se gestiona en el evento onBackPressed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Definimos el RecyclerView..Pasar a un procedimiento.
        lista = (RecyclerView) findViewById(R.id.lista);
        lManager = new LinearLayoutManager(contexto);
        //lista.setLayoutManager(lManager);
        lista.setLayoutManager(
                new LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false));

        //requestQueue = Volley.newRequestQueue(this);

        traerDatos();


    }

    private void traerDatos() {

        //Para refrescar conocimientos de volley ver http://www.tutorialesandroid.net/trabajando-con-volley-en-android/
        //o sino http://www.hermosaprogramacion.com/2015/02/android-volley-peticiones-http/

        //NOTA: Los métodos POST suelen dar problemas en algunos tipos de respuesta. Se recomienda utilizar
        //StringRequest si se van a mandar datos utilizándolo.

                    /*  1-StringRequest: Este es el tipo más común, ya que permite solicitar un recurso con formato de texto plano, como son los documentos HTML.
                        2-ImageRequest: Como su nombre lo indica, permite obtener un recurso gráfico alojado en un servidor externo.
                        3-JsonObjectRequest: Obtiene una respuesta de tipo JSONObject a partir de un recurso con este formato.
                        4-JsonArrayRequest: Obtiene como respuesta un objeto del tipo JSONArray a partir de un formato JSON.*/



        String tag_json_obj_actual = "json_obj_req_actual";

        String patronUrl = "http://petty.hol.es/WebServicesPHPCasaRozas/obtener_datos_casa.php";
        String uri = String.format(patronUrl);

        listdatos = new ArrayList<CasaRozas>();

        Log.v(LOGTAG, "Ha llegado a immediateRequestTiempoActual. Uri: " + uri);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Accediendo a los datos espera por favor...");
        pDialog.show();

        //Creamos JSonObjectRequest de respuesta inmediata...
        myjsonObjectRequest = new MyJSonRequestImmediate(
                Request.Method.GET,
                uri,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response2) {

                        //String id = "";
                        pDialog.dismiss();
                        int id;
                        String lugar = "";
                        String anio = "";
                        String mes = "";
                        String imagen = "";
                        String descripcion = "";



                        try {

                            //for (int i = 0; i < response2.length(); i++) {
                            //JSONObject json_estado = response2.getJSONObject("estado");
                            String resultJSON = response2.getString("estado");

                            JSONArray json_array = response2.getJSONArray("alumnos");
                            for (int z = 0; z < json_array.length(); z++) {
                                //OJO: se ha cambiado a int. Antes era un String
                                id = json_array.getJSONObject(z).getInt("Id");
                                lugar = json_array.getJSONObject(z).getString("Lugar");
                                anio = json_array.getJSONObject(z).getString("Anio");
                                mes = json_array.getJSONObject(z).getString("Mes");
                                imagen = json_array.getJSONObject(z).getString("Imagen");
                                descripcion = json_array.getJSONObject(z).getString("Descripcion");

                                //CREAMOS EL MODELO CASAROZAS A PARTIR DE LOS DATOS OBTENIDOS EN EL SERVIDOR
                                casaRozas = new CasaRozas();
                                casaRozas.setId(id);
                                casaRozas.setLugar(lugar);
                                casaRozas.setAnio(anio);
                                casaRozas.setMes(mes);
                                casaRozas.setImagen(imagen);
                                casaRozas.setDescripcion(descripcion);

                                //LLENAMOS EL ARRAYLIST PARA LUEGO PASÁRSELO AL ADAPTADOR
                                listdatos.add(casaRozas);
                                Log.d(LOGTAG, "Tamaño listadatos: " + listdatos.size());

                            }



                            adapter=new Adaptador(listdatos, new Adaptador.OnItemClickListener() {
                                @Override
                                public void onClick(RecyclerView.ViewHolder holder, int idPromocion, View v) {

                                    if(v.getId()==R.id.verImagen) {

                                       // Toast.makeText(getBaseContext(), "Has hecho click en el botón", Toast.LENGTH_LONG).show();

                                        abrirActivityFoto(idPromocion);

                                    }
                                    else if(v.getId()==R.id.foto) {

                                        // Toast.makeText(getBaseContext(), "Has hecho click en el botón", Toast.LENGTH_LONG).show();

                                        abrirActivityFoto(idPromocion);

                                    }
                                }
                            }, getBaseContext());


                            lista.setAdapter(adapter);
                            /*adapter=new Adaptador(listdatos,listener,getContext());
                            lista.setAdapter(adapter);*/


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(LOGTAG, "Error Respuesta en JSON: ");
                            pDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Se ha producido un error conectando con el servidor.", Toast.LENGTH_LONG).show();
                        }

                        //priority = Request.Priority.IMMEDIATE;

                    }//fin onresponse

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGTAG, "Error Respuesta en JSON: " + error.getMessage());
                        pDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Se ha producido un error de respuesta accediendo al Servidor", Toast.LENGTH_SHORT).show();

                    }
                }
        );

        // Añadir petición a la cola
        AppController.getInstance().addToRequestQueue(myjsonObjectRequest, tag_json_obj_actual);


    }


private void abrirActivityFoto(int idPromocion) {

    String imagen = null;

    Iterator<CasaRozas> it = listdatos.iterator();

    while (it.hasNext()) {

        casaRozas = (CasaRozas) it.next();

        //idPromocion contiene el id de bbdd del usuario. Lo comparamos con el id que tiene la coleccion para recoger
        //todos los datos del registro seleccionado
        if (casaRozas.getId() == (idPromocion)) {

            imagen = casaRozas.getImagen();
            // Toast.makeText(getActivity(),"Datos recogidos.",Toast.LENGTH_LONG).show();
            break;
        }
    }

    Intent intent = new Intent(MainActivity.this, ActivityFoto.class);
    intent.putExtra("Id", idPromocion);
    intent.putExtra("Imagen", imagen);

    startActivity(intent);

}


    @Override
    public void onBackPressed() {
/**
 * Cierra la app cuando se ha pulsado dos veces seguidas en un intervalo inferior a dos segundos.
 */

        if (back_pressed + 2000 > System.currentTimeMillis())
            super.onBackPressed();
        else
            Toast.makeText(getBaseContext(), R.string.salir, Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
        // super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.grilla) {
            Intent intent=new Intent(MainActivity.this,ActivityGridView.class);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
