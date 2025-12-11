/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelo;

import ec.edu.monster.modelo.XeusuUsuar;
import ec.edu.monster.modelo.PesexSexo;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "peper_person")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PeperPerson.findAll", query = "SELECT p FROM PeperPerson p"),
    @NamedQuery(name = "PeperPerson.findByPeperId", query = "SELECT p FROM PeperPerson p WHERE p.peperId = :peperId"),
    @NamedQuery(name = "PeperPerson.findByPeperNombre", query = "SELECT p FROM PeperPerson p WHERE p.peperNombre = :peperNombre"),
    @NamedQuery(name = "PeperPerson.findByPeperApellido", query = "SELECT p FROM PeperPerson p WHERE p.peperApellido = :peperApellido"),
    @NamedQuery(name = "PeperPerson.findByPeperEmail", query = "SELECT p FROM PeperPerson p WHERE p.peperEmail = :peperEmail"),
    @NamedQuery(name = "PeperPerson.findByPeperCedula", query = "SELECT p FROM PeperPerson p WHERE p.peperCedula = :peperCedula"),
    @NamedQuery(name = "PeperPerson.findByPeperCelular", query = "SELECT p FROM PeperPerson p WHERE p.peperCelular = :peperCelular"),
    @NamedQuery(name = "PeperPerson.findByPeperTipo", query = "SELECT p FROM PeperPerson p WHERE p.peperTipo = :peperTipo"),
    @NamedQuery(name = "PeperPerson.findByPepeperFechIngr", query = "SELECT p FROM PeperPerson p WHERE p.pepeperFechIngr = :pepeperFechIngr"),
    @NamedQuery(name = "PeperPerson.findByPepeperFecNa", query = "SELECT p FROM PeperPerson p WHERE p.pepeperFecNa = :pepeperFecNa"),
    @NamedQuery(name = "PeperPerson.findByPepeperImag", query = "SELECT p FROM PeperPerson p WHERE p.pepeperImag = :pepeperImag")})
public class PeperPerson implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "PEPER_ID")
    private String peperId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "PEPER_NOMBRE")
    private String peperNombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "PEPER_APELLIDO")
    private String peperApellido;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEPER_EMAIL")
    private String peperEmail;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "PEPER_CEDULA")
    private String peperCedula;
    @Size(max = 15)
    @Column(name = "PEPER_CELULAR")
    private String peperCelular;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEPER_TIPO")
    private String peperTipo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PEPEPER_FECH_INGR")
    @Temporal(TemporalType.DATE)
    private Date pepeperFechIngr;
    @Column(name = "PEPEPER_FEC_NA")
    @Temporal(TemporalType.DATE)
    private Date pepeperFecNa;
    @Size(max = 500)
    @Column(name = "PEPEPER_IMAG")
    private String pepeperImag;
    @JoinColumn(name = "PEESC_ID", referencedColumnName = "PEESC_ID")
    @ManyToOne
    private PeescEstciv peescId;
    @JoinColumn(name = "PESEX_ID", referencedColumnName = "PESEX_ID")
    @ManyToOne(optional = false)
    private PesexSexo pesexId;
    @JoinColumn(name = "XEUSU_ID", referencedColumnName = "XEUSU_ID")
    @ManyToOne
    private XeusuUsuar xeusuId;
    @OneToMany(mappedBy = "peperId")
    private Collection<XeusuUsuar> xeusuUsuarCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "peperId")
    private Collection<MegrpGrupo> megrpGrupoCollection;

    public PeperPerson() {
    }

    public PeperPerson(String peperId) {
        this.peperId = peperId;
    }

    public PeperPerson(String peperId, String peperNombre, String peperApellido, String peperEmail, String peperCedula, String peperTipo, Date pepeperFechIngr) {
        this.peperId = peperId;
        this.peperNombre = peperNombre;
        this.peperApellido = peperApellido;
        this.peperEmail = peperEmail;
        this.peperCedula = peperCedula;
        this.peperTipo = peperTipo;
        this.pepeperFechIngr = pepeperFechIngr;
    }

    public String getPeperId() {
        return peperId;
    }

    public void setPeperId(String peperId) {
        this.peperId = peperId;
    }

    public String getPeperNombre() {
        return peperNombre;
    }

    public void setPeperNombre(String peperNombre) {
        this.peperNombre = peperNombre;
    }

    public String getPeperApellido() {
        return peperApellido;
    }

    public void setPeperApellido(String peperApellido) {
        this.peperApellido = peperApellido;
    }

    public String getPeperEmail() {
        return peperEmail;
    }

    public void setPeperEmail(String peperEmail) {
        this.peperEmail = peperEmail;
    }

    public String getPeperCedula() {
        return peperCedula;
    }

    public void setPeperCedula(String peperCedula) {
        this.peperCedula = peperCedula;
    }

    public String getPeperCelular() {
        return peperCelular;
    }

    public void setPeperCelular(String peperCelular) {
        this.peperCelular = peperCelular;
    }

    public String getPeperTipo() {
        return peperTipo;
    }

    public void setPeperTipo(String peperTipo) {
        this.peperTipo = peperTipo;
    }

    public Date getPepeperFechIngr() {
        return pepeperFechIngr;
    }

    public void setPepeperFechIngr(Date pepeperFechIngr) {
        this.pepeperFechIngr = pepeperFechIngr;
    }

    public Date getPepeperFecNa() {
        return pepeperFecNa;
    }

    public void setPepeperFecNa(Date pepeperFecNa) {
        this.pepeperFecNa = pepeperFecNa;
    }

    public String getPepeperImag() {
        return pepeperImag;
    }

    public void setPepeperImag(String pepeperImag) {
        this.pepeperImag = pepeperImag;
    }

    public PeescEstciv getPeescId() {
        return peescId;
    }

    public void setPeescId(PeescEstciv peescId) {
        this.peescId = peescId;
    }

    public PesexSexo getPesexId() {
        return pesexId;
    }

    public void setPesexId(PesexSexo pesexId) {
        this.pesexId = pesexId;
    }

    public XeusuUsuar getXeusuId() {
        return xeusuId;
    }

    public void setXeusuId(XeusuUsuar xeusuId) {
        this.xeusuId = xeusuId;
    }

    @XmlTransient
    public Collection<XeusuUsuar> getXeusuUsuarCollection() {
        return xeusuUsuarCollection;
    }

    public void setXeusuUsuarCollection(Collection<XeusuUsuar> xeusuUsuarCollection) {
        this.xeusuUsuarCollection = xeusuUsuarCollection;
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
        hash += (peperId != null ? peperId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PeperPerson)) {
            return false;
        }
        PeperPerson other = (PeperPerson) object;
        if ((this.peperId == null && other.peperId != null) || (this.peperId != null && !this.peperId.equals(other.peperId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ec.edu.monster.controlador.PeperPerson[ peperId=" + peperId + " ]";
    }
    
}