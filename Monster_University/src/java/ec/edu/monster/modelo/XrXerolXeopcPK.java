package ec.edu.monster.modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class XrXerolXeopcPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "XEROL_ID")
    private String xerolId;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "XEOPC_ID")
    private String xeopcId;

    public XrXerolXeopcPK() {
    }

    public XrXerolXeopcPK(String xerolId, String xeopcId) {
        this.xerolId = xerolId;
        this.xeopcId = xeopcId;
    }

    public String getXerolId() {
        return xerolId;
    }

    public void setXerolId(String xerolId) {
        this.xerolId = xerolId;
    }

    public String getXeopcId() {
        return xeopcId;
    }

    public void setXeopcId(String xeopcId) {
        this.xeopcId = xeopcId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (xerolId != null ? xerolId.hashCode() : 0);
        hash += (xeopcId != null ? xeopcId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof XrXerolXeopcPK)) {
            return false;
        }
        XrXerolXeopcPK other = (XrXerolXeopcPK) object;
        if ((this.xerolId == null && other.xerolId != null) || (this.xerolId != null && !this.xerolId.equals(other.xerolId))) {
            return false;
        }
        if ((this.xeopcId == null && other.xeopcId != null) || (this.xeopcId != null && !this.xeopcId.equals(other.xeopcId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.modelo.XrXerolXeopcPK[ xerolId=" + xerolId + ", xeopcId=" + xeopcId + " ]";
    }
}