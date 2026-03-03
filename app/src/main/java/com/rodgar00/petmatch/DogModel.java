package com.rodgar00.petmatch;

import com.google.gson.annotations.SerializedName;

public class DogModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("raza")
    private String raza;

    @SerializedName("duenyo")
    private String duenyo;

    @SerializedName("edad")
    private int edad;

    @SerializedName("localizacion")
    private String localizacion;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("es_refugio_texto")
    private String esRefugioTexto;

    @SerializedName("imagen")
    private String imagen;

    public DogModel(int id, String nombre, String duenyo, int edad, String localizacion,
                    String descripcion, String categoria, String esRefugioTexto,
                    String raza, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.duenyo = duenyo;
        this.edad = edad;
        this.localizacion = localizacion;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.esRefugioTexto = esRefugioTexto;
        this.raza = raza;
        this.imagen = imagen;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre != null ? nombre : "Sin nombre"; }
    public String getDuenyo() { return duenyo != null ? duenyo : "Desconocido"; }
    public int getEdad() { return edad; }
    public String getLocalizacion() { return localizacion != null ? localizacion : "Desconocida"; }
    public String getDescripcion() { return descripcion != null ? descripcion : "Sin descripción"; }
    public String getRaza() { return raza != null ? raza : "Desconocida"; }
    public String getCategoria() { return categoria != null ? categoria : "Desconocida"; }
    public String getEsRefugio() { return esRefugioTexto != null ? esRefugioTexto : "No"; }
    public String getImagen() { return imagen != null ? imagen : ""; }
}