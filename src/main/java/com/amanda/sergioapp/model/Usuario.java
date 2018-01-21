package com.amanda.sergioapp.model;



public class Usuario {

    private String nombre;
    private String apellidos;
    private String cargo;
    private String peso;
    private String altura;
    private String fechaNacimiento;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getapellidos() {
        return apellidos;
    }
    public void setapellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getcargo() {
        return cargo;
    }
    public void setcargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

}
