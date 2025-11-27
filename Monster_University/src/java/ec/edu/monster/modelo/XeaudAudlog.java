/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
@Table(name = "xeaud_audlog")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "XeaudAudlog.findAll", query = "SELECT x FROM XeaudAudlog x"),
    @NamedQuery(name = "XeaudAudlog.findByIdauditoria", query = "SELECT x FROM XeaudAudlog x WHERE x.idauditoria = :idauditoria"),
    @NamedQuery(name = "XeaudAudlog.findByAccion", query = "SELECT x FROM XeaudAudlog x WHERE x.accion = :accion"),
    @NamedQuery(name = "XeaudAudlog.findByTablaafec", query = "SELECT x FROM XeaudAudlog x WHERE x.tablaafec = :tablaafec"),
    @NamedQuery(name = "XeaudAudlog.findByFilaid", query = "SELECT x FROM XeaudAudlog x WHERE x.filaid = :filaid"),
    @NamedQuery(name = "XeaudAudlog.findByFechahora", query = "SELECT x FROM XeaudAudlog x WHERE x.fechahora = :fechahora")})
public class XeaudAudlog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "IDAUDITORIA")
    private String idauditoria;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ACCION")
    private String accion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "TABLAAFEC")
    private String tablaafec;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FILAID")
    private int filaid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHAHORA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechahora;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "DETALLE")
    private String detalle;
    @JoinColumn(name = "XEUSU_ID", referencedColumnName = "XEUSU_ID")
    @ManyToOne(optional = false)
    private XeusuUsuar xeusuId;

    public XeaudAudlog() {
    }

    public XeaudAudlog(String idauditoria) {
        this.idauditoria = idauditoria;
    }

    public XeaudAudlog(String idauditoria, String accion, String tablaafec, int filaid, Date fechahora, String detalle) {
        this.idauditoria = idauditoria;
        this.accion = accion;
        this.tablaafec = tablaafec;
        this.filaid = filaid;
        this.fechahora = fechahora;
        this.detalle = detalle;
    }

    public String getIdauditoria() {
        return idauditoria;
    }

    public void setIdauditoria(String idauditoria) {
        this.idauditoria = idauditoria;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getTablaafec() {
        return tablaafec;
    }

    public void setTablaafec(String tablaafec) {
        this.tablaafec = tablaafec;
    }

    public int getFilaid() {
        return filaid;
    }

    public void setFilaid(int filaid) {
        this.filaid = filaid;
    }

    public Date getFechahora() {
        return fechahora;
    }

    public void setFechahora(Date fechahora) {
        this.fechahora = fechahora;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public XeusuUsuar getXeusuId() {
        return xeusuId;
    }

    public void setXeusuId(XeusuUsuar xeusuId) {
        this.xeusuId = xeusuId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idauditoria != null ? idauditoria.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof XeaudAudlog)) {
            return false;
        }
        XeaudAudlog other = (XeaudAudlog) object;
        if ((this.idauditoria == null && other.idauditoria != null) || (this.idauditoria != null && !this.idauditoria.equals(other.idauditoria))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.XeaudAudlog[ idauditoria=" + idauditoria + " ]";
    }
    
}
