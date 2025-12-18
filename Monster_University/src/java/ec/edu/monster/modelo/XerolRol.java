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
@Table(name = "xerol_rol")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "XerolRol.findAll", query = "SELECT x FROM XerolRol x"),
    @NamedQuery(name = "XerolRol.findByXerolId", query = "SELECT x FROM XerolRol x WHERE x.xerolId = :xerolId"),
    @NamedQuery(name = "XerolRol.findByXerolNombre", query = "SELECT x FROM XerolRol x WHERE x.xerolNombre = :xerolNombre"),
    @NamedQuery(name = "XerolRol.findByXerolDescri", query = "SELECT x FROM XerolRol x WHERE x.xerolDescri = :xerolDescri")})
public class XerolRol implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "XEROL_ID")
    private String xerolId;
    
    @Size(max = 30)
    @Column(name = "XEROL_NOMBRE")
    private String xerolNombre;
    
    @Size(max = 100)
    @Column(name = "XEROL_DESCRI")
    private String xerolDescri;
    
    // RELACIÓN ONE-TO-MANY (UN ROL TIENE MUCHOS USUARIOS)
    // El mappedBy = "xerolId" se refiere al atributo en XeusuUsuar.java
    @OneToMany(mappedBy = "xerolId")
    private Collection<XeusuUsuar> xeusuUsuarCollection;
    

    public XerolRol() {
    }

    public XerolRol(String xerolId) {
        this.xerolId = xerolId;
    }

    public String getXerolId() {
        return xerolId;
    }

    public void setXerolId(String xerolId) {
        this.xerolId = xerolId;
    }

    public String getXerolNombre() {
        return xerolNombre;
    }

    public void setXerolNombre(String xerolNombre) {
        this.xerolNombre = xerolNombre;
    }

    public String getXerolDescri() {
        return xerolDescri;
    }

    public void setXerolDescri(String xerolDescri) {
        this.xerolDescri = xerolDescri;
    }

    @XmlTransient
    public Collection<XeusuUsuar> getXeusuUsuarCollection() {
        return xeusuUsuarCollection;
    }

    public void setXeusuUsuarCollection(Collection<XeusuUsuar> xeusuUsuarCollection) {
        this.xeusuUsuarCollection = xeusuUsuarCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (xerolId != null ? xerolId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof XerolRol)) {
            return false;
        }
        XerolRol other = (XerolRol) object;
        if ((this.xerolId == null && other.xerolId != null) || 
            (this.xerolId != null && !this.xerolId.equals(other.xerolId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.modelo.XerolRol[ xerolId=" + xerolId + " ]";
    }
}