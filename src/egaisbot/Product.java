package egaisbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.sql.Blob;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;

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

//import sun.print.DocumentPropertiesUI;

public class Product {

    public Node xmlNode;
    public int f_id;
    private String fullName;
    private String shortName;
    public String alcCode;
    private String capacity;
    private String alcVolume;
    private Date actual_date;
    public String productVCode;
    private static String SET_SQL =
        "select f_id from PR_T_EGAIS_PRODUCT_SET('%1$s','%2$s','%3$s'," + "'%4$s',%5$s," + "%6$s,'%7$s',%8$s,%9$s,?,%10$s)";
    private static String GET_SQL = "select * from pr_t_egais_product_get(%1$s)";
    private static String GET_SOURCE_V1 = "select f_source from t_egais_product where f_id=%1$s";
    private static String GET_SOURCE_V2 = "select f_source_v2 as f_source from t_egais_product where f_id=%1$s";
    private Producer producer;
    private Producer importer;
    private Connection conn;
    private int version;

    public Product(Node xml, Connection lConn, Date p_actual_date, int p_version) {
        conn = lConn;
        xmlNode = xml;
        actual_date = p_actual_date;
        version = p_version;
        Parse();
        //      OutLine();
        if (!alcCode.isEmpty()) {
            SaveToBase();
        }
    }

    public Product(int p_id, Connection lconn) {
        conn = lconn;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery = String.format(GET_SQL, p_id);

        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                fullName = rs.getString("F_FULL_NAME");
                alcCode = rs.getString("F_EGAIS_ID");
                alcVolume = rs.getString("F_ALCVOL");
                capacity = rs.getString("F_CAPACITY");

            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public Node GetXML(Node parent, int version) throws SQLException, ParserConfigurationException, SAXException,
                                                        IOException {
        Node result = null;
        Document document;
        //result=parent;

        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        Blob source = null;
        DocumentBuilderFactory builderFactory;
        DocumentBuilder builder = null;
        builderFactory = DocumentBuilderFactory.newInstance();
        ByteArrayInputStream inp; // = new ByteArrayInputStream();
        if (version == 2) {
            sqlQuery = String.format(GET_SOURCE_V2, f_id);
        } else {
            sqlQuery = String.format(GET_SOURCE_V1, f_id);
        }
        System.out.println(String.valueOf(f_id));
        stmt = conn.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();
        byte[] b_source;
        builder = builderFactory.newDocumentBuilder();
        while (rs.next()) {
            source = rs.getBlob("F_SOURCE");
            b_source = source.getBytes(1, (int)source.length());
            int g = (int)source.length();
            for (int k = 0; k < b_source.length; k++) {
                if (k > 1) {
                    if (b_source[k - 1] == (byte)208 && b_source[k] == (byte)0) {
                        b_source[k - 1] = (byte)208;
                        b_source[k] = (byte)152;
                    }
                }
            }

            inp = new ByteArrayInputStream(b_source);
            document = builder.parse(inp);

            Node root = document.getFirstChild();
            result = parent.getOwnerDocument().createElement("wb:Product");

            NodeList nl = root.getChildNodes();
            for (int k = 0; k < nl.getLength(); k++) {
                Node src = parent.getOwnerDocument().importNode(nl.item(k), true);

                result.appendChild(src);
            }
            //                                          result = document;
            /*            DOMSource ds = new DOMSource(result);
            OutputStream outt;
            StreamResult streamResult = new StreamResult();

            try {
                outt = new FileOutputStream("d:\\file.xml");
//                outt.write(b_source);
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

        }


        return result;
    }

    private void Parse() {
        if (xmlNode instanceof Element) {
            //a child element to process
            String nodeName;
            NodeList nl = xmlNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                nodeName = nl.item(i).getLocalName();
                if (nodeName != null) {
                    switch (nodeName.toLowerCase()) {
                    case "fullname":
                        fullName = nl.item(i).getTextContent();
                        fullName = fullName.replaceAll("'", "''");
                        break;
                    case "alccode":
                        alcCode = nl.item(i).getTextContent();
                        break;
                    case "capacity":
                        capacity = nl.item(i).getTextContent();
                        break;
                    case "productvcode":
                        productVCode = nl.item(i).getTextContent();
                        break;
                    case "alcvolume":
                        alcVolume = nl.item(i).getTextContent();
                        break;
                    case "producer":
                        if (version == 2) {
                            producer = new Producer(nl.item(i), conn, actual_date, 2);
                        } else {
                            producer = new Producer(nl.item(i), conn, actual_date);
                        }
                        break;
                    case "importer":
                        if (version == 2) {
                            producer = new Producer(nl.item(i), conn, actual_date, 2);
                        } else {
                            importer = new Producer(nl.item(i), conn, actual_date);
                        }

                        break;
                    case "shortname":
                        shortName = nl.item(i).getTextContent();
                        shortName = shortName.replaceAll("'", "''");
                        break;
                    }
                }

                /*if (nl.item(i).getNodeName() == "pref:FullName") {
                    fullName = nl.item(i).getTextContent();
                    fullName = fullName.replaceAll("'", "''");
                }
                if (nl.item(i).getNodeName() == "pref:AlcCode") {
                    alcCode = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "pref:Capacity") {
                    capacity = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "pref:AlcVolume") {
                    alcVolume = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "pref:ProductVCode") {
                    productVCode = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "pref:Producer") {
                    if (version == 2) {
                        producer = new Producer(nl.item(i), conn, actual_date, 2);
                    } else {
                        producer = new Producer(nl.item(i), conn, actual_date);
                    }
                }
                if (nl.item(i).getNodeName() == "pref:Importer") {
                    if (version == 2) {
                        producer = new Producer(nl.item(i), conn, actual_date, 2);
                    } else {
                        importer = new Producer(nl.item(i), conn, actual_date);
                    }
                }
                if (nl.item(i).getNodeName() == "pref:ShortName") {
                    shortName = nl.item(i).getTextContent();
                    shortName = shortName.replaceAll("'", "''");
                }

                }
            }*/
            }
        }
    }

    public void OutLine() {
        String str;
        str = fullName + ";" + alcCode + ";" + capacity + ";" + alcVolume + ";" + productVCode;
        System.out.println(str);
    }

    public int SetDB() {
        String selectSQL = "select * from t_EGAIS_Product where f_id=:p_id";
        return 0;
    }

    private void SaveToBase() {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery =
                String.format(SET_SQL, alcCode, "", fullName, shortName, capacity, alcVolume, productVCode, (producer !=
                                                                                                             null) ?
                                                                                                            producer.f_id :
                                                                                                            null,
                              (importer != null) ? importer.f_id : null, version);

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

        try {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public Node GetQuery(String Product_id) throws ParserConfigurationException, SAXException, IOException {
        Document queryBody;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        queryBody = db.parse(new File(Console.XML_TEMPLATE));
        Element parameter = queryBody.createElement("qp:Parameter");
        Element content = queryBody.createElement("qp:Name");
        content.setTextContent("КОД");
        parameter.appendChild(content);
        content = queryBody.createElement("qp:Value");
        content.setTextContent(Product_id);
        parameter.appendChild(content);
        content = queryBody.createElement("qp:Parameters");
        content.appendChild(parameter);
        parameter = queryBody.createElement("ns:QueryAP_v2");
        parameter.appendChild(content);
        NodeList nl = queryBody.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(parameter);

        queryBody.normalize();

        return queryBody;

    }

}
