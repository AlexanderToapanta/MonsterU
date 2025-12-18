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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "xr_xerol_xeopc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "XrXerolXeopc.findAll", query = "SELECT x FROM XrXerolXeopc x"),
    @NamedQuery(name = "XrXerolXeopc.findByXerolId", query = "SELECT x FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xerolId = :xerolId"),
    @NamedQuery(name = "XrXerolXeopc.findByXeopcId", query = "SELECT x FROM XrXerolXeopc x WHERE x.xrXerolXeopcPK.xeopcId = :xeopcId"),
    @NamedQuery(name = "XrXerolXeopc.findByXropFechaAsig", query = "SELECT x FROM XrXerolXeopc x WHERE x.xropFechaAsig = :xropFechaAsig"),
    @NamedQuery(name = "XrXerolXeopc.findByXropFechaRetiro", query = "SELECT x FROM XrXerolXeopc x WHERE x.xropFechaRetiro = :xropFechaRetiro")})
public class XrXerolXeopc implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected XrXerolXeopcPK xrXerolXeopcPK;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "XROP_FECHA_ASIG")
    @Temporal(TemporalType.DATE)
    private Date xropFechaAsig;
    
    @Column(name = "XROP_FECHA_RETIRO")
    @Temporal(TemporalType.DATE)
    private Date xropFechaRetiro;
    
    @JoinColumn(name = "XEOPC_ID", referencedColumnName = "XEOPC_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private XeopcOpcion xeopcOpcion;
    
    @JoinColumn(name = "XEROL_ID", referencedColumnName = "XEROL_ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private XerolRol xerolRol;

    public XrXerolXeopc() {
    }

    public XrXerolXeopc(XrXerolXeopcPK xrXerolXeopcPK) {
        this.xrXerolXeopcPK = xrXerolXeopcPK;
    }

    public XrXerolXeopc(XrXerolXeopcPK xrXerolXeopcPK, Date xropFechaAsig) {
        this.xrXerolXeopcPK = xrXerolXeopcPK;
        this.xropFechaAsig = xropFechaAsig;
    }

    public XrXerolXeopc(String xerolId, String xeopcId) {
        this.xrXerolXeopcPK = new XrXerolXeopcPK(xerolId, xeopcId);
    }

    public XrXerolXeopc(String xerolId, String xeopcId, Date xropFechaAsig) {
        this.xrXerolXeopcPK = new XrXerolXeopcPK(xerolId, xeopcId);
        this.xropFechaAsig = xropFechaAsig;
    }

    public XrXerolXeopcPK getXrXerolXeopcPK() {
        return xrXerolXeopcPK;
    }

    public void setXrXerolXeopcPK(XrXerolXeopcPK xrXerolXeopcPK) {
        this.xrXerolXeopcPK = xrXerolXeopcPK;
    }

    public Date getXropFechaAsig() {
        return xropFechaAsig;
    }

    public void setXropFechaAsig(Date xropFechaAsig) {
        this.xropFechaAsig = xropFechaAsig;
    }

    public Date getXropFechaRetiro() {
        return xropFechaRetiro;
    }

    public void setXropFechaRetiro(Date xropFechaRetiro) {
        this.xropFechaRetiro = xropFechaRetiro;
    }

    public XeopcOpcion getXeopcOpcion() {
        return xeopcOpcion;
    }

    public void setXeopcOpcion(XeopcOpcion xeopcOpcion) {
        this.xeopcOpcion = xeopcOpcion;
    }

    public XerolRol getXerolRol() {
        return xerolRol;
    }

    public void setXerolRol(XerolRol xerolRol) {
        this.xerolRol = xerolRol;
    }

    // Métodos helper para acceder a los IDs directamente (como en MeestEstud)
    public String getXerolId() {
        return xrXerolXeopcPK != null ? xrXerolXeopcPK.getXerolId() : null;
    }

    public String getXeopcId() {
        return xrXerolXeopcPK != null ? xrXerolXeopcPK.getXeopcId() : null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (xrXerolXeopcPK != null ? xrXerolXeopcPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof XrXerolXeopc)) {
            return false;
        }
        XrXerolXeopc other = (XrXerolXeopc) object;
        if ((this.xrXerolXeopcPK == null && other.xrXerolXeopcPK != null) || (this.xrXerolXeopcPK != null && !this.xrXerolXeopcPK.equals(other.xrXerolXeopcPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.modelo.XrXerolXeopc[ xrXerolXeopcPK=" + xrXerolXeopcPK + " ]";
    }
}