/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.FepagoPago;
import ec.edu.monster.modelo.MecarrCarrera;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "meest_estud")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MeestEstud.findAll", query = "SELECT m FROM MeestEstud m"),
    @NamedQuery(name = "MeestEstud.findByMecarrId", query = "SELECT m FROM MeestEstud m WHERE m.meestEstudPK.mecarrId = :mecarrId"),
    @NamedQuery(name = "MeestEstud.findByMeestId", query = "SELECT m FROM MeestEstud m WHERE m.meestEstudPK.meestId = :meestId"),
    @NamedQuery(name = "MeestEstud.findByMeestNombre", query = "SELECT m FROM MeestEstud m WHERE m.meestNombre = :meestNombre"),
    @NamedQuery(name = "MeestEstud.findByMeestApellido", query = "SELECT m FROM MeestEstud m WHERE m.meestApellido = :meestApellido"),
    @NamedQuery(name = "MeestEstud.findByMeestCedula", query = "SELECT m FROM MeestEstud m WHERE m.meestCedula = :meestCedula"),
    @NamedQuery(name = "MeestEstud.findByMeestEmail", query = "SELECT m FROM MeestEstud m WHERE m.meestEmail = :meestEmail"),
    @NamedQuery(name = "MeestEstud.findByMeestFechana", query = "SELECT m FROM MeestEstud m WHERE m.meestFechana = :meestFechana"),
    @NamedQuery(name = "MeestEstud.findByMeestPromedio", query = "SELECT m FROM MeestEstud m WHERE m.meestPromedio = :meestPromedio")})
public class MeestEstud implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MeestEstudPK meestEstudPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "MEEST_NOMBRE")
    private String meestNombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "MEEST_APELLIDO")
    private String meestApellido;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "MEEST_CEDULA")
    private String meestCedula;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "MEEST_EMAIL")
    private String meestEmail;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEEST_FECHANA")
    @Temporal(TemporalType.DATE)
    private Date meestFechana;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEEST_PROMEDIO")
    private long meestPromedio;
    @JoinTable(name = "mr_meest_megrp", joinColumns = {
        @JoinColumn(name = "MECARR_ID", referencedColumnName = "MECARR_ID"),
        @JoinColumn(name = "MEEST_ID", referencedColumnName = "MEEST_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "MEGRP_ID", referencedColumnName = "MEGRP_ID")})
    @ManyToMany
    private Collection<MegrpGrupo> megrpGrupoCollection;
    @OneToMany(mappedBy = "meestEstud")
    private Collection<XeusuUsuar> xeusuUsuarCollection;
    @JoinColumn(name = "MECARR_ID", referencedColumnName = "MECARR_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private MecarrCarrera mecarrCarrera;
    @JoinColumn(name = "XEUSU_ID", referencedColumnName = "XEUSU_ID")
    @ManyToOne
    private XeusuUsuar xeusuId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meestEstud")
    private Collection<FepagoPago> fepagoPagoCollection;

    public MeestEstud() {
    }

    public MeestEstud(MeestEstudPK meestEstudPK) {
        this.meestEstudPK = meestEstudPK;
    }

    public MeestEstud(MeestEstudPK meestEstudPK, String meestNombre, String meestApellido, String meestCedula, String meestEmail, Date meestFechana, long meestPromedio) {
        this.meestEstudPK = meestEstudPK;
        this.meestNombre = meestNombre;
        this.meestApellido = meestApellido;
        this.meestCedula = meestCedula;
        this.meestEmail = meestEmail;
        this.meestFechana = meestFechana;
        this.meestPromedio = meestPromedio;
    }

    public MeestEstud(String mecarrId, String meestId) {
        this.meestEstudPK = new MeestEstudPK(mecarrId, meestId);
    }

    public MeestEstudPK getMeestEstudPK() {
        return meestEstudPK;
    }

    public void setMeestEstudPK(MeestEstudPK meestEstudPK) {
        this.meestEstudPK = meestEstudPK;
    }

    public String getMeestNombre() {
        return meestNombre;
    }

    public void setMeestNombre(String meestNombre) {
        this.meestNombre = meestNombre;
    }

    public String getMeestApellido() {
        return meestApellido;
    }

    public void setMeestApellido(String meestApellido) {
        this.meestApellido = meestApellido;
    }

    public String getMeestCedula() {
        return meestCedula;
    }

    public void setMeestCedula(String meestCedula) {
        this.meestCedula = meestCedula;
    }

    public String getMeestEmail() {
        return meestEmail;
    }

    public void setMeestEmail(String meestEmail) {
        this.meestEmail = meestEmail;
    }

    public Date getMeestFechana() {
        return meestFechana;
    }

    public void setMeestFechana(Date meestFechana) {
        this.meestFechana = meestFechana;
    }

    public long getMeestPromedio() {
        return meestPromedio;
    }

    public void setMeestPromedio(long meestPromedio) {
        this.meestPromedio = meestPromedio;
    }

    @XmlTransient
    public Collection<MegrpGrupo> getMegrpGrupoCollection() {
        return megrpGrupoCollection;
    }

    public void setMegrpGrupoCollection(Collection<MegrpGrupo> megrpGrupoCollection) {
        this.megrpGrupoCollection = megrpGrupoCollection;
    }

    @XmlTransient
    public Collection<XeusuUsuar> getXeusuUsuarCollection() {
        return xeusuUsuarCollection;
    }

    public void setXeusuUsuarCollection(Collection<XeusuUsuar> xeusuUsuarCollection) {
        this.xeusuUsuarCollection = xeusuUsuarCollection;
    }

    public MecarrCarrera getMecarrCarrera() {
        return mecarrCarrera;
    }

    public void setMecarrCarrera(MecarrCarrera mecarrCarrera) {
        this.mecarrCarrera = mecarrCarrera;
    }

    public XeusuUsuar getXeusuId() {
        return xeusuId;
    }

    public void setXeusuId(XeusuUsuar xeusuId) {
        this.xeusuId = xeusuId;
    }

    @XmlTransient
    public Collection<FepagoPago> getFepagoPagoCollection() {
        return fepagoPagoCollection;
    }

    public void setFepagoPagoCollection(Collection<FepagoPago> fepagoPagoCollection) {
        this.fepagoPagoCollection = fepagoPagoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meestEstudPK != null ? meestEstudPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeestEstud)) {
            return false;
        }
        MeestEstud other = (MeestEstud) object;
        if ((this.meestEstudPK == null && other.meestEstudPK != null) || (this.meestEstudPK != null && !this.meestEstudPK.equals(other.meestEstudPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MeestEstud[ meestEstudPK=" + meestEstudPK + " ]";
    }
    
}
