//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.actinventoryinformf2reg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.fsrar.wegais.productref_v2.MarkInfoType;


/**
 * <p>Java class for InformInvF2RegItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InformInvF2RegItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Identity" type="{http://fsrar.ru/WEGAIS/Common}IdentityType"/>
 *         &lt;element name="F2RegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="MarkInfo" type="{http://fsrar.ru/WEGAIS/ProductRef_v2}MarkInfoType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InformInvF2RegItem", propOrder = {

})
public class InformInvF2RegItem {

    @XmlElement(name = "Identity", required = true)
    protected String identity;
    @XmlElement(name = "F2RegId", required = true)
    protected String f2RegId;
    @XmlElement(name = "MarkInfo")
    protected MarkInfoType markInfo;

    /**
     * Gets the value of the identity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentity(String value) {
        this.identity = value;
    }

    /**
     * Gets the value of the f2RegId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF2RegId() {
        return f2RegId;
    }

    /**
     * Sets the value of the f2RegId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF2RegId(String value) {
        this.f2RegId = value;
    }

    /**
     * Gets the value of the markInfo property.
     * 
     * @return
     *     possible object is
     *     {@link MarkInfoType }
     *     
     */
    public MarkInfoType getMarkInfo() {
        return markInfo;
    }

    /**
     * Sets the value of the markInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MarkInfoType }
     *     
     */
    public void setMarkInfo(MarkInfoType value) {
        this.markInfo = value;
    }

}
