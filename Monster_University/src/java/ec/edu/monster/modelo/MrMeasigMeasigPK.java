/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Usuario
 */
@Embeddable
public class MrMeasigMeasigPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEASIG_ID")
    private String measigId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEA_MEASIG_ID")
    private String meaMeasigId;

    public MrMeasigMeasigPK() {
    }

    public MrMeasigMeasigPK(String measigId, String meaMeasigId) {
        this.measigId = measigId;
        this.meaMeasigId = meaMeasigId;
    }

    public String getMeasigId() {
        return measigId;
    }

    public void setMeasigId(String measigId) {
        this.measigId = measigId;
    }

    public String getMeaMeasigId() {
        return meaMeasigId;
    }

    public void setMeaMeasigId(String meaMeasigId) {
        this.meaMeasigId = meaMeasigId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (measigId != null ? measigId.hashCode() : 0);
        hash += (meaMeasigId != null ? meaMeasigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MrMeasigMeasigPK)) {
            return false;
        }
        MrMeasigMeasigPK other = (MrMeasigMeasigPK) object;
        if ((this.measigId == null && other.measigId != null) || (this.measigId != null && !this.measigId.equals(other.measigId))) {
            return false;
        }
        if ((this.meaMeasigId == null && other.meaMeasigId != null) || (this.meaMeasigId != null && !this.meaMeasigId.equals(other.meaMeasigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MrMeasigMeasigPK[ measigId=" + measigId + ", meaMeasigId=" + meaMeasigId + " ]";
    }
    
}
