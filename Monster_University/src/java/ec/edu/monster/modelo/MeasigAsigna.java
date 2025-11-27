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
@Table(name = "measig_asigna")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MeasigAsigna.findAll", query = "SELECT m FROM MeasigAsigna m"),
    @NamedQuery(name = "MeasigAsigna.findByMeasigId", query = "SELECT m FROM MeasigAsigna m WHERE m.measigId = :measigId"),
    @NamedQuery(name = "MeasigAsigna.findByMeasigNombre", query = "SELECT m FROM MeasigAsigna m WHERE m.measigNombre = :measigNombre"),
    @NamedQuery(name = "MeasigAsigna.findByMeasigNrc", query = "SELECT m FROM MeasigAsigna m WHERE m.measigNrc = :measigNrc"),
    @NamedQuery(name = "MeasigAsigna.findByMeasigCreditos", query = "SELECT m FROM MeasigAsigna m WHERE m.measigCreditos = :measigCreditos"),
    @NamedQuery(name = "MeasigAsigna.findByMeasigDescripcion", query = "SELECT m FROM MeasigAsigna m WHERE m.measigDescripcion = :measigDescripcion")})
public class MeasigAsigna implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEASIG_ID")
    private String measigId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "MEASIG_NOMBRE")
    private String measigNombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "MEASIG_NRC")
    private String measigNrc;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEASIG_CREDITOS")
    private int measigCreditos;
    @Size(max = 100)
    @Column(name = "MEASIG_DESCRIPCION")
    private String measigDescripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measigId")
    private Collection<MegrpGrupo> megrpGrupoCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measigAsigna")
    private Collection<MrMeasigMeasig> mrMeasigMeasigCollection;

    public MeasigAsigna() {
    }

    public MeasigAsigna(String measigId) {
        this.measigId = measigId;
    }

    public MeasigAsigna(String measigId, String measigNombre, String measigNrc, int measigCreditos) {
        this.measigId = measigId;
        this.measigNombre = measigNombre;
        this.measigNrc = measigNrc;
        this.measigCreditos = measigCreditos;
    }

    public String getMeasigId() {
        return measigId;
    }

    public void setMeasigId(String measigId) {
        this.measigId = measigId;
    }

    public String getMeasigNombre() {
        return measigNombre;
    }

    public void setMeasigNombre(String measigNombre) {
        this.measigNombre = measigNombre;
    }

    public String getMeasigNrc() {
        return measigNrc;
    }

    public void setMeasigNrc(String measigNrc) {
        this.measigNrc = measigNrc;
    }

    public int getMeasigCreditos() {
        return measigCreditos;
    }

    public void setMeasigCreditos(int measigCreditos) {
        this.measigCreditos = measigCreditos;
    }

    public String getMeasigDescripcion() {
        return measigDescripcion;
    }

    public void setMeasigDescripcion(String measigDescripcion) {
        this.measigDescripcion = measigDescripcion;
    }

    @XmlTransient
    public Collection<MegrpGrupo> getMegrpGrupoCollection() {
        return megrpGrupoCollection;
    }

    public void setMegrpGrupoCollection(Collection<MegrpGrupo> megrpGrupoCollection) {
        this.megrpGrupoCollection = megrpGrupoCollection;
    }

    @XmlTransient
    public Collection<MrMeasigMeasig> getMrMeasigMeasigCollection() {
        return mrMeasigMeasigCollection;
    }

    public void setMrMeasigMeasigCollection(Collection<MrMeasigMeasig> mrMeasigMeasigCollection) {
        this.mrMeasigMeasigCollection = mrMeasigMeasigCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (measigId != null ? measigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeasigAsigna)) {
            return false;
        }
        MeasigAsigna other = (MeasigAsigna) object;
        if ((this.measigId == null && other.measigId != null) || (this.measigId != null && !this.measigId.equals(other.measigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MeasigAsigna[ measigId=" + measigId + " ]";
    }
    
}
