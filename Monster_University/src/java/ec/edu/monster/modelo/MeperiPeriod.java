/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "meperi_period")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MeperiPeriod.findAll", query = "SELECT m FROM MeperiPeriod m"),
    @NamedQuery(name = "MeperiPeriod.findByMeperiId", query = "SELECT m FROM MeperiPeriod m WHERE m.meperiId = :meperiId"),
    @NamedQuery(name = "MeperiPeriod.findByMeperiNombre", query = "SELECT m FROM MeperiPeriod m WHERE m.meperiNombre = :meperiNombre"),
    @NamedQuery(name = "MeperiPeriod.findByMeperiFechaini", query = "SELECT m FROM MeperiPeriod m WHERE m.meperiFechaini = :meperiFechaini"),
    @NamedQuery(name = "MeperiPeriod.findByMeperiFechafin", query = "SELECT m FROM MeperiPeriod m WHERE m.meperiFechafin = :meperiFechafin"),
    @NamedQuery(name = "MeperiPeriod.findByMeperiActivo", query = "SELECT m FROM MeperiPeriod m WHERE m.meperiActivo = :meperiActivo")})
public class MeperiPeriod implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEPERI_ID")
    private String meperiId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "MEPERI_NOMBRE")
    private String meperiNombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEPERI_FECHAINI")
    @Temporal(TemporalType.DATE)
    private Date meperiFechaini;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEPERI_FECHAFIN")
    @Temporal(TemporalType.DATE)
    private Date meperiFechafin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEPERI_ACTIVO")
    private boolean meperiActivo;
    @OneToMany(mappedBy = "meperiId")
    private Collection<MegrpGrupo> megrpGrupoCollection;

    public MeperiPeriod() {
    }

    public MeperiPeriod(String meperiId) {
        this.meperiId = meperiId;
    }

    public MeperiPeriod(String meperiId, String meperiNombre, Date meperiFechaini, Date meperiFechafin, boolean meperiActivo) {
        this.meperiId = meperiId;
        this.meperiNombre = meperiNombre;
        this.meperiFechaini = meperiFechaini;
        this.meperiFechafin = meperiFechafin;
        this.meperiActivo = meperiActivo;
    }

    public String getMeperiId() {
        return meperiId;
    }

    public void setMeperiId(String meperiId) {
        this.meperiId = meperiId;
    }

    public String getMeperiNombre() {
        return meperiNombre;
    }

    public void setMeperiNombre(String meperiNombre) {
        this.meperiNombre = meperiNombre;
    }

    public Date getMeperiFechaini() {
        return meperiFechaini;
    }

    public void setMeperiFechaini(Date meperiFechaini) {
        this.meperiFechaini = meperiFechaini;
    }

    public Date getMeperiFechafin() {
        return meperiFechafin;
    }

    public void setMeperiFechafin(Date meperiFechafin) {
        this.meperiFechafin = meperiFechafin;
    }

    public boolean getMeperiActivo() {
        return meperiActivo;
    }

    public void setMeperiActivo(boolean meperiActivo) {
        this.meperiActivo = meperiActivo;
    }

    @XmlTransient
    public Collection<MegrpGrupo> getMegrpGrupoCollection() {
        return megrpGrupoCollection;
    }

    public void setMegrpGrupoCollection(Collection<MegrpGrupo> megrpGrupoCollection) {
        this.megrpGrupoCollection = megrpGrupoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meperiId != null ? meperiId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeperiPeriod)) {
            return false;
        }
        MeperiPeriod other = (MeperiPeriod) object;
        if ((this.meperiId == null && other.meperiId != null) || (this.meperiId != null && !this.meperiId.equals(other.meperiId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MeperiPeriod[ meperiId=" + meperiId + " ]";
    }
    
}
