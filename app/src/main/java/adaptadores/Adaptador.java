package adaptadores;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.antonioejemplo.casarozas.R;
import com.bumptech.glide.Glide;

import java.util.List;

import modelos.CasaRozas;


/**
 * Created by Susana on 22/09/2016.
 */
public class Adaptador extends RecyclerView.Adapter<Adaptador.CasaRozasViewHolder> {

    private final Context contexto;
    private List<CasaRozas> items;//Collection de Modelo. Los datos nos llegan desde el Main en esta Collection List()
    private CasaRozas casaRozas;
    private static final String LOGTAG = "AdapatadorVolley";//Constante para gestionar la escritura en el Log
    private OnItemClickListener escucha;

    //Conexion a la API
    //private RequestQueue requestQueueAdaptador;//Cola de peticiones de Volley.
    //private JsonObjectRequest myjsonObjectRequestAdaptador;//Tipo de petición Volley utilizada...


    //Constructor de la clase Adaptador
    public Adaptador(List<CasaRozas> datos, OnItemClickListener escucha, Context contexto) {
        this.contexto = contexto;
        this.escucha = escucha;
        this.items = datos;

    }

    public Adaptador(Context contexto) {
        this.contexto = contexto;
    }




    /**
     * Se llama cuando RecyclerView necesita un nuevo enlace @ {} ViewHolder del tipo dado para representar
     * Un elemento.
     * <p>
     * <p>
     * Esta nueva ViewHolder deberá estar construido con una nueva vista que pueden representar a los artículos
     * Del tipo dado. Puede crear una nueva vista de forma manual o inflarlo a partir de un XML
     * Archivo de diseño.
     * <p>
     * El nuevo ViewHolder se utilizará para mostrar los elementos del adaptador utilizando
     * {#onBindViewHolder @link (ViewHolder, int, Lista)}. Ya que será re-utilizado para mostrar
     * Diferentes elementos en el conjunto de datos, es una buena idea para almacenar en caché las referencias a sub vistas
     * Ver para evitar la innecesaria {@ link Ver # findViewById (int)} llama.
     *
     * @param ViewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que tiene una visión del tipo de vista determinado.
     * @ Param matriz La ViewGroup en la que se añadirá la nueva vista después de que está obligado a
     * Una posición adaptador.
     * @see #getItemViewType (Int)
     * @see #onBindViewHolder (ViewHolder, int)
     */
    @Override
    public CasaRozasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_recyclerview, parent, false);


        CasaRozasViewHolder tvh = new CasaRozasViewHolder(itemView);

        return tvh;
    }


    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CasaRozasViewHolder holder, int position) {

        casaRozas = items.get(position);

        //Llamamos a dameFecha pasándole items.get(position).getDia() para que la formatee
        holder.txtLugar.setText(String.format("Lugar %s", items.get(position).getLugar()));

        holder.txtmes.setText(String.format("Mes de %s", items.get(position).getMes()));

        holder.txtanio.setText(String.format("Año %s", items.get(position).getAnio()));
        holder.txtdescripcion.setText(items.get(position).getDescripcion());

        //Informamos la imagen con Glide:
        Glide.with(contexto)
                .load("http://petty.hol.es/CasaRozas/"+items.get(position).getImagen())//Desde dónde cargamos las imágenes
                //.placeholder(R.drawable.image_susti)//Imagen de sustitución mientras carga la imagen final. Contiene transición fade.
                .error(R.drawable.image_susti)//Imagen de sustitución si se ha producido error de carga
                //.override(600,400)//Tamaño aplicado a la imagen. Tamaño en px. cuidado con los tamaños de las pantallas de los dispositivos.
                .centerCrop()//Escalado de imagen para llenar siempre los límites establecidos en diseño
                //.skipMemoryCache(true)//Omitiría la memoria caché. Por defecto está activada.
                //.diskCacheStrategy(DiskCacheStrategy.ALL)//Gestión de la caché de disco.
                .into(holder.imagenFoto);//dónde vamos a mostrar las imágenes


        //Informamos la imagen con Picasso:
        /*Picasso.with(contexto)
                .load("http://petty.hol.es/CasaRozas/"+items.get(position).getImagen())
                .resize(600, 400)
                .centerCrop()
                .error(R.drawable.image_susti)
                .into(holder.imagenFoto);
        Picasso.with(contexto).setIndicatorsEnabled(true);*/



       /* holder.cardView.setRadius(10);
        holder.cardView.setCardElevation(6);//Parece que no se aprecia
        holder.cardView.setContentPadding(6,6,6,6);*/


    }

    /**
     * Llamado por RecyclerView para visualizar los datos en la posición especificada. Este método debe
     * Actualizar el contenido de la {@ link ViewHolder # itemView} para reflejar el tema en la propuesta
     * posición.
     * <P>
     * Tenga en cuenta que a diferencia de {ListView @link}, RecyclerView no llamará a este método
     * Otra vez si la posición del elemento cambia en el conjunto de datos a menos que el artículo en sí mismo es
     * Invalidada o la nueva posición no se puede determinar. Por esta razón, sólo deben
     * Utilizar el <code> Posición </ code> mientras que la adquisición del elemento de datos relacionados con el interior
     * Este método y no debe guardar una copia de la misma. Si necesita la posición de un elemento más tarde
     * En (por ejemplo en un detector de clics), utilice {@ link ViewHolder # getAdapterPosition ()} y esto
     * Tiene la posición del adaptador actualizado.
     * <P>
     * Anulación {#onBindViewHolder @link (ViewHolder, int, Lista)} en su lugar si adaptador puede
     * Manejar unen parcial effcient.
     *
     * Titular @param El ViewHolder que debe ser actualizado para representar el contenido de la
     * Elemento en la posición dada en el conjunto de datos.
     * Posición @param La posición del elemento dentro del conjunto de datos del adaptador.
     */


    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public interface OnItemClickListener {
        //
        public void onClick(RecyclerView.ViewHolder holder, int idPromocion, View v);
    }


    //Para implementar el patrón viewHolder...
    public class CasaRozasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //Se definen las views que contenga el layout que dibuja los items del recycler
        private TextView txtLugar;
        private TextView txtmes;
        private TextView txtanio;
        private TextView txtdescripcion;
        private ImageView imagenFoto;
        private CardView cardView;
        //private ImageView imagenllamada;
        private Button btnVerImagen;

        public CasaRozasViewHolder(View itemView) {
            super(itemView);

            txtLugar = (TextView) itemView.findViewById(R.id.lugar);
            txtmes = (TextView) itemView.findViewById(R.id.mes);
            txtanio = (TextView) itemView.findViewById(R.id.anio);
            txtdescripcion = (TextView) itemView.findViewById(R.id.descripcion);
            imagenFoto = (ImageView) itemView.findViewById(R.id.foto);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            btnVerImagen=(Button) itemView.findViewById(R.id.verImagen);


            //Preparamos el listener y se lo asignamos a los controles que queramos...
            btnVerImagen.setOnClickListener(this);
            imagenFoto.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            //Método sobreescrito. La clase hereda de View.OnClickListener.
           escucha.onClick(this, obtenerIdContacto(getAdapterPosition()), view);

        }

        private int obtenerIdContacto(int posicion) {

            //return Integer.parseInt(items.get(posicion).getId());
            return items.get(posicion).getId();
            //return (int)contactos.get_id();
            //return items.getInt(contactos.get_id());

        }


    }//Fin class UsuarioviewHolder


}