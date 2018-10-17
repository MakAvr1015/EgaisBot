package egaisbot;

//import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
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

public class Rest {
    public int f_id;
    private Node xmlNode;
    Connection conn;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private String guid;
    private Date rest_date;
    private static String SET_SQL = "select * from PR_T_EGAIS_REST_SET('%1$s','%2$s',?)";
    private static String SET_SQL_STR = "select * from PR_T_EGAIS_REST_STR_SET(%1$s,'%2$s'" + ",'%3$s','%4$s',%5$s)";

    private void SetCurrOst() {
        String setCurrOst="execute procedure PR_T_EGAIS_SYS_PARAM_SET('CURR_OST',%1$s)";
        PreparedStatement stmt;
        
       
        String sqlQuery = String.format(setCurrOst, f_id);

        try {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        
        
    }

    private void ParseRR_v2() {
        
        String nodeVal;
        if (xmlNode instanceof Element) {

            NodeList nl = xmlNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName() == "rst:RestsDate") {
                    nodeVal = nl.item(i).getTextContent();
                    try {
                        rest_date = format.parse(nodeVal);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (nl.item(i).getNodeName() == "rst:Products") {
                    parseContent_v2(nl.item(i));
                    //rst:StockPosition
                }

            }
        }
        
    }

    private void parseContent_v2(Node node) {
        PosRest ps;
        if (node instanceof Element) {
            Element el = (Element) node;
            NodeList nl = el.getElementsByTagName("rst:StockPosition");
            for (int i = 0; i < nl.getLength(); i++) {
                ps = new PosRest();
                NodeList pl = nl.item(i).getChildNodes();
                for (int j = 0; j < pl.getLength(); j++) {
                    if (pl.item(j).getNodeName() == "rst:Quantity") {
                        ps.quant = Float.parseFloat(pl.item(j).getTextContent());
                    }
                    if (pl.item(j).getNodeName() == "rst:InformF1RegId") {
                        ps.form_a = pl.item(j).getTextContent();
                    }
                    if (pl.item(j).getNodeName() == "rst:InformF2RegId") {
                        ps.form_b = pl.item(j).getTextContent();
                    }
                    if (pl.item(j).getNodeName() == "rst:Product") {
                        Product pr = new Product(pl.item(j), conn,rest_date,2);
                        ps.product = pr.alcCode;
                    }

                }
                posRest.add(ps);
            }
        }
        
    }

    private class PosRest {
        String form_b;
        String form_a;
        String product;
        float quant;
    }
    private ArrayList<PosRest> posRest = new ArrayList<PosRest>();
    public Rest(){
        
    }
    public Rest(Node xml, Connection lConn, String l_guid) {
        super();
        conn = lConn;
        xmlNode = xml;
        guid = l_guid;
        if (((Element) xmlNode).getNodeName() == "ns:ReplyRests") {

            ParseRR();
            SaveToBase();
            SetCurrOst();
        }
        if (((Element) xmlNode).getNodeName() == "ns:ReplyRests_v2") {

            ParseRR_v2();
            SaveToBase();
            SetCurrOst();
        }

    }

    public void SaveToBase() {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        String sqlDateTime; //rest_date
        DateFormat lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sqlDateTime = lformat.format(rest_date);
        sqlQuery = String.format(SET_SQL, guid, sqlDateTime);
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
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        PosRest ps;
        for (int i = 0; i < posRest.size(); i++) {
            ps = posRest.get(i);
            sqlQuery = String.format(SET_SQL_STR, f_id, ps.product, ps.form_a, ps.form_b, ps.quant);

            try {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int p_id = rs.getInt("F_ID");
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();
            }

        }

    }

    private void ParseRR() {
        String nodeVal;
        if (xmlNode instanceof Element) {

            NodeList nl = xmlNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName() == "rst:RestsDate") {
                    nodeVal = nl.item(i).getTextContent();
                    try {
                        rest_date = format.parse(nodeVal);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (nl.item(i).getNodeName() == "rst:Products") {
                    parseContent(nl.item(i));
                    //rst:StockPosition
                }

            }
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
        Element wbQuery = confirmTicket.createElement(/*"ns:QueryRests"*/"ns:QueryRests_v2");
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbQuery);

        confirmDoc.normalize();


        return confirmDoc;
        
    }
    private void parseContent(Node node) {
        PosRest ps;
        if (node instanceof Element) {
            Element el = (Element) node;
            NodeList nl = el.getElementsByTagName("rst:StockPosition");
            for (int i = 0; i < nl.getLength(); i++) {
                ps = new PosRest();
                NodeList pl = nl.item(i).getChildNodes();
                for (int j = 0; j < pl.getLength(); j++) {
                    if (pl.item(j).getNodeName() == "rst:Quantity") {
                        ps.quant = Float.parseFloat(pl.item(j).getTextContent());
                    }
                    if (pl.item(j).getNodeName() == "rst:InformARegId") {
                        ps.form_a = pl.item(j).getTextContent();
                    }
                    if (pl.item(j).getNodeName() == "rst:InformBRegId") {
                        ps.form_b = pl.item(j).getTextContent();
                    }
                    if (pl.item(j).getNodeName() == "rst:Product") {
                        Product pr = new Product(pl.item(j), conn,rest_date,1);
                        ps.product = pr.alcCode;
                    }

                }
                posRest.add(ps);
            }
        }

    }
}
