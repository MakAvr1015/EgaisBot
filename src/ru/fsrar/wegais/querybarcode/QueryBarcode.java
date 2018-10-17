//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.querybarcode;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Запрос по типу/серии/номеру ФСМ/АМ штрихового кода PDF-417, нанесенного на эту ФСМ/АМ
 * 
 * <p>Java class for QueryBarcode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryBarcode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QueryNumber" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString"/>
 *         &lt;element name="Date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="Marks">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Mark" type="{http://fsrar.ru/WEGAIS/QueryBarcode}MarkType" maxOccurs="unbounded"/>
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
@XmlType(name = "QueryBarcode", propOrder = {
    "queryNumber",
    "date",
    "marks"
})
public class QueryBarcode {

    @XmlElement(name = "QueryNumber", required = true)
    protected String queryNumber;
    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlElement(name = "Marks", required = true)
    protected QueryBarcode.Marks marks;

    /**
     * Gets the value of the queryNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryNumber() {
        return queryNumber;
    }

    /**
     * Sets the value of the queryNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryNumber(String value) {
        this.queryNumber = value;
    }

    /**
     * Gets the value of the date property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Sets the value of the date property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Gets the value of the marks property.
     * 
     * @return
     *     possible object is
     *     {@link QueryBarcode.Marks }
     *     
     */
    public QueryBarcode.Marks getMarks() {
        return marks;
    }

    /**
     * Sets the value of the marks property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryBarcode.Marks }
     *     
     */
    public void setMarks(QueryBarcode.Marks value) {
        this.marks = value;
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
     *         &lt;element name="Mark" type="{http://fsrar.ru/WEGAIS/QueryBarcode}MarkType" maxOccurs="unbounded"/>
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
        "mark"
    })
    public static class Marks {

        @XmlElement(name = "Mark", required = true)
        protected List<MarkType> mark;

        /**
         * Gets the value of the mark property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the mark property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMark().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MarkType }
         * 
         * 
         */
        public List<MarkType> getMark() {
            if (mark == null) {
                mark = new ArrayList<MarkType>();
            }
            return this.mark;
        }

    }

}