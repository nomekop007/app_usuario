package com.example.app_usuario_v2.model;

public class Calificacion {
    private String idCalificacion;
    private float calificacion;
    private String idUsuario;
    private String idTrasporte;
    private String fechaHora;


    public Calificacion() {

    }

    public String getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(String idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdTrasporte() {
        return idTrasporte;
    }

    public void setIdTrasporte(String idTrasporte) {
        this.idTrasporte = idTrasporte;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}
