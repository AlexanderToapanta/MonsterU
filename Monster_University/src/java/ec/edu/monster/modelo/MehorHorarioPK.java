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
public class MehorHorarioPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEGRP_ID")
    private String megrpId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEHOR_ID")
    private String mehorId;

    public MehorHorarioPK() {
    }

    public MehorHorarioPK(String megrpId, String mehorId) {
        this.megrpId = megrpId;
        this.mehorId = mehorId;
    }

    public String getMegrpId() {
        return megrpId;
    }

    public void setMegrpId(String megrpId) {
        this.megrpId = megrpId;
    }

    public String getMehorId() {
        return mehorId;
    }

    public void setMehorId(String mehorId) {
        this.mehorId = mehorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (megrpId != null ? megrpId.hashCode() : 0);
        hash += (mehorId != null ? mehorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MehorHorarioPK)) {
            return false;
        }
        MehorHorarioPK other = (MehorHorarioPK) object;
        if ((this.megrpId == null && other.megrpId != null) || (this.megrpId != null && !this.megrpId.equals(other.megrpId))) {
            return false;
        }
        if ((this.mehorId == null && other.mehorId != null) || (this.mehorId != null && !this.mehorId.equals(other.mehorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MehorHorarioPK[ megrpId=" + megrpId + ", mehorId=" + mehorId + " ]";
    }
    
}
