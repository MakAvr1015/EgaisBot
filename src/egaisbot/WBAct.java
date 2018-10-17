package egaisbot;

import java.io.File;

import java.io.IOException;

import java.sql.Connection;

import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.firebirdsql.pool.FBWrappingDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class WBAct {

    public static class ActWBPos {
        public String f_from_b;
        public String f_ident;
        public String f_quant;
    }

    
    public static String SQL_GET_ACT_HEAD="";
    private String egais_regid;
    private int type_ttn;
    private int ttn_id;
    private String number;
    private Connection conn;
    public WBAct(String regId,Connection lconn) {
        
        super();
        egais_regid=regId;
        conn=lconn;
    }
    public Node getWBact(){
        Document result=null;
        return result;
    }
    
    public Node confirmWB() throws ParserConfigurationException, SAXException, IOException {
        java.util.Date date = new java.util.Date();
        Document confirmTicket;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));


        Element confirmDoc = //confirmTicket.createElementNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01","WayBillAct" );
            //confirmTicket.createElement("ns:WayBillAct");
            confirmTicket.getDocumentElement();
        Element wbAct = confirmTicket.createElement("ns:WayBillAct");
        Element header = confirmTicket.createElement("wa:Header");
        wbAct.appendChild(header);
        Element content = confirmTicket.createElement("wa:IsAccept");
        content.setTextContent("Accepted");
        header.appendChild(content);
        content = confirmTicket.createElement("wa:ACTNUMBER");
        content.setTextContent(this.number);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:ActDate");
        content.setTextContent(EGAISparser.format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wa:WBRegId");
        content.setTextContent(egais_regid);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:Content");
        wbAct.appendChild(content);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);
        confirmDoc.normalize();


        return confirmDoc;
        
    }
}
