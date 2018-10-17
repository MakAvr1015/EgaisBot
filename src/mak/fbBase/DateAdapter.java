package mak.fbBase;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.GregorianCalendar;

import javax.rmi.CORBA.Util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateAdapter extends XmlAdapter<String, XMLGregorianCalendar>  {
    public DateAdapter() {
        super();
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public String marshal(XMLGregorianCalendar v) throws Exception {
        synchronized (dateFormat) {
            
            return dateFormat.format(v.toGregorianCalendar().getTime());
        }
    }

    @Override
    public XMLGregorianCalendar unmarshal(String v) throws Exception {
        synchronized (dateFormat) {
            XMLGregorianCalendar xmlCalender=null;
            GregorianCalendar calender = new GregorianCalendar();
            calender.setTime(dateFormat.parse(v));
            xmlCalender = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);            
            return xmlCalender;
        }
    }

}
