package adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.antonioejemplo.casarozas.R;
import com.bumptech.glide.Glide;

import java.util.List;

import modelos.CasaRozas;

/**
 * Created by Susana on 21/11/2016.
 */

public class AdaptadorGrid extends BaseAdapter {

    private Context contexto;
    private List<CasaRozas> items;//Collection de Modelo. Los datos nos llegan desde el Main en esta Collection List()

    public AdaptadorGrid(Context contexto, List<CasaRozas> casaRozas) {
        this.contexto = contexto;
        this.items = casaRozas;
    }

    @Override
    public int getCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) contexto
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.items_gridview, parent, false);
        }


        ImageView imagen=(ImageView)convertView.findViewById(R.id.imagengrid);
        TextView texto=(TextView)convertView.findViewById(R.id.textofoto);

        //Informamos la imagen con Glide:
        Glide.with(contexto)
                .load("http://petty.hol.es/CasaRozas/"+items.get(position).getImagen())//Desde dónde cargamos las imágenes
                //.placeholder(R.drawable.image_susti)//Imagen de sustitución mientras carga la imagen final. Contiene transición fade.
                .error(R.drawable.image_susti)//Imagen de sustitución si se ha producido error de carga
                //.override(600,400)//Tamaño aplicado a la imagen. Tamaño en px. cuidado con los tamaños de las pantallas de los dispositivos.
                .centerCrop()//Escalado de imagen para llenar siempre los límites establecidos en diseño
                //.skipMemoryCache(true)//Omitiría la memoria caché. Por defecto está activada.
                //.diskCacheStrategy(DiskCacheStrategy.ALL)//Gestión de la caché de disco.
                .into(imagen);//dónde vamos a mostrar las imágenes

        texto.setText(items.get(position).getMes()+" "+ items.get(position).getAnio());


        return convertView;
    }
}
