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
public class MeestEstudPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MECARR_ID")
    private String mecarrId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "MEEST_ID")
    private String meestId;

    public MeestEstudPK() {
    }

    public MeestEstudPK(String mecarrId, String meestId) {
        this.mecarrId = mecarrId;
        this.meestId = meestId;
    }

    public String getMecarrId() {
        return mecarrId;
    }

    public void setMecarrId(String mecarrId) {
        this.mecarrId = mecarrId;
    }

    public String getMeestId() {
        return meestId;
    }

    public void setMeestId(String meestId) {
        this.meestId = meestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (mecarrId != null ? mecarrId.hashCode() : 0);
        hash += (meestId != null ? meestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeestEstudPK)) {
            return false;
        }
        MeestEstudPK other = (MeestEstudPK) object;
        if ((this.mecarrId == null && other.mecarrId != null) || (this.mecarrId != null && !this.mecarrId.equals(other.mecarrId))) {
            return false;
        }
        if ((this.meestId == null && other.meestId != null) || (this.meestId != null && !this.meestId.equals(other.meestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.MeestEstudPK[ mecarrId=" + mecarrId + ", meestId=" + meestId + " ]";
    }
    
}
