/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import ec.edu.monster.modelo.MeperiPeriod;
import ec.edu.monster.modelo.PeperPerson;
import ec.edu.monster.modelo.MeasigAsigna;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Table(name = "megrp_grupo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MegrpGrupo.findAll", query = "SELECT m FROM MegrpGrupo m"),
    @NamedQuery(name = "MegrpGrupo.findByMegrpId", query = "SELECT m FROM MegrpGrupo m WHERE m.megrpId = :megrpId"),
    @NamedQuery(name = "MegrpGrupo.findByMegrpCodigo", query = "SELECT m FROM MegrpGrupo m WHERE m.megrpCodigo = :megrpCodigo"),
    @NamedQuery(name = "MegrpGrupo.findByMegrpAula", query = "SELECT m FROM MegrpGrupo m WHERE m.megrpAula = :megrpAula"),
    @NamedQuery(name = "MegrpGrupo.findByMegrpCupoMax", query = "SELECT m FROM MegrpGrupo m WHERE m.megrpCupoMax = :megrpCupoMax"),
    @NamedQuery(name = "MegrpGrupo.findByMegrpCupoAct", query = "SELECT m FROM MegrpGrupo m WHERE m.megrpCupoAct = :megrpCupoAct")})
public class MegrpGrupo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEGRP_ID")
    private String megrpId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "MEGRP_CODIGO")
    private String megrpCodigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 35)
    @Column(name = "MEGRP_AULA")
    private String megrpAula;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEGRP_CUPO_MAX")
    private int megrpCupoMax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEGRP_CUPO_ACT")
    private int megrpCupoAct;
    @ManyToMany(mappedBy = "megrpGrupoCollection")
    private Collection<MeestEstud> meestEstudCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "megrpGrupo")
    private Collection<MehorHorario> mehorHorarioCollection;
    @JoinColumn(name = "MEASIG_ID", referencedColumnName = "MEASIG_ID")
    @ManyToOne(optional = false)
    private MeasigAsigna measigId;
    @JoinColumn(name = "MEPERI_ID", referencedColumnName = "MEPERI_ID")
    @ManyToOne
    private MeperiPeriod meperiId;
    @JoinColumn(name = "PEPER_ID", referencedColumnName = "PEPER_ID")
    @ManyToOne(optional = false)
    private PeperPerson peperId;

    public MegrpGrupo() {
    }

    public MegrpGrupo(String megrpId) {
        this.megrpId = megrpId;
    }

    public MegrpGrupo(String megrpId, String megrpCodigo, String megrpAula, int megrpCupoMax, int megrpCupoAct) {
        this.megrpId = megrpId;
        this.megrpCodigo = megrpCodigo;
        this.megrpAula = megrpAula;
        this.megrpCupoMax = megrpCupoMax;
        this.megrpCupoAct = megrpCupoAct;
    }

    public String getMegrpId() {
        return megrpId;
    }

    public void setMegrpId(String megrpId) {
        this.megrpId = megrpId;
    }

    public String getMegrpCodigo() {
        return megrpCodigo;
    }

    public void setMegrpCodigo(String megrpCodigo) {
        this.megrpCodigo = megrpCodigo;
    }

    public String getMegrpAula() {
        return megrpAula;
    }

    public void setMegrpAula(String megrpAula) {
        this.megrpAula = megrpAula;
    }

    public int getMegrpCupoMax() {
        return megrpCupoMax;
    }

    public void setMegrpCupoMax(int megrpCupoMax) {
        this.megrpCupoMax = megrpCupoMax;
    }

    public int getMegrpCupoAct() {
        return megrpCupoAct;
    }

    public void setMegrpCupoAct(int megrpCupoAct) {
        this.megrpCupoAct = megrpCupoAct;
    }

    @XmlTransient
    public Collection<MeestEstud> getMeestEstudCollection() {
        return meestEstudCollection;
    }

    public void setMeestEstudCollection(Collection<MeestEstud> meestEstudCollection) {
        this.meestEstudCollection = meestEstudCollection;
    }

    @XmlTransient
    public Collection<MehorHorario> getMehorHorarioCollection() {
        return mehorHorarioCollection;
    }

    public void setMehorHorarioCollection(Collection<MehorHorario> mehorHorarioCollection) {
        this.mehorHorarioCollection = mehorHorarioCollection;
    }

    public MeasigAsigna getMeasigId() {
        return measigId;
    }

    public void setMeasigId(MeasigAsigna measigId) {
        this.measigId = measigId;
    }

    public MeperiPeriod getMeperiId() {
        return meperiId;
    }

    public void setMeperiId(MeperiPeriod meperiId) {
        this.meperiId = meperiId;
    }

    public PeperPerson getPeperId() {
        return peperId;
    }

    public void setPeperId(PeperPerson peperId) {
        this.peperId = peperId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (megrpId != null ? megrpId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MegrpGrupo)) {
            return false;
        }
        MegrpGrupo other = (MegrpGrupo) object;
        if ((this.megrpId == null && other.megrpId != null) || (this.megrpId != null && !this.megrpId.equals(other.megrpId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MegrpGrupo[ megrpId=" + megrpId + " ]";
    }
    
}
