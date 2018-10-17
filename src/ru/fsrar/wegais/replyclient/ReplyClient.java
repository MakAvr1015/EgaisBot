//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.replyclient;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.fsrar.wegais.clientref.OrgInfoEx;


/**
 * Ответ на запрос о клиентах
 * 
 * <p>Java class for ReplyClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplyClient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Clients">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Client" type="{http://fsrar.ru/WEGAIS/ClientRef}OrgInfoEx" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReplyClient", propOrder = {
    "clients"
})
public class ReplyClient {

    @XmlElement(name = "Clients", required = true)
    protected ReplyClient.Clients clients;

    /**
     * Gets the value of the clients property.
     * 
     * @return
     *     possible object is
     *     {@link ReplyClient.Clients }
     *     
     */
    public ReplyClient.Clients getClients() {
        return clients;
    }

    /**
     * Sets the value of the clients property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReplyClient.Clients }
     *     
     */
    public void setClients(ReplyClient.Clients value) {
        this.clients = value;
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
     *         &lt;element name="Client" type="{http://fsrar.ru/WEGAIS/ClientRef}OrgInfoEx" maxOccurs="unbounded" minOccurs="0"/>
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
        "client"
    })
    public static class Clients {

        @XmlElement(name = "Client")
        protected List<OrgInfoEx> client;

        /**
         * Gets the value of the client property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the client property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getClient().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OrgInfoEx }
         * 
         * 
         */
        public List<OrgInfoEx> getClient() {
            if (client == null) {
                client = new ArrayList<OrgInfoEx>();
            }
            return this.client;
        }

    }

}
