/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "pesex_sexo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PesexSexo.findAll", query = "SELECT p FROM PesexSexo p"),
    @NamedQuery(name = "PesexSexo.findByPesexId", query = "SELECT p FROM PesexSexo p WHERE p.pesexId = :pesexId"),
    @NamedQuery(name = "PesexSexo.findByPesexDescri", query = "SELECT p FROM PesexSexo p WHERE p.pesexDescri = :pesexDescri")})
public class PesexSexo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1)
    @Column(name = "PESEX_ID")
    private String pesexId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "PESEX_DESCRI")
    private String pesexDescri;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pesexId")
    private Collection<PeperPerson> peperPersonCollection;

    public PesexSexo() {
    }

    public PesexSexo(String pesexId) {
        this.pesexId = pesexId;
    }

    public PesexSexo(String pesexId, String pesexDescri) {
        this.pesexId = pesexId;
        this.pesexDescri = pesexDescri;
    }

    public String getPesexId() {
        return pesexId;
    }

    public void setPesexId(String pesexId) {
        this.pesexId = pesexId;
    }

    public String getPesexDescri() {
        return pesexDescri;
    }

    public void setPesexDescri(String pesexDescri) {
        this.pesexDescri = pesexDescri;
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
        hash += (pesexId != null ? pesexId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PesexSexo)) {
            return false;
        }
        PesexSexo other = (PesexSexo) object;
        if ((this.pesexId == null && other.pesexId != null) || (this.pesexId != null && !this.pesexId.equals(other.pesexId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.PesexSexo[ pesexId=" + pesexId + " ]";
    }
    
}
