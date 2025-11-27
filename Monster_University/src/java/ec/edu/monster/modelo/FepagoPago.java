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
import javax.persistence.JoinColumns;
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
@Table(name = "fepago_pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FepagoPago.findAll", query = "SELECT f FROM FepagoPago f"),
    @NamedQuery(name = "FepagoPago.findByFepagoId", query = "SELECT f FROM FepagoPago f WHERE f.fepagoId = :fepagoId"),
    @NamedQuery(name = "FepagoPago.findByFepagoMonto", query = "SELECT f FROM FepagoPago f WHERE f.fepagoMonto = :fepagoMonto"),
    @NamedQuery(name = "FepagoPago.findByFepagoFecha", query = "SELECT f FROM FepagoPago f WHERE f.fepagoFecha = :fepagoFecha"),
    @NamedQuery(name = "FepagoPago.findByFepagoRefe", query = "SELECT f FROM FepagoPago f WHERE f.fepagoRefe = :fepagoRefe"),
    @NamedQuery(name = "FepagoPago.findByFepagoEstado", query = "SELECT f FROM FepagoPago f WHERE f.fepagoEstado = :fepagoEstado")})
public class FepagoPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "FEPAGO_ID")
    private String fepagoId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FEPAGO_MONTO")
    private long fepagoMonto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FEPAGO_FECHA")
    @Temporal(TemporalType.DATE)
    private Date fepagoFecha;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "FEPAGO_REFE")
    private String fepagoRefe;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "FEPAGO_ESTADO")
    private String fepagoEstado;
    @JoinColumns({
        @JoinColumn(name = "MECARR_ID", referencedColumnName = "MECARR_ID"),
        @JoinColumn(name = "MEEST_ID", referencedColumnName = "MEEST_ID")})
    @ManyToOne(optional = false)
    private MeestEstud meestEstud;

    public FepagoPago() {
    }

    public FepagoPago(String fepagoId) {
        this.fepagoId = fepagoId;
    }

    public FepagoPago(String fepagoId, long fepagoMonto, Date fepagoFecha, String fepagoRefe, String fepagoEstado) {
        this.fepagoId = fepagoId;
        this.fepagoMonto = fepagoMonto;
        this.fepagoFecha = fepagoFecha;
        this.fepagoRefe = fepagoRefe;
        this.fepagoEstado = fepagoEstado;
    }

    public String getFepagoId() {
        return fepagoId;
    }

    public void setFepagoId(String fepagoId) {
        this.fepagoId = fepagoId;
    }

    public long getFepagoMonto() {
        return fepagoMonto;
    }

    public void setFepagoMonto(long fepagoMonto) {
        this.fepagoMonto = fepagoMonto;
    }

    public Date getFepagoFecha() {
        return fepagoFecha;
    }

    public void setFepagoFecha(Date fepagoFecha) {
        this.fepagoFecha = fepagoFecha;
    }

    public String getFepagoRefe() {
        return fepagoRefe;
    }

    public void setFepagoRefe(String fepagoRefe) {
        this.fepagoRefe = fepagoRefe;
    }

    public String getFepagoEstado() {
        return fepagoEstado;
    }

    public void setFepagoEstado(String fepagoEstado) {
        this.fepagoEstado = fepagoEstado;
    }

    public MeestEstud getMeestEstud() {
        return meestEstud;
    }

    public void setMeestEstud(MeestEstud meestEstud) {
        this.meestEstud = meestEstud;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fepagoId != null ? fepagoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FepagoPago)) {
            return false;
        }
        FepagoPago other = (FepagoPago) object;
        if ((this.fepagoId == null && other.fepagoId != null) || (this.fepagoId != null && !this.fepagoId.equals(other.fepagoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.FepagoPago[ fepagoId=" + fepagoId + " ]";
    }
    
}
