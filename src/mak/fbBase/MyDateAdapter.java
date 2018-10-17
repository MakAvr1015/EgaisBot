package mak.fbBase;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.GregorianCalendar;

import java.util.Locale;

import javax.rmi.CORBA.Util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


public class MyDateAdapter extends XmlAdapter<String, XMLGregorianCalendar> {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public XMLGregorianCalendar unmarshal(String xml) throws Exception {

        XMLGregorianCalendar xmlCalender=null;
        GregorianCalendar calender = new GregorianCalendar();
        calender.setTime(stringToJavaDate(xml));
        xmlCalender = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
        return xmlCalender;        
        
//        return dateFormat.parse(xml);
    }

    public static Date  stringToJavaDate(String sDate){
        Date date=null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;  
    }
    @Override
    public String marshal(XMLGregorianCalendar object) throws Exception {
        return dateFormat.format(object.toGregorianCalendar().getTime());
    }
}
