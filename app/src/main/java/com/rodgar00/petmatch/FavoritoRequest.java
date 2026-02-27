package com.rodgar00.petmatch;

import com.google.gson.annotations.SerializedName;

public class FavoritoRequest {

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

    @SerializedName("esRefugio")
    private boolean esRefugio;

    @SerializedName("imagen")
    private String imagen;

    public FavoritoRequest(String nombre, String duenyo, int edad, String localizacion,
                           String descripcion, String categoria, boolean esRefugio, String raza, String imagen) {
        this.nombre = nombre;
        this.duenyo = duenyo;
        this.edad = edad;
        this.localizacion = localizacion;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.esRefugio = esRefugio;
        this.raza = raza;
        this.imagen = imagen;
    }

    public String getNombre() { return nombre; }
    public String getRaza() { return raza; }
    public String getDuenyo() { return duenyo; }
    public int getEdad() { return edad; }
    public String getLocalizacion() { return localizacion; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public boolean isEsRefugio() { return esRefugio; }
    public String getImagen() { return imagen; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRaza(String raza) { this.raza = raza; }
    public void setDuenyo(String duenyo) { this.duenyo = duenyo; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setEsRefugio(boolean esRefugio) { this.esRefugio = esRefugio; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}