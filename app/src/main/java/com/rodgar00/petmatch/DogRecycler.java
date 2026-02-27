package com.rodgar00.petmatch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DogRecycler extends RecyclerView.Adapter<DogRecycler.DogViewHolder> {

    private Context context;
    private ArrayList<DogModel> dogModels;
    private String tablaActual = "adoptados";
    private ArrayList<String> favoritosLocales = new ArrayList<>();

    public DogRecycler(Context context, ArrayList<DogModel> dogModels) {
        this.context = context;
        this.dogModels = dogModels;
    }

    public void setTablaActual(String tabla) {
        this.tablaActual = tabla;
    }

    public void setFavoritosLocales(ArrayList<String> favoritos) {
        this.favoritosLocales = favoritos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.tarjeta, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        DogModel dog = dogModels.get(position);

        holder.nombreText.setText(dog.getNombre());
        if (dog.getDuenyo() != null && !dog.getDuenyo().isEmpty()) {
            holder.duenyoText.setText("Dueño: " + dog.getDuenyo());
            holder.duenyoText.setVisibility(View.VISIBLE);
        } else {
            holder.duenyoText.setVisibility(View.GONE);
        }
        holder.categoriaText.setText("Categoría: " + dog.getCategoria());
        holder.refugioText.setText("Refugio: " + dog.getEsRefugio());

        if (tablaActual.equals("favoritos")) {
            holder.btnFavorito.setVisibility(View.VISIBLE);
            holder.btnFavorito.setImageResource(android.R.drawable.ic_menu_delete);
            holder.btnFavorito.setEnabled(true);
        } else if (tablaActual.equals("adoptados")) {
            holder.btnFavorito.setVisibility(View.VISIBLE);

            if (favoritosLocales.contains(dog.getNombre())) {
                holder.btnFavorito.setImageResource(R.drawable.ic_favorite_filled);
                holder.btnFavorito.setEnabled(false);
            } else {
                holder.btnFavorito.setImageResource(R.drawable.ic_favorite_border);
                holder.btnFavorito.setEnabled(true);
            }
        } else {
            holder.btnFavorito.setVisibility(View.GONE);
        }

        holder.btnFavorito.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            if (tablaActual.equals("favoritos")) {
                hacerLlamadaEliminarFavorito(dog, currentPosition);
            } else {
                holder.btnFavorito.setImageResource(R.drawable.ic_favorite_filled);
                holder.btnFavorito.setEnabled(false);

                if (!favoritosLocales.contains(dog.getNombre())) {
                    favoritosLocales.add(dog.getNombre());
                }

                Toast.makeText(context, "Guardando...", Toast.LENGTH_SHORT).show();

                okhttp3.RequestBody nombrePart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getNombre() != null ? dog.getNombre() : "");
                okhttp3.RequestBody duenyoPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getDuenyo() != null ? dog.getDuenyo() : "");
                okhttp3.RequestBody edadPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(dog.getEdad()));
                okhttp3.RequestBody localizacionPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getLocalizacion() != null ? dog.getLocalizacion() : "");
                okhttp3.RequestBody descripcionPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getDescripcion() != null ? dog.getDescripcion() : "");
                okhttp3.RequestBody categoriaPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getCategoria() != null ? dog.getCategoria() : "Otro");
                okhttp3.RequestBody refugioPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(dog.getEsRefugio()));
                okhttp3.RequestBody razaPart = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, dog.getRaza() != null ? dog.getRaza() : "sin raza");

                Glide.with(context)
                        .asBitmap()
                        .load(dog.getImagen())
                        .into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull android.graphics.Bitmap resource, @androidx.annotation.Nullable com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> transition) {
                                java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
                                resource.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream);
                                byte[] byteArray = stream.toByteArray();
                                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), byteArray);
                                okhttp3.MultipartBody.Part bodyImagen = okhttp3.MultipartBody.Part.createFormData("imagen", "favorito.jpg", requestFile);

                                hacerLlamadaFavoritos(nombrePart, duenyoPart, edadPart, localizacionPart, descripcionPart, categoriaPart, refugioPart, razaPart, bodyImagen, dog.getNombre(), currentPosition);
                            }

                            @Override
                            public void onLoadCleared(@androidx.annotation.Nullable android.graphics.drawable.Drawable placeholder) {}

                            @Override
                            public void onLoadFailed(@androidx.annotation.Nullable android.graphics.drawable.Drawable errorDrawable) {
                                hacerLlamadaFavoritos(nombrePart, duenyoPart, edadPart, localizacionPart, descripcionPart, categoriaPart, refugioPart, razaPart, null, dog.getNombre(), currentPosition);
                            }
                        });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileDog.class);
            intent.putExtra("nombre", dog.getNombre());
            intent.putExtra("duenyo", dog.getDuenyo());
            intent.putExtra("categoria", dog.getCategoria());
            intent.putExtra("refugio", dog.getEsRefugio());
            intent.putExtra("imagen", dog.getImagen());
            intent.putExtra("descripcion", dog.getDescripcion());
            intent.putExtra("raza", dog.getRaza());
            intent.putExtra("localizacion", dog.getLocalizacion());
            context.startActivity(intent);
        });

        Glide.with(context)
                .load(dog.getImagen())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(holder.dogImage);
    }

    @Override
    public int getItemCount() {
        return dogModels.size();
    }

    private void hacerLlamadaFavoritos(okhttp3.RequestBody nombre, okhttp3.RequestBody duenyo, okhttp3.RequestBody edad,
                                       okhttp3.RequestBody localizacion, okhttp3.RequestBody descripcion, okhttp3.RequestBody categoria,
                                       okhttp3.RequestBody refugio, okhttp3.RequestBody raza, okhttp3.MultipartBody.Part imagen,
                                       String nombrePerro, int position) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> call = apiService.guardarFavorito(nombre, duenyo, edad, localizacion, descripcion, categoria, refugio, raza, imagen);

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, nombrePerro + " añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    favoritosLocales.remove(nombrePerro);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                favoritosLocales.remove(nombrePerro);
                notifyItemChanged(position);
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hacerLlamadaEliminarFavorito(DogModel dog, int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Void> call = apiService.eliminarFavorito(dog.getNombre(), dog.getDuenyo());

        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    dogModels.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, dogModels.size());
                    Toast.makeText(context, dog.getNombre() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView nombreText, duenyoText, categoriaText, refugioText;
        ImageView dogImage, btnFavorito;

        public DogViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreText = itemView.findViewById(R.id.textoNombre);
            duenyoText = itemView.findViewById(R.id.textoDuenyo);
            categoriaText = itemView.findViewById(R.id.textoCategoria);
            refugioText = itemView.findViewById(R.id.textoRefugio);
            dogImage = itemView.findViewById(R.id.imagenTarjetaDog);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
        }
    }
}