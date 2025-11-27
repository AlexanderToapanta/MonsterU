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
@Table(name = "peesc_estciv")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeescEstciv.findAll", query = "SELECT p FROM PeescEstciv p"),
    @NamedQuery(name = "PeescEstciv.findByPeescId", query = "SELECT p FROM PeescEstciv p WHERE p.peescId = :peescId"),
    @NamedQuery(name = "PeescEstciv.findByPeescDescri", query = "SELECT p FROM PeescEstciv p WHERE p.peescDescri = :peescDescri")})
public class PeescEstciv implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PEESC_ID")
    private String peescId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "PEESC_DESCRI")
    private String peescDescri;
    @OneToMany(mappedBy = "peescId")
    private Collection<PeperPerson> peperPersonCollection;

    public PeescEstciv() {
    }

    public PeescEstciv(String peescId) {
        this.peescId = peescId;
    }

    public PeescEstciv(String peescId, String peescDescri) {
        this.peescId = peescId;
        this.peescDescri = peescDescri;
    }

    public String getPeescId() {
        return peescId;
    }

    public void setPeescId(String peescId) {
        this.peescId = peescId;
    }

    public String getPeescDescri() {
        return peescDescri;
    }

    public void setPeescDescri(String peescDescri) {
        this.peescDescri = peescDescri;
    }

    @XmlTransient
    public Collection<PeperPerson> getPeperPersonCollection() {
        return peperPersonCollection;
    }

    public void setPeperPersonCollection(Collection<PeperPerson> peperPersonCollection) {
        this.peperPersonCollection = peperPersonCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (peescId != null ? peescId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PeescEstciv)) {
            return false;
        }
        PeescEstciv other = (PeescEstciv) object;
        if ((this.peescId == null && other.peescId != null) || (this.peescId != null && !this.peescId.equals(other.peescId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.PeescEstciv[ peescId=" + peescId + " ]";
    }
    
}
