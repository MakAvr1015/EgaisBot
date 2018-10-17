//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.actinventoryinformf2reg;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Квитанция о регистрации по акту инвентаризации
 * 
 * <p>Java class for ActInventoryInformF2Reg complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActInventoryInformF2Reg">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="Header">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="Identity" type="{http://fsrar.ru/WEGAIS/Common}IdentityType" minOccurs="0"/>
 *                   &lt;element name="ActRegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *                   &lt;element name="Number" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Content">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Position" type="{http://fsrar.ru/WEGAIS/ActInventoryInformF2Reg}InformInvPositionType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActInventoryInformF2Reg", propOrder = {

})
public class ActInventoryInformF2Reg {

    @XmlElement(name = "Header", required = true)
    protected ActInventoryInformF2Reg.Header header;
    @XmlElement(name = "Content", required = true)
    protected ActInventoryInformF2Reg.Content content;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link ActInventoryInformF2Reg.Header }
     *     
     */
    public ActInventoryInformF2Reg.Header getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActInventoryInformF2Reg.Header }
     *     
     */
    public void setHeader(ActInventoryInformF2Reg.Header value) {
        this.header = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link ActInventoryInformF2Reg.Content }
     *     
     */
    public ActInventoryInformF2Reg.Content getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActInventoryInformF2Reg.Content }
     *     
     */
    public void setContent(ActInventoryInformF2Reg.Content value) {
        this.content = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Position" type="{http://fsrar.ru/WEGAIS/ActInventoryInformF2Reg}InformInvPositionType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "position"
    })
    public static class Content {

        @XmlElement(name = "Position", required = true)
        protected List<InformInvPositionType> position;

        /**
         * Gets the value of the position property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the position property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPosition().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link InformInvPositionType }
         * 
         * 
         */
        public List<InformInvPositionType> getPosition() {
            if (position == null) {
                position = new ArrayList<InformInvPositionType>();
            }
            return this.position;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element name="Identity" type="{http://fsrar.ru/WEGAIS/Common}IdentityType" minOccurs="0"/>
     *         &lt;element name="ActRegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
     *         &lt;element name="Number" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Header {

        @XmlElement(name = "Identity")
        protected String identity;
        @XmlElement(name = "ActRegId", required = true)
        protected String actRegId;
        @XmlElement(name = "Number", required = true)
        protected String number;

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
         * Gets the value of the actRegId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getActRegId() {
            return actRegId;
        }

        /**
         * Sets the value of the actRegId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setActRegId(String value) {
            this.actRegId = value;
        }

        /**
         * Gets the value of the number property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNumber() {
            return number;
        }

        /**
         * Sets the value of the number property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNumber(String value) {
            this.number = value;
        }

    }

}
