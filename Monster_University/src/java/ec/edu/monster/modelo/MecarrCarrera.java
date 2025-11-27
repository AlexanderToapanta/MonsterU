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
@Table(name = "mecarr_carrera")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MecarrCarrera.findAll", query = "SELECT m FROM MecarrCarrera m"),
    @NamedQuery(name = "MecarrCarrera.findByMecarrId", query = "SELECT m FROM MecarrCarrera m WHERE m.mecarrId = :mecarrId"),
    @NamedQuery(name = "MecarrCarrera.findByMecarrNombre", query = "SELECT m FROM MecarrCarrera m WHERE m.mecarrNombre = :mecarrNombre"),
    @NamedQuery(name = "MecarrCarrera.findByMecarrMaxCred", query = "SELECT m FROM MecarrCarrera m WHERE m.mecarrMaxCred = :mecarrMaxCred"),
    @NamedQuery(name = "MecarrCarrera.findByMecarrMinCred", query = "SELECT m FROM MecarrCarrera m WHERE m.mecarrMinCred = :mecarrMinCred")})
public class MecarrCarrera implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MECARR_ID")
    private String mecarrId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "MECARR_NOMBRE")
    private String mecarrNombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MECARR_MAX_CRED")
    private int mecarrMaxCred;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MECARR_MIN_CRED")
    private int mecarrMinCred;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mecarrCarrera")
    private Collection<MeestEstud> meestEstudCollection;

    public MecarrCarrera() {
    }

    public MecarrCarrera(String mecarrId) {
        this.mecarrId = mecarrId;
    }

    public MecarrCarrera(String mecarrId, String mecarrNombre, int mecarrMaxCred, int mecarrMinCred) {
        this.mecarrId = mecarrId;
        this.mecarrNombre = mecarrNombre;
        this.mecarrMaxCred = mecarrMaxCred;
        this.mecarrMinCred = mecarrMinCred;
    }

    public String getMecarrId() {
        return mecarrId;
    }

    public void setMecarrId(String mecarrId) {
        this.mecarrId = mecarrId;
    }

    public String getMecarrNombre() {
        return mecarrNombre;
    }

    public void setMecarrNombre(String mecarrNombre) {
        this.mecarrNombre = mecarrNombre;
    }

    public int getMecarrMaxCred() {
        return mecarrMaxCred;
    }

    public void setMecarrMaxCred(int mecarrMaxCred) {
        this.mecarrMaxCred = mecarrMaxCred;
    }

    public int getMecarrMinCred() {
        return mecarrMinCred;
    }

    public void setMecarrMinCred(int mecarrMinCred) {
        this.mecarrMinCred = mecarrMinCred;
    }

    @XmlTransient
    public Collection<MeestEstud> getMeestEstudCollection() {
        return meestEstudCollection;
    }

    public void setMeestEstudCollection(Collection<MeestEstud> meestEstudCollection) {
        this.meestEstudCollection = meestEstudCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mecarrId != null ? mecarrId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MecarrCarrera)) {
            return false;
        }
        MecarrCarrera other = (MecarrCarrera) object;
        if ((this.mecarrId == null && other.mecarrId != null) || (this.mecarrId != null && !this.mecarrId.equals(other.mecarrId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MecarrCarrera[ mecarrId=" + mecarrId + " ]";
    }
    
}
