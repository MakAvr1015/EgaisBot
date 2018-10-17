package egaisbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;

import java.io.InputStream;

import java.sql.Connection;

import java.util.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.text.ParseException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class InformA {
    private static String SET_SQL = "SELECT * FROM PR_T_EGAIS_FORM_A_SET(%1$s, '%2$s')";
    private static String GET_SQL = "SELECT * FROM T_EGAIS_FORM_A where f_id=%1$s";
    private static String SET_REQ =
        "execute procedure PR_T_EGAIS_FORM_A_REQ_SET(%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,%8$s, ?)";
    public String regId;
    public int f_id;
    Connection conn;

    InformA() {
    }

    InformA(int p_id, Connection lConn) {
        ResultSet rs;
        Statement stmt;
        String sqlQuery = String.format(GET_SQL, p_id);
        try {
            stmt = lConn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                regId = rs.getString("F_EGAIS_ID");
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public void saveQuery(Node query, Connection lConn) throws ParseException {
        NodeList nl = query.getChildNodes();
        String ttnNum = "null";
        String ttnDate = "null";
        String egaisNumber = "null";
        String egaisDate = "null";
        String shippingDate = "null";
        String bottlingDate = "null";
        String quant = "null";
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName() == "rfa:InformARegId") {
                regId = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:InformF1RegId") {
                regId = "'" + nl.item(i).getTextContent() + "'";
            }
            
            

            if (nl.item(i).getNodeName() == "rfa:TTNNumber") {
                ttnNum = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:TTNDate") {
                ttnDate = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:EGAISNumber") {
                egaisNumber = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:EGAISDate") {
                egaisDate = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:ShippingDate") {
                shippingDate = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:BottlingDate") {
                bottlingDate = "'" + nl.item(i).getTextContent() + "'";
            }
            if (nl.item(i).getNodeName() == "rfa:Quantity") {
                quant = "'" + nl.item(i).getTextContent() + "'";
            }


        }
        PreparedStatement stmt;
        String sqlQuery =
            String.format(SET_REQ, regId, ttnNum, ttnDate, egaisNumber, egaisDate, bottlingDate, shippingDate, quant);

        DOMSource ds = new DOMSource(query);
        ByteArrayOutputStream outt = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult();
        streamResult.setOutputStream(outt);
        try {
            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        InputStream ts = new ByteArrayInputStream(outt.toByteArray());

        try {
            stmt = lConn.prepareStatement(sqlQuery);
            stmt.setBlob(1, ts);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


    }

    public Node getXML(Document productXml, int version) throws ParserConfigurationException {
        Element content;
        if (version == 2) {
            content = productXml.createElement("wb:InformF1");
        } else {
            content = productXml.createElement("wb:InformA");
        }
        Element formARegID = productXml.createElement("pref:RegId");
        formARegID.setTextContent(regId);
        content.appendChild(formARegID);
        return content;
    }

    InformA(Node node) {
        ParseNode(node);
    }

    public void ParseNode(Node node) {
        NodeList nl = node.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            String nodeName=nl.item(i).getLocalName();
            if (nodeName!=null){
                if (nodeName.equals("RegId")){
                    regId = nl.item(i).getTextContent();
                }
            }
/*            if (nl.item(i).getNodeName() == "pref:RegId") {
                regId = nl.item(i).getTextContent();
            }*/
        }
        if (nl.getLength() == 0) {

        }

    }

    public Node getQuery() throws ParserConfigurationException, SAXException, IOException {
        Document confirmTicket;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));


        Element confirmDoc = //confirmTicket.createElementNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01","WayBillAct" );
            //confirmTicket.createElement("ns:WayBillAct");
            confirmTicket.getDocumentElement();
        Element wbQuery = confirmTicket.createElement("ns:QueryFormF1");
        Element f_regId = confirmTicket.createElement("qf:FormRegId");
        f_regId.setTextContent(this.regId);
        wbQuery.appendChild(f_regId);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbQuery);

        confirmDoc.normalize();


        return confirmDoc;
    }

    public void SaveToBase(int p_product, Connection lConn) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_SQL, p_product, regId);
        try {
            stmt = lConn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

}
