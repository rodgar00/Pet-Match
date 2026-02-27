package com.rodgar00.petmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Necesitas la librería Glide
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private Context context;
    private List<DogModel> petList;

    public PetAdapter(Context context, List<DogModel> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño de la card que creamos antes
        View view = LayoutInflater.from(context).inflate(R.layout.tarjeta, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        DogModel pet = petList.get(position);

        // Llenamos los datos
        holder.txtNombre.setText(pet.getNombre());
        holder.txtEdad.setText("Edad: " + pet.getEdad());
        holder.txtRaza.setText("Raza: " + pet.getRaza());

        // Manejo de la imagen desde Django
        String urlImagen = pet.getImagen();
        if (urlImagen != null && !urlImagen.startsWith("http")) {
            urlImagen = "http://10.0.2.2:8000" + urlImagen;
        }

        Glide.with(context)
                .load(urlImagen)
                .placeholder(R.drawable.pet_match2)
                .into(holder.imgPet);
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtEdad, txtRaza, txtDuenyo;
        ImageView imgPet;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vinculamos con los IDs REALES de tu archivo tarjeta.xml
            txtNombre = itemView.findViewById(R.id.textoNombre);
            txtEdad = itemView.findViewById(R.id.textoEdad);
            txtRaza = itemView.findViewById(R.id.textoraza);
            txtDuenyo = itemView.findViewById(R.id.textoDuenyo); // Opcional si quieres mostrar el dueño
            imgPet = itemView.findViewById(R.id.imagenTarjetaDog);
        }
    }
}