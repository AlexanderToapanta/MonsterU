/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
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
@Table(name = "xeusu_usuar")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "XeusuUsuar.findAll", query = "SELECT x FROM XeusuUsuar x"),
    @NamedQuery(name = "XeusuUsuar.findByXeusuId", query = "SELECT x FROM XeusuUsuar x WHERE x.xeusuId = :xeusuId"),
    @NamedQuery(name = "XeusuUsuar.findByXeusuNombre", query = "SELECT x FROM XeusuUsuar x WHERE x.xeusuNombre = :xeusuNombre"),
    @NamedQuery(name = "XeusuUsuar.findByXeusuContra", query = "SELECT x FROM XeusuUsuar x WHERE x.xeusuContra = :xeusuContra"),
    @NamedQuery(name = "XeusuUsuar.findByXeusuEstado", query = "SELECT x FROM XeusuUsuar x WHERE x.xeusuEstado = :xeusuEstado")})
public class XeusuUsuar implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "XEUSU_ID")
    private String xeusuId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "XEUSU_NOMBRE")
    private String xeusuNombre;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "XEUSU_CONTRA")
    private String xeusuContra;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "XEUSU_ESTADO")
    private String xeusuEstado;
    
    // RELACIÓN CON ROL (UN USUARIO TIENE UN ROL)
    @JoinColumn(name = "XEROL_ID", referencedColumnName = "XEROL_ID")
    @ManyToOne
    private XerolRol xerolId;
    
    // RELACIÓN CON PERSONA (UN USUARIO PUEDE ESTAR ASOCIADO A UNA PERSONA)
    @JoinColumn(name = "PEPER_ID", referencedColumnName = "PEPER_ID")
    @ManyToOne
    private PeperPerson peperId;
    
    // RELACIÓN CON ESTUDIANTE (UN USUARIO PUEDE ESTAR ASOCIADO A UN ESTUDIANTE)
    @JoinColumns({
        @JoinColumn(name = "MECARR_ID", referencedColumnName = "MECARR_ID"),
        @JoinColumn(name = "MEEST_ID", referencedColumnName = "MEEST_ID")
    })
    @ManyToOne
    private MeestEstud meestEstud;
    
    // RELACIÓN ONE-TO-MANY CON AUDITORÍA (UN USUARIO TIENE MUCHOS LOGS DE AUDITORÍA)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "xeusuId")
    private Collection<XeaudAudlog> xeaudAudlogCollection;
    
    // RELACIÓN ONE-TO-MANY CON ESTUDIANTES (PARA LA RELACIÓN BIDIRECCIONAL)
    @OneToMany(mappedBy = "xeusuId")
    private Collection<MeestEstud> meestEstudCollection;

    public XeusuUsuar() {
    }

    public XeusuUsuar(String xeusuId) {
        this.xeusuId = xeusuId;
    }

    public XeusuUsuar(String xeusuId, String xeusuNombre, String xeusuContra, String xeusuEstado) {
        this.xeusuId = xeusuId;
        this.xeusuNombre = xeusuNombre;
        this.xeusuContra = xeusuContra;
        this.xeusuEstado = xeusuEstado;
    }

    public String getXeusuId() {
        return xeusuId;
    }

    public void setXeusuId(String xeusuId) {
        this.xeusuId = xeusuId;
    }

    public String getXeusuNombre() {
        return xeusuNombre;
    }

    public void setXeusuNombre(String xeusuNombre) {
        this.xeusuNombre = xeusuNombre;
    }

    public String getXeusuContra() {
        return xeusuContra;
    }

    public void setXeusuContra(String xeusuContra) {
        this.xeusuContra = xeusuContra;
    }

    public String getXeusuEstado() {
        return xeusuEstado;
    }

    public void setXeusuEstado(String xeusuEstado) {
        this.xeusuEstado = xeusuEstado;
    }

    public XerolRol getXerolId() {
        return xerolId;
    }

    public void setXerolId(XerolRol xerolId) {
        this.xerolId = xerolId;
    }

    public PeperPerson getPeperId() {
        return peperId;
    }

    public void setPeperId(PeperPerson peperId) {
        this.peperId = peperId;
    }

    public MeestEstud getMeestEstud() {
        return meestEstud;
    }

    public void setMeestEstud(MeestEstud meestEstud) {
        this.meestEstud = meestEstud;
    }

    @XmlTransient
    public Collection<XeaudAudlog> getXeaudAudlogCollection() {
        return xeaudAudlogCollection;
    }

    public void setXeaudAudlogCollection(Collection<XeaudAudlog> xeaudAudlogCollection) {
        this.xeaudAudlogCollection = xeaudAudlogCollection;
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
        hash += (xeusuId != null ? xeusuId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof XeusuUsuar)) {
            return false;
        }
        XeusuUsuar other = (XeusuUsuar) object;
        if ((this.xeusuId == null && other.xeusuId != null) || 
            (this.xeusuId != null && !this.xeusuId.equals(other.xeusuId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.modelo.XeusuUsuar[ xeusuId=" + xeusuId + " ]";
    }
}