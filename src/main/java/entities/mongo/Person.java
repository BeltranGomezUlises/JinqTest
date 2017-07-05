/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.mongo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */

@Entity  // establecer como entidad
@NoSql(dataFormat = DataFormatType.MAPPED, dataType = "persons") //especificar tipo NoSQL, mappeado y nombre de coleccion
public class Person implements Serializable {

    @Id //asignar como id
    @GeneratedValue // dejar como generado
    @Field(name = "_id") //nombre del campo
    private String id;
    @Field(name = "nombre")
    private String nombre;

    @Temporal(javax.persistence.TemporalType.DATE) //asignar tipo de dato
    @Field(name = "fechaRegistro")
    private Date fechaRegistro;
    @Field(name = "edad")
    private int edad;
    
    @Embedded
    @ElementCollection //necesario para elemento encrustrado como lista
    @Field(name = "mascotas")
    private List<Mascota> mascotas;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", nombre=" + nombre + ", fechaRegistro=" + fechaRegistro + ", edad=" + edad + ", mascotas=" + mascotas + '}';
    }

}
