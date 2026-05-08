
package com.didistore.model.modulelogisticslocation;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Departamentos {
    
    private int idDepartamento;
    private String departamento;
    
    public Departamentos() {}
    
    public Departamentos (int idDepartamento,
            String departamento) {
        
        this.idDepartamento = idDepartamento;
        this.departamento = departamento;
    }
    
    public int getidDepartamento() { return idDepartamento; }
    public void setidDepartamento(int idDepartamento) { this.idDepartamento = idDepartamento; }
    
    public String getdepartamento() { return departamento; }
    public void setdepartamento(String departamento) { this.departamento = departamento; }    
}