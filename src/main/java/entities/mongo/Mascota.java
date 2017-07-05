/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.mongo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */

@Embeddable // necesario para poder incrustar en una entidad
@NoSql(dataFormat = DataFormatType.MAPPED) // mapeado a base de datos NoSQL
public class Mascota implements Serializable {

    @Column(name = "edad") //nombre de campo con field de eclipselink
    private int edad;
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "commentarios") //nombre de campo con column de JPA
    private String comentarios;

    public Mascota() {
    }

    public Mascota(int edad, String nombre) {
        this.edad = edad;
        this.nombre = nombre;
    }

    public Mascota(int edad, String nombre, String comentarios) {
        this.edad = edad;
        this.nombre = nombre;
        this.comentarios = comentarios;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    @Override
    public String toString() {
        return "Mascota{" + "edad=" + edad + ", nombre=" + nombre + ", comentarios=" + comentarios + '}';
    }

}
