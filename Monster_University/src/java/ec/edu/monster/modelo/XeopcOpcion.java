/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "xeopc_opcion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "XeopcOpcion.findAll", query = "SELECT x FROM XeopcOpcion x"),
    @NamedQuery(name = "XeopcOpcion.findByXeopcId", query = "SELECT x FROM XeopcOpcion x WHERE x.xeopcId = :xeopcId"),
    @NamedQuery(name = "XeopcOpcion.findByXeopcNombre", query = "SELECT x FROM XeopcOpcion x WHERE x.xeopcNombre = :xeopcNombre")})
public class XeopcOpcion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "XEOPC_ID")
    private String xeopcId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "XEOPC_NOMBRE")
    private String xeopcNombre;
    
    @OneToMany(mappedBy = "xeopcOpcion")  // CAMBIADO de "xeopcId" a "xeopcOpcion"
private Collection<XrXerolXeopc> xrXerolXeopcCollection;
    public XeopcOpcion() {
    }

    public XeopcOpcion(String xeopcId) {
        this.xeopcId = xeopcId;
    }

    public XeopcOpcion(String xeopcId, String xeopcNombre) {
        this.xeopcId = xeopcId;
        this.xeopcNombre = xeopcNombre;
    }

    public String getXeopcId() {
        return xeopcId;
    }

    public void setXeopcId(String xeopcId) {
        this.xeopcId = xeopcId;
    }

    public String getXeopcNombre() {
        return xeopcNombre;
    }

    public void setXeopcNombre(String xeopcNombre) {
        this.xeopcNombre = xeopcNombre;
    }

    @XmlTransient
    public Collection<XrXerolXeopc> getXrXerolXeopcCollection() {
        return xrXerolXeopcCollection;
    }

    public void setXrXerolXeopcCollection(Collection<XrXerolXeopc> xrXerolXeopcCollection) {
        this.xrXerolXeopcCollection = xrXerolXeopcCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (xeopcId != null ? xeopcId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof XeopcOpcion)) {
            return false;
        }
        XeopcOpcion other = (XeopcOpcion) object;
        if ((this.xeopcId == null && other.xeopcId != null) || 
            (this.xeopcId != null && !this.xeopcId.equals(other.xeopcId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.modelo.XeopcOpcion[ xeopcId=" + xeopcId + " ]";
    }
}