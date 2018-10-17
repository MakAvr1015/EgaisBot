//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.replyform1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusReplyV2;
import ru.fsrar.wegais.productref_v2.MarkInfoType;
import ru.fsrar.wegais.productref_v2.ProductInfoReplyV2;


/**
 * Ответ на запрос о форме 1
 * 
 * <p>Java class for ReplyForm1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReplyForm1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="InformF1RegId" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="OriginalClient" type="{http://fsrar.ru/WEGAIS/ClientRef_v2}OrgInfoRusReply_v2"/>
 *         &lt;element name="OriginalDocNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="OriginalDocDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime"/>
 *         &lt;element name="Product" type="{http://fsrar.ru/WEGAIS/ProductRef_v2}ProductInfoReply_v2"/>
 *         &lt;element name="BottlingDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime" minOccurs="0"/>
 *         &lt;element name="Quantity" type="{http://fsrar.ru/WEGAIS/Common}PositiveDecimalType"/>
 *         &lt;element name="EGAISNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="EGAISDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime"/>
 *         &lt;element name="GTDNUMBER" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50" minOccurs="0"/>
 *         &lt;element name="GTDDate" type="{http://fsrar.ru/WEGAIS/Common}DateNoTime" minOccurs="0"/>
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
@XmlType(name = "ReplyForm1", propOrder = {

})
public class ReplyForm1 {

    @XmlElement(name = "InformF1RegId", required = true)
    protected String informF1RegId;
    @XmlElement(name = "OriginalClient", required = true)
    protected OrgInfoRusReplyV2 originalClient;
    @XmlElement(name = "OriginalDocNumber", required = true)
    protected String originalDocNumber;
    @XmlElement(name = "OriginalDocDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar originalDocDate;
    @XmlElement(name = "Product", required = true)
    protected ProductInfoReplyV2 product;
    @XmlElement(name = "BottlingDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar bottlingDate;
    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;
    @XmlElement(name = "EGAISNumber", required = true)
    protected String egaisNumber;
    @XmlElement(name = "EGAISDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar egaisDate;
    @XmlElement(name = "GTDNUMBER")
    protected String gtdnumber;
    @XmlElement(name = "GTDDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar gtdDate;
    @XmlElement(name = "MarkInfo")
    protected MarkInfoType markInfo;

    /**
     * Gets the value of the informF1RegId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInformF1RegId() {
        return informF1RegId;
    }

    /**
     * Sets the value of the informF1RegId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInformF1RegId(String value) {
        this.informF1RegId = value;
    }

    /**
     * Gets the value of the originalClient property.
     * 
     * @return
     *     possible object is
     *     {@link OrgInfoRusReplyV2 }
     *     
     */
    public OrgInfoRusReplyV2 getOriginalClient() {
        return originalClient;
    }

    /**
     * Sets the value of the originalClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrgInfoRusReplyV2 }
     *     
     */
    public void setOriginalClient(OrgInfoRusReplyV2 value) {
        this.originalClient = value;
    }

    /**
     * Gets the value of the originalDocNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginalDocNumber() {
        return originalDocNumber;
    }

    /**
     * Sets the value of the originalDocNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginalDocNumber(String value) {
        this.originalDocNumber = value;
    }

    /**
     * Gets the value of the originalDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOriginalDocDate() {
        return originalDocDate;
    }

    /**
     * Sets the value of the originalDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOriginalDocDate(XMLGregorianCalendar value) {
        this.originalDocDate = value;
    }

    /**
     * Gets the value of the product property.
     * 
     * @return
     *     possible object is
     *     {@link ProductInfoReplyV2 }
     *     
     */
    public ProductInfoReplyV2 getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductInfoReplyV2 }
     *     
     */
    public void setProduct(ProductInfoReplyV2 value) {
        this.product = value;
    }

    /**
     * Gets the value of the bottlingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBottlingDate() {
        return bottlingDate;
    }

    /**
     * Sets the value of the bottlingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBottlingDate(XMLGregorianCalendar value) {
        this.bottlingDate = value;
    }

    /**
     * Gets the value of the quantity property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantity(BigDecimal value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the egaisNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEGAISNumber() {
        return egaisNumber;
    }

    /**
     * Sets the value of the egaisNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEGAISNumber(String value) {
        this.egaisNumber = value;
    }

    /**
     * Gets the value of the egaisDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEGAISDate() {
        return egaisDate;
    }

    /**
     * Sets the value of the egaisDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEGAISDate(XMLGregorianCalendar value) {
        this.egaisDate = value;
    }

    /**
     * Gets the value of the gtdnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGTDNUMBER() {
        return gtdnumber;
    }

    /**
     * Sets the value of the gtdnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGTDNUMBER(String value) {
        this.gtdnumber = value;
    }

    /**
     * Gets the value of the gtdDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGTDDate() {
        return gtdDate;
    }

    /**
     * Sets the value of the gtdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGTDDate(XMLGregorianCalendar value) {
        this.gtdDate = value;
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
