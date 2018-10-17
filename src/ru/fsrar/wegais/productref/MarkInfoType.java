//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.03.04 at 04:05:40 PM KRAT 
//


package ru.fsrar.wegais.productref;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Информация о марках
 * 
 * <p>Java class for MarkInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MarkInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://fsrar.ru/WEGAIS/Common}NoEmptyString50"/>
 *         &lt;element name="Ranges">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Range" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="Rank" type="{http://fsrar.ru/WEGAIS/ProductRef}RankType"/>
 *                             &lt;element name="Start" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
 *                             &lt;element name="Last" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
@XmlType(name = "MarkInfoType", propOrder = {
    "type",
    "ranges"
})
public class MarkInfoType {

    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Ranges", required = true)
    protected MarkInfoType.Ranges ranges;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the ranges property.
     * 
     * @return
     *     possible object is
     *     {@link MarkInfoType.Ranges }
     *     
     */
    public MarkInfoType.Ranges getRanges() {
        return ranges;
    }

    /**
     * Sets the value of the ranges property.
     * 
     * @param value
     *     allowed object is
     *     {@link MarkInfoType.Ranges }
     *     
     */
    public void setRanges(MarkInfoType.Ranges value) {
        this.ranges = value;
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
     *         &lt;element name="Range" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="Rank" type="{http://fsrar.ru/WEGAIS/ProductRef}RankType"/>
     *                   &lt;element name="Start" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
     *                   &lt;element name="Last" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
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
    @XmlType(name = "", propOrder = {
        "range"
    })
    public static class Ranges {

        @XmlElement(name = "Range", required = true)
        protected List<MarkInfoType.Ranges.Range> range;

        /**
         * Gets the value of the range property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the range property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRange().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MarkInfoType.Ranges.Range }
         * 
         * 
         */
        public List<MarkInfoType.Ranges.Range> getRange() {
            if (range == null) {
                range = new ArrayList<MarkInfoType.Ranges.Range>();
            }
            return this.range;
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
         *         &lt;element name="Identity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="Rank" type="{http://fsrar.ru/WEGAIS/ProductRef}RankType"/>
         *         &lt;element name="Start" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
         *         &lt;element name="Last" type="{http://fsrar.ru/WEGAIS/ProductRef}MarkNumberType"/>
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
            "identity",
            "rank",
            "start",
            "last"
        })
        public static class Range {

            @XmlElement(name = "Identity")
            protected String identity;
            @XmlElement(name = "Rank", required = true)
            protected String rank;
            @XmlElement(name = "Start", required = true)
            protected String start;
            @XmlElement(name = "Last", required = true)
            protected String last;

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
             * Gets the value of the rank property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRank() {
                return rank;
            }

            /**
             * Sets the value of the rank property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRank(String value) {
                this.rank = value;
            }

            /**
             * Gets the value of the start property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getStart() {
                return start;
            }

            /**
             * Sets the value of the start property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setStart(String value) {
                this.start = value;
            }

            /**
             * Gets the value of the last property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLast() {
                return last;
            }

            /**
             * Sets the value of the last property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLast(String value) {
                this.last = value;
            }

        }

    }

}
