
package com.didistore.model.logistics;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Ciudades {
    
    private int idCiudad;
    private String nombreCiudad;
    private int departamentoId;
    private String codigoPostal;
    
    public Ciudades() {}
    
    public Ciudades (int idCiudad,
            String nombreCiudad,
            int departamentoId,
            String codigoPostal) {
        
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.departamentoId = departamentoId;
        this.codigoPostal = codigoPostal;
    }
    
    public int getidCiudad() { return idCiudad; }
    public void setidCiudad(int idCiudad) { this.idCiudad = idCiudad; }
    
    public String getnombreCiudad() { return nombreCiudad; }
    public void setnombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }
    
    public int getdepartamentoId() { return departamentoId; }
    public void setdepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }
    
    public String getcodigoPostal() { return codigoPostal; }
    public void setcodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}