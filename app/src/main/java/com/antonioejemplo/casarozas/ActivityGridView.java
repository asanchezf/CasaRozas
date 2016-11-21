package com.antonioejemplo.casarozas;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adaptadores.AdaptadorGridView;
import modelos.CasaRozas;
import volley.AppController;
import volley.MyJSonRequestImmediate;

public class ActivityGridView extends AppCompatActivity {

    private static final String LOGTAG ="OBTENER_DATOS" ;
    private List<CasaRozas> listdatos;
    private CasaRozas casaRozas;
    private JsonObjectRequest myjsonObjectRequest;
    private  GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);


         gridView=(GridView)findViewById(R.id.gridView);
        traerDatos();
        //gridView.setAdapter(new AdaptadorGridView());
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

        //Log.v(LOGTAG, "Ha llegado a immediateRequestTiempoActual. Uri: " + uri);

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
                               // Log.d(LOGTAG, "Tamaño listadatos: " + listdatos.size());

                            }

                            gridView.setAdapter(new AdaptadorGridView(getBaseContext(),listdatos));




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
}
