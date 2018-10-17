package egaisbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import java.io.StringBufferInputStream;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

//import javax.xml.transform.Transformer;

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

public class Producer {

    public Node xmlNode;
    public Node xmlNode_v2;
    public int f_id;
    public String clientRegID;
    public String inn;
    public String kpp;
    public String fullName;
    public String shortName;
    private String country;
    private String regionCode = "null";
    private String clientType = "null";
    private String versionWb = "null";
    public java.util.Date actual_date;

    public static String URL_TARGET = "/in/QueryClients_v2";
    private static String SET_SQL =
        "select f_id from PR_T_EGAIS_PARTNER_SET(%1$s,%2$s,'%3$s','%4$s','%5$s','%6$s','%7$s',?,'%8$s',?,%9$s,%10$s,%11$s)";
    private static String GET_SQL = "select * from PR_T_EGAIS_PARTNER_GET(%1$s)";
    public String address;
    private Connection conn;

    public static Document GetQuery(String INN) throws ParserConfigurationException, SAXException, IOException {
        Document queryPartner;
        //        java.util.Date date=new java.util.Date();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        queryPartner = db.parse(new File(Console.XML_TEMPLATE));
        Element parameter = queryPartner.createElement("qp:Parameter");
        Element content = queryPartner.createElement("qp:Name");
        content.setTextContent("ИНН");
        parameter.appendChild(content);
        content = queryPartner.createElement("qp:Value");
        content.setTextContent(INN);
        parameter.appendChild(content);
        content = queryPartner.createElement("qp:Parameters");
        content.appendChild(parameter);
        parameter = queryPartner.createElement("ns:QueryClients_v2");
        parameter.appendChild(content);
        NodeList nl = queryPartner.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(parameter);

        DOMSource ds = new DOMSource(queryPartner);
        OutputStream outt;
        StreamResult streamResult = new StreamResult();

        /*try {
            outt = new FileOutputStream("d:\\p_q.xml");
            streamResult.setOutputStream(outt);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }*/

        return queryPartner;

    }

    public Producer(Node node, Connection lConn, java.util.Date lDat) {
        conn = lConn;
        xmlNode = node;
        actual_date = lDat;
        if (actual_date == null) {
            actual_date = new java.util.Date();
        }
        Parce();
        SaveToBase();
    }

    public Producer(Node node, Connection lConn, java.util.Date lDat, int ver) {
        conn = lConn;
        xmlNode_v2 = node;
        actual_date = lDat;
        if (actual_date == null) {
            actual_date = new java.util.Date();
        }
        Parce_v2();
        SaveToBase();
    }

    public Node GetXml(Element parent) throws ParserConfigurationException {
        Document producerXml;
        /*        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();*/
        producerXml = parent.getOwnerDocument();
        Element content;
        content = producerXml.createElement("oref:INN");
        content.setTextContent(inn);
        parent.appendChild(content);
        content = producerXml.createElement("oref:KPP");
        content.setTextContent(kpp);
        parent.appendChild(content);
        content = producerXml.createElement("oref:ClientRegId");
        content.setTextContent(clientRegID);
        parent.appendChild(content);
        content = producerXml.createElement("oref:FullName");
        content.setTextContent(fullName);
        parent.appendChild(content);
        content = producerXml.createElement("oref:ShortName");
        content.setTextContent(shortName);
        parent.appendChild(content);
        Element address = producerXml.createElement("oref:address");
        content = producerXml.createElement("oref:Country");
        content.setTextContent(country);
        address.appendChild(content);
        content = producerXml.createElement("oref:description");
        content.setTextContent(this.address);
        address.appendChild(content);
        parent.appendChild(address);
        Node result;
        result = parent;
        return result;
    }

