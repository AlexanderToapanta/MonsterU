/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "mehor_horario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MehorHorario.findAll", query = "SELECT m FROM MehorHorario m"),
    @NamedQuery(name = "MehorHorario.findByMegrpId", query = "SELECT m FROM MehorHorario m WHERE m.mehorHorarioPK.megrpId = :megrpId"),
    @NamedQuery(name = "MehorHorario.findByMehorId", query = "SELECT m FROM MehorHorario m WHERE m.mehorHorarioPK.mehorId = :mehorId"),
    @NamedQuery(name = "MehorHorario.findByMehorDia", query = "SELECT m FROM MehorHorario m WHERE m.mehorDia = :mehorDia"),
    @NamedQuery(name = "MehorHorario.findByMehorFechaI", query = "SELECT m FROM MehorHorario m WHERE m.mehorFechaI = :mehorFechaI"),
    @NamedQuery(name = "MehorHorario.findByMehorFechaF", query = "SELECT m FROM MehorHorario m WHERE m.mehorFechaF = :mehorFechaF")})
public class MehorHorario implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MehorHorarioPK mehorHorarioPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "MEHOR_DIA")
    private String mehorDia;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEHOR_FECHA_I")
    @Temporal(TemporalType.TIME)
    private Date mehorFechaI;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEHOR_FECHA_F")
    @Temporal(TemporalType.TIME)
    private Date mehorFechaF;
    @JoinColumn(name = "MEGRP_ID", referencedColumnName = "MEGRP_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private MegrpGrupo megrpGrupo;

    public MehorHorario() {
    }

    public MehorHorario(MehorHorarioPK mehorHorarioPK) {
        this.mehorHorarioPK = mehorHorarioPK;
    }

    public MehorHorario(MehorHorarioPK mehorHorarioPK, String mehorDia, Date mehorFechaI, Date mehorFechaF) {
        this.mehorHorarioPK = mehorHorarioPK;
        this.mehorDia = mehorDia;
        this.mehorFechaI = mehorFechaI;
        this.mehorFechaF = mehorFechaF;
    }

    public MehorHorario(String megrpId, String mehorId) {
        this.mehorHorarioPK = new MehorHorarioPK(megrpId, mehorId);
    }

    public MehorHorarioPK getMehorHorarioPK() {
        return mehorHorarioPK;
    }

    public void setMehorHorarioPK(MehorHorarioPK mehorHorarioPK) {
        this.mehorHorarioPK = mehorHorarioPK;
    }

    public String getMehorDia() {
        return mehorDia;
    }

    public void setMehorDia(String mehorDia) {
        this.mehorDia = mehorDia;
    }

    public Date getMehorFechaI() {
        return mehorFechaI;
    }

    public void setMehorFechaI(Date mehorFechaI) {
        this.mehorFechaI = mehorFechaI;
    }

    public Date getMehorFechaF() {
        return mehorFechaF;
    }

    public void setMehorFechaF(Date mehorFechaF) {
        this.mehorFechaF = mehorFechaF;
    }

    public MegrpGrupo getMegrpGrupo() {
        return megrpGrupo;
    }

    public void setMegrpGrupo(MegrpGrupo megrpGrupo) {
        this.megrpGrupo = megrpGrupo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mehorHorarioPK != null ? mehorHorarioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MehorHorario)) {
            return false;
        }
        MehorHorario other = (MehorHorario) object;
        if ((this.mehorHorarioPK == null && other.mehorHorarioPK != null) || (this.mehorHorarioPK != null && !this.mehorHorarioPK.equals(other.mehorHorarioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MehorHorario[ mehorHorarioPK=" + mehorHorarioPK + " ]";
    }
    
}
