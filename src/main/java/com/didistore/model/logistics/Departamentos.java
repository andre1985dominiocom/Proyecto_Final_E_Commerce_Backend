
package com.didistore.model.logistics;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa un departamento de un país, con su respectivo id y nombre del departamento
public class Departamentos {
    
    private int idDepartamento;
    private String departamento;
    
    // Constructor vacío y constructor con parámetros para inicializar los atributos de la clase
    public Departamentos() {}
    
    // Constructor con parámetros para inicializar los atributos de la clase
    public Departamentos (int idDepartamento,
            String departamento) {
        
        this.idDepartamento = idDepartamento;
        this.departamento = departamento;
    }
    
    // Métodos getter y setter para acceder y modificar los atributos de la clase
    public int getidDepartamento() { return idDepartamento; }
    public void setidDepartamento(int idDepartamento) { this.idDepartamento = idDepartamento; }
    
    public String getdepartamento() { return departamento; }
    public void setdepartamento(String departamento) { this.departamento = departamento; }    
}