    public Node GetXML_v2(Element parent) throws ParserConfigurationException {
        Document producerXml;
        /*        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();*/
        producerXml = parent.getOwnerDocument();
        Element header;
        header = producerXml.createElement(clientType);
        Element content;
        content = producerXml.createElement("oref:INN");
        content.setTextContent(inn);
        header.appendChild(content);
        content = producerXml.createElement("oref:KPP");
        content.setTextContent(kpp);
        header.appendChild(content);
        content = producerXml.createElement("oref:ClientRegId");
        content.setTextContent(clientRegID);
        header.appendChild(content);
        content = producerXml.createElement("oref:FullName");
        content.setTextContent(fullName);
        header.appendChild(content);
        content = producerXml.createElement("oref:ShortName");
        content.setTextContent(shortName);
        header.appendChild(content);
        Element address = producerXml.createElement("oref:address");
        content = producerXml.createElement("oref:Country");
        content.setTextContent(country);
        address.appendChild(content);
        content = producerXml.createElement("oref:RegionCode");
        content.setTextContent(regionCode);
        address.appendChild(content);
        content = producerXml.createElement("oref:description");
        content.setTextContent(this.address);
        address.appendChild(content);
        header.appendChild(address);
        parent.appendChild(header);
        Node result;
        result = parent;
        return result;
    }

    public Producer(int p_id, Connection lConn) {
        conn = lConn;
        String sqlQuery = String.format(GET_SQL, p_id);
        Statement stmt;
        ResultSet rs;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                inn = rs.getString("F_INN");
                kpp = rs.getString("F_KPP");
                shortName = rs.getString("F_NAME");
                fullName = rs.getString("F_FULL_NAME");
                country = rs.getString("F_COUNTRY");
                address = rs.getString("F_ADRES");
                shortName = rs.getString("F_NAME");
                clientRegID = rs.getString("F_EGAIS_ID");
                regionCode = rs.getString("F_REGION_CODE");
                if (rs.getString("F_CLIENTTYPE").isEmpty()) {
                    clientType = "null";
                } else {
                    clientType = rs.getString("F_CLIENTTYPE");
                }
                if (rs.getString("F_version_wb").isEmpty()) {
                    versionWb = "WayBill_v1";
                } else {
                    versionWb = rs.getString("F_version_wb");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void Parce() {
        if (xmlNode instanceof Element) {
            NodeList nl = xmlNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName() == "oref:ClientRegId") {
                    clientRegID = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:INN") {
                    inn = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:KPP") {
                    kpp = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:FullName") {
                    fullName = nl.item(i).getTextContent();
                    fullName = fullName.replaceAll("'", "''");
                }
                if (nl.item(i).getNodeName() == "oref:ShortName") {
                    shortName = nl.item(i).getTextContent();
                    shortName = shortName.replaceAll("'", "''");
                }
                if (nl.item(i).getNodeName() == "oref:address") {
                    NodeList adr = nl.item(i).getChildNodes();
                    for (int j = 0; j < adr.getLength(); j++) {
                        if (adr.item(j).getNodeName() == "oref:Country") {
                            country = adr.item(j).getTextContent();
                        }
                        if (adr.item(j).getNodeName() == "oref:description") {
                            address = adr.item(j).getTextContent();
                            address = address.replaceAll("'", "''");
                        }
                    }
                }

            }
        }
    }

