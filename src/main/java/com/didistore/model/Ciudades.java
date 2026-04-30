
package com.didistore.model;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Ciudades {
    
    private int id_Ciudad;
    private String nombre_Ciudad;
    private int departamento_Id;
    private String codigo_Postal;
    
    public Ciudades() {}
    
    public Ciudades (int id_Ciudad,
            String nombre_Ciudad,
            int departamento_Id,
            String codigo_Postal) {
        
        this.id_Ciudad = id_Ciudad;
        this.nombre_Ciudad = nombre_Ciudad;
        this.departamento_Id = departamento_Id;
        this.codigo_Postal = codigo_Postal;
    }
    
    public int getid_Ciudad() { return id_Ciudad; }
    public void setid_Ciudad(int id_Ciudad) { this.id_Ciudad = id_Ciudad; }
    
    public String getnombre_Ciudad() { return nombre_Ciudad; }
    public void setnombre_Ciudad(String nombre_Ciudad) { this.nombre_Ciudad = nombre_Ciudad; }
    
    public int getdepartamento_Id() { return departamento_Id; }
    public void setdepartamento_Id(int departamento_Id) { this.departamento_Id = departamento_Id; }
    
    public String getcodigo_Postal() { return codigo_Postal; }
    public void setcodigo_Postal(String codigo_Postal) { this.codigo_Postal = codigo_Postal; }
}