
package com.didistore.model;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Departamentos {
    
    private int id_Departamento;
    private String departamento;
    
    public Departamentos() {}
    
    public Departamentos (int id_Departamento,
            String departamento) {
        
        this.id_Departamento = id_Departamento;
        this.departamento = departamento;
    }
    
    public int getid_Departamento() { return id_Departamento; }
    public void setid_Departamento(int id_Departamento) { this.id_Departamento = id_Departamento; }
    
    public String getdepartamento() { return departamento; }
    public void setdepartamento(String departamento) { this.departamento = departamento; }    
}