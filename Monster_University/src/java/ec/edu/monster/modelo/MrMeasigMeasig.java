/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import ec.edu.monster.modelo.MeasigAsigna;
import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "mr_measig_measig")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MrMeasigMeasig.findAll", query = "SELECT m FROM MrMeasigMeasig m"),
    @NamedQuery(name = "MrMeasigMeasig.findByMeasigId", query = "SELECT m FROM MrMeasigMeasig m WHERE m.mrMeasigMeasigPK.measigId = :measigId"),
    @NamedQuery(name = "MrMeasigMeasig.findByMeaMeasigId", query = "SELECT m FROM MrMeasigMeasig m WHERE m.mrMeasigMeasigPK.meaMeasigId = :meaMeasigId")})
public class MrMeasigMeasig implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MrMeasigMeasigPK mrMeasigMeasigPK;
    @JoinColumn(name = "MEASIG_ID", referencedColumnName = "MEASIG_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private MeasigAsigna measigAsigna;

    public MrMeasigMeasig() {
    }

    public MrMeasigMeasig(MrMeasigMeasigPK mrMeasigMeasigPK) {
        this.mrMeasigMeasigPK = mrMeasigMeasigPK;
    }

    public MrMeasigMeasig(String measigId, String meaMeasigId) {
        this.mrMeasigMeasigPK = new MrMeasigMeasigPK(measigId, meaMeasigId);
    }

    public MrMeasigMeasigPK getMrMeasigMeasigPK() {
        return mrMeasigMeasigPK;
    }

    public void setMrMeasigMeasigPK(MrMeasigMeasigPK mrMeasigMeasigPK) {
        this.mrMeasigMeasigPK = mrMeasigMeasigPK;
    }

    public MeasigAsigna getMeasigAsigna() {
        return measigAsigna;
    }

    public void setMeasigAsigna(MeasigAsigna measigAsigna) {
        this.measigAsigna = measigAsigna;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mrMeasigMeasigPK != null ? mrMeasigMeasigPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MrMeasigMeasig)) {
            return false;
        }
        MrMeasigMeasig other = (MrMeasigMeasig) object;
        if ((this.mrMeasigMeasigPK == null && other.mrMeasigMeasigPK != null) || (this.mrMeasigMeasigPK != null && !this.mrMeasigMeasigPK.equals(other.mrMeasigMeasigPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MrMeasigMeasig[ mrMeasigMeasigPK=" + mrMeasigMeasigPK + " ]";
    }
    
}