    private void Parce_v2() {

        if (xmlNode_v2 instanceof Element) {

            NodeList ul = xmlNode_v2.getChildNodes();
            Node orgInfo = xmlNode_v2;
            String nodeName;
            String itemName;
            nodeName = xmlNode_v2.getLocalName();
            switch (nodeName.toLowerCase()) {
            case "client":
            case "producer":
            case "importer":


                for (int k = 0; k < ul.getLength(); k++) {
                    itemName = ul.item(k).getLocalName();
                    if (itemName != null) {
                        switch (itemName.toLowerCase()) {
                        case "ol":
                        case "fl":
                        case "fo":
                        case "ts":
                        case "ul":
                            orgInfo = ul.item(k);
                            break;
                        case "versionwb":
                            versionWb = "'" + ul.item(k).getTextContent() + "'";
                            break;
                        }
                        //                        ul.item(k).setPrefix("oref");

                        //                   }
                        if (ul.item(k).getNodeName() == "oref:OrgInfoV2") {
                            orgInfo = ul.item(k).getFirstChild();
                        }
                    }
                    /*    if (ul.item(k).getNodeName() == "oref:VersionWB") {
                        versionWb = "'" + ul.item(k).getTextContent() + "'";
                    }*/

                }
                break;

                //            case "producer":
                //            case "importer":
                //                orgInfo = xmlNode_v2.getFirstChild();
                //                break;
            case "shipper":
            case "consignee":

                for (int k = 0; k < ul.getLength(); k++) {
                    itemName = ul.item(k).getLocalName();
                    if (itemName != null) {
                        switch (itemName.toLowerCase()) {
                        case "ol":
                        case "fl":
                        case "fo":
                        case "ts":
                        case "ul":
                            orgInfo = ul.item(k);
                            break;
                        }
                    }
                }
                //                    orgInfo = xmlNode_v2.getChildNodes().item(1);
                break;
            }
            /*            switch (xmlNode_v2.getNodeName()) {
            case "rc:Client":
                for (int k = 0; k < ul.getLength(); k++) {
                    if (ul.item(k).getNodeName() == "oref:OrgInfoV2") {
                        orgInfo = ul.item(k).getFirstChild();

                    }
                    if (ul.item(k).getNodeName() == "oref:VersionWB") {
                        versionWb = "'" + ul.item(k).getTextContent() + "'";
                    }

                }
                break;
            case "wbr:Shipper":
                orgInfo = xmlNode_v2.getChildNodes().item(1);
                //                orgInfo = xmlNode_v2.getFirstChild();
                break;
            case "wbr:Consignee":
                orgInfo = xmlNode_v2.getChildNodes().item(1);
                //                orgInfo = xmlNode_v2.getFirstChild();
                break;
            case "wb:Shipper":
                orgInfo = xmlNode_v2.getChildNodes().item(1);
                //                orgInfo = xmlNode_v2.getFirstChild();
                break;
            case "wb:Consignee":
                orgInfo = xmlNode_v2.getChildNodes().item(1);
                //                orgInfo = xmlNode_v2.getFirstChild();
                break;

            case "pref:Producer":
                orgInfo = xmlNode_v2.getFirstChild();
                break;
            case "pref:Importer":
                orgInfo = xmlNode_v2.getFirstChild();
                break;
            default:
                orgInfo = xmlNode_v2;
            }*/
            /*            for (int k = 0; k < ul.getLength(); k++) {
                if (ul.item(k).getNodeName() == "oref:OrgInfoV2") {
                    orgInfo = ul.item(k).getFirstChild();*/
            xmlNode_v2 = orgInfo;
            clientType = "'oref:" + orgInfo.getLocalName() + "'";
            NodeList nl = orgInfo.getChildNodes(); //xmlNode_v2.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                nodeName = nl.item(i).getLocalName();
                if (nodeName != null) {
                    switch (nodeName.toLowerCase()) {
                    case "clientregid":
                        clientRegID = nl.item(i).getTextContent();
                        break;
                    case "inn":
                        inn = nl.item(i).getTextContent();
                        break;
                    case "kpp":
                        kpp = nl.item(i).getTextContent();
                        break;
                    case "fullname":
                        fullName = nl.item(i).getTextContent();
                        fullName = fullName.replaceAll("'", "''");
                        break;
                    case "shortname":
                        shortName = nl.item(i).getTextContent();
                        shortName = shortName.replaceAll("'", "''");
                        break;
                    case "regioncode":
                        regionCode = "'" + nl.item(i).getTextContent() + "'";
                        break;
                    case "address":
                        NodeList adr = nl.item(i).getChildNodes();
                        for (int j = 0; j < adr.getLength(); j++) {
                            String adrNode = adr.item(j).getLocalName();
                            if (adrNode != null) {
                                switch (adrNode.toLowerCase()) {
                                case "country":
                                    country = adr.item(j).getTextContent();
                                    break;
                                case "description":
                                    address = adr.item(j).getTextContent();
                                    address = address.replaceAll("'", "''");
                                    break;
                                case "regioncode":
                                    regionCode = adr.item(j).getTextContent();
                                    break;
                                }
                            }

                        }

                        break;
                    }
                }
                /*                if (nl.item(i).getNodeName() == "oref:ClientRegId") {
                    clientRegID = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:INN") {
                    inn = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:KPP") {
                    kpp = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "oref:FullName") {
                    fullName = nl.item(i).getTextContent();
                    fullName = fullName.replaceAll("'", "''");
                }
                if (nl.item(i).getNodeName() == "oref:ShortName") {
                    shortName = nl.item(i).getTextContent();
                    shortName = shortName.replaceAll("'", "''");
                }
                if (nl.item(i).getNodeName() == "oref:RegionCode") {
                    regionCode = "'" + nl.item(i).getTextContent() + "'";
                }
                if (nl.item(i).getNodeName() == "oref:address") {
                    NodeList adr = nl.item(i).getChildNodes();
                    for (int j = 0; j < adr.getLength(); j++) {
                        if (adr.item(j).getNodeName() == "oref:Country") {
                            country = adr.item(j).getTextContent();
                        }
                        if (adr.item(j).getNodeName() == "oref:description") {
                            address = adr.item(j).getTextContent();
                            address = address.replaceAll("'", "''");
                        }
                        if (adr.item(j).getNodeName() == "oref:RegionCode") {
                            regionCode = adr.item(j).getTextContent();
                        }

                    }
                }*/

            }
            //                }
            //            }
        }

    }

    public int SaveToBase() {

        int result = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery =
            String.format(SET_SQL, inn, kpp, fullName, shortName, clientRegID, address, country, EGAISparser.format.format(actual_date),
                          clientType, versionWb, regionCode);
        DOMSource ds = new DOMSource(xmlNode);
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


        DOMSource ds_v2 = new DOMSource(xmlNode_v2);
        ByteArrayOutputStream outt_v2 = new ByteArrayOutputStream();
        StreamResult streamResult_v2 = new StreamResult();
        streamResult_v2.setOutputStream(outt_v2);
        try {
            TransformerFactory.newInstance().newTransformer().transform(ds_v2, streamResult_v2);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        InputStream ts_v2 = new ByteArrayInputStream(outt_v2.toByteArray());
        try {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.setBlob(1, ts);
            stmt.setBlob(2, ts_v2);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        return result;
    }

    public Node GetQuery() throws ParserConfigurationException, SAXException, IOException {
        Document query;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        query = db.parse(new File(Console.XML_TEMPLATE));
        Element confirmDoc = query.getDocumentElement();
        Element element1 = query.createElement("ns:QueryClients");
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(element1);
        Element element2 = query.createElement("qp:Parameters");
        element1.appendChild(element2);
        Element parameter = query.createElement("qp:Parameter");
        element2.appendChild(parameter);
        element2 = query.createElement("qp:Name");
        element2.setTextContent("ИНН");
        parameter.appendChild(element2);
        element1 = query.createElement("qp:Value");
        element1.setTextContent(inn);
        parameter.appendChild(element1);
        /*        <ns:QueryClients>
        <qp:Parameters>
        <qp:Parameter>
        <qp:Name>ИНН</qp:Name>
        <qp:Value>5407250469</qp:Value>
        </qp:Parameter>
        </qp:Parameters>
        </ns:QueryClients>*/


        return confirmDoc;
    }
}
