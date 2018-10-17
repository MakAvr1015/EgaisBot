package egaisbot;

//import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

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


public class WayBill {
    private static String SET_SQL_WB =
        "select f_id,f_remove_url from PR_T_EGAIS_TTN_SET" +
        "('%1$s','%2$s','%3$s',%4$s,%5$s,%6$s,%7$s,%8$s,'%9$s',%10$s,?)";
    private static String GET_SQL_WB = "select * from PR_T_EGAIS_TTN_GET(%1$s)";
    private static String SET_SQL_REGINFO =
        "select * from PR_T_EGAIS_TTN_FORMREG_SET" + "(%1$s,'%2$s','%3$s',%4$s,'%5$s','%6$s','%7$s','%8$s',?)";
    private static String SET_SQL_REGINFO_OUT =
        "select * from PR_T_EGAIS_TTN_OUT_FORMREG_SET" + "(%1$s,'%2$s','%3$s',%4$s,%5$s,'%6$s','%7$s','%8$s',?)";
    private static String SET_SQL_REGINFO_STR =
        "select f_id from PR_T_EGAIS_TTN_FORMREG_SET_S(" + "%1$s,'%2$s','%3$s',%4$s)";
    private static String SET_SQL_UID_WB = "execute procedure PR_T_EGAIS_TTN_IN_SET_GUID('%1$s',%2$s)";
    private static String SET_SQL_WBAct = "select * from PR_T_EGAIS_TTN_ACT_SET('%1$s','%2$s','%3$s','%4$s','%5$s',?)";
    private static String SET_SQL_WBAct_pos = "select f_id from PR_T_EGAIS_TTN_ACT_POS_SET(%1$s,%2$s,'%3$s',%4$s)";
    public Node xmlNode;
    public int f_id;
    String identity;
    String number;
    java.util.Date date;
    String strDate;
    String type;
    String unitType;
    java.util.Date shippingDate;
    Producer shiper;
    Producer consignee;
    WBPosition[] positions;
    Connection conn;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public boolean remove_url;
    private java.util.Date egaisFixdate;
    private String egais_regid;
    private String egais_fix_num;

    private void parseWBAct(int version) {
        String accept = null, actNum = null, actRegId = null, note = null, dat = null;
        int v_id = 0;
        if (xmlNode instanceof Element) {
            Element el = (Element)xmlNode;

            NodeList nl;
            if (version == 2) {
                nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/ActTTNSingle_v2", "Header");
            } else {
                nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/ActTTNSingle", "Header");
                //               nl = el.getElementsByTagName("wa:Header");
            }
            NodeList hd = nl.item(0).getChildNodes();
            String nodeName;
            for (int i = 0; i < hd.getLength(); i++) {

                nodeName = hd.item(i).getLocalName();

                if (nodeName != null) {
                    if (nodeName.equals("IsAccept")) {
                        accept = hd.item(i).getTextContent();
                    }
                    if (nodeName.equals("ACTNUMBER")) {
                        actNum = hd.item(i).getTextContent();
                    }
                    if (nodeName.equals("ActDate")) {
                        dat = hd.item(i).getTextContent();
                    }
                    if (nodeName.equals("WBRegId")) {
                        actRegId = hd.item(i).getTextContent();
                    }
                    if (nodeName.equals("Note")) {
                        note = hd.item(i).getTextContent();
                    }

                }
                if (hd.item(i).getNodeName() == "wa:IsAccept") {
                    accept = hd.item(i).getTextContent();
                }

                if (hd.item(i).getNodeName() == "wa:ACTNUMBER") {
                    actNum = hd.item(i).getTextContent();
                }


                if (hd.item(i).getNodeName() == "wa:ActDate") {
                    dat = hd.item(i).getTextContent();
                }


                if (hd.item(i).getNodeName() == "wa:WBRegId") {
                    actRegId = hd.item(i).getTextContent();
                }


                if (hd.item(i).getNodeName() == "wa:Note") {
                    note = hd.item(i).getTextContent();
                }


            }
            nl = el.getElementsByTagName("wa:Position");
            actWBPos = new ArrayList<WBAct.ActWBPos>();
            WBAct.ActWBPos wbaPos;
            for (int i = 0; i < nl.getLength(); i++) {
                hd = nl.item(i).getChildNodes();
                wbaPos = new WBAct.ActWBPos();
                for (int j = 0; j < hd.getLength(); j++) {
                    if (hd.item(j).getNodeName() == "wa:Identity") {
                        wbaPos.f_ident = hd.item(j).getTextContent();
                        //Integer.getInteger(hd.item(j).getTextContent());
                    }
                    if (hd.item(j).getNodeName() == "wa:RealQuantity") {
                        wbaPos.f_quant = hd.item(j).getTextContent();
                    }
                    if (hd.item(j).getNodeName() == "wa:InformBRegId") {
                        wbaPos.f_from_b = hd.item(j).getTextContent();
                    }

                }
                actWBPos.add(wbaPos);
            }
            ResultSet rs;
            PreparedStatement stmt;
            String sqlQuery;
            sqlQuery = String.format(SET_SQL_WBAct, actNum, dat, actRegId, note, accept);
            DOMSource ds = new DOMSource(xmlNode);
            OutputStream outFile;
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
                    v_id = rs.getInt("F_ID");
                    if (rs.getInt("F_REMOVE_URL") > 0) {
                        remove_url = true;
                    } else {
                        remove_url = false;
                    }
                    /*                    outFile = new FileOutputStream("d:\\WbAct\\"+String.valueOf(v_id));
                    streamResult.setOutputStream(outFile);
                    try {
                        TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
                    } catch (TransformerConfigurationException e) {
                    } catch (TransformerException e) {
                    }*/
                }


            } catch (SQLException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();
            } /*catch (FileNotFoundException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();
            }*/
            int vv_id;
            if (v_id > 0) {
                for (int i = 0; i < actWBPos.size(); i++) {
                    sqlQuery =
                        String.format(SET_SQL_WBAct_pos, v_id, actWBPos.get(i).f_ident, actWBPos.get(i).f_from_b,
                                      actWBPos.get(i).f_quant);

                    try {
                        stmt = conn.prepareStatement(sqlQuery);
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            vv_id = rs.getInt("F_ID");
                        }
                    } catch (SQLException e) {
                        System.out.println(sqlQuery);
                        e.printStackTrace();

                    }
                }

            }

            //            actWBPos

        }
    }

    private class PosRegInfo {
        public String ident;
        public String new_form_B;
    }

    private ArrayList<PosRegInfo> posRegInfo;
    private ArrayList<WBAct.ActWBPos> actWBPos;

    private void SaveToBaseWB(int urlNo) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery =
            String.format(SET_SQL_WB, number, strDate, type, null, shiper.f_id, consignee.f_id, null, null, identity,
                          urlNo);
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
                if (rs.getInt("f_remove_url") == 1) {
                    this.remove_url = true;
                }
            }

        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public WayBill(Node xml, Connection lConn, int urlNo) {
        super();
        conn = lConn;
        xmlNode = xml;
        remove_url = false;
        String nodeName = xml.getLocalName();
        if (nodeName != null) {
            if (nodeName.equals("WayBillAct_v2")) {
                parseWBAct(2);
            }
            if (nodeName.equals("WayBillAct")) {
                parseWBAct(1);
            }
            if (nodeName.equals("WayBill_v2")) {

                ParseWb(urlNo, 2);
            }


        }
        if (((Element)xmlNode).getNodeName() == "ns:WayBill") {
            ParseWb(urlNo, 1);
        }
        /*        if (((Element)xmlNode).getNodeName() == "ns:WayBill_v2") {
            ParseWb(urlNo, 2);
        } else */
        if (((Element)xmlNode).getNodeName() == "ns:TTNInformBReg") {
            ParseInformReg(1);
        } /*else if (((Element)xmlNode).getNodeName() == "ns:WayBillAct") {
            parseWBAct(1);
        } else if (((Element)xmlNode).getNodeName() == "ns:WayBillAct_v2") {
            parseWBAct(2);
        }*/ else if (((Element)xmlNode).getNodeName() == "ns:TTNInformF2Reg") {
            ParseInformReg(2);
        }
    }

    public WayBill(int p_id, Connection lConn) {
        super();
        conn = lConn;
        remove_url = false;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        //DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        sqlQuery = String.format(GET_SQL_WB, p_id);
        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                this.number = rs.getString("F_NUMBER");
                this.date = rs.getDate("F_DATE");
                this.egais_regid = rs.getString("F_REGID");
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    private void ParseWb(int urlNo, int version) {
        if (xmlNode instanceof Element) {
            String nodeName;
            NodeList nl = xmlNode.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                nodeName = nl.item(i).getLocalName();
                if (nodeName != null) {
                    switch (nodeName.toLowerCase()) {
                    case "identity":
                        identity = nl.item(i).getTextContent();
                        break;
                    case "header":
                        parseHeader(nl.item(i), version);
                        SaveToBaseWB(urlNo);

                        break;
                    case "content":
                        nl.item(i).setPrefix("wb");
                        parseContent(nl.item(i));
                        break;
                    }
                }
                if (nl.item(i).getNodeName() == "wb:Identity") {
                    identity = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wb:Header") {
                    parseHeader(nl.item(i), version);
                    SaveToBaseWB(urlNo);
                }
                if (nl.item(i).getNodeName() == "wb:Content") {
                    parseContent(nl.item(i));

                }

            }
        }
    }

    private void parseHeader(Node node, int version) {
        //        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        if (node instanceof Element) {
            NodeList nl = node.getChildNodes();
            String nodeName;

            for (int i = 0; i < nl.getLength(); i++) {
                nodeName = nl.item(i).getLocalName();
                if (nodeName != null) {
                    nl.item(i).setPrefix("wb");
                    switch (nodeName.toLowerCase()) {
                    case "number":
                        number = nl.item(i).getTextContent();
                        break;
                    case "date":
                        try {
                            strDate = nl.item(i).getTextContent();
                            date = format.parse(nl.item(i).getTextContent());
                            //format.parse(nl.item(i).getTextContent());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "type":
                        type = nl.item(i).getTextContent();
                        break;
                    case "unittype":
                        unitType = nl.item(i).getTextContent();
                        break;
                    case "shippingdate":
                        try {
                            shippingDate = format.parse(nl.item(i).getTextContent());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        break;
                    case "shipper":
                        nl.item(i).setPrefix("oref");
                        nl.item(i).normalize();
                        if (version == 2) {
                            shiper = new Producer(nl.item(i), conn, shippingDate, 2);
                        } else {
                            shiper = new Producer(nl.item(i), conn, shippingDate);
                        }

                        break;
                    case "consignee":
                        nl.item(i).setPrefix("oref");
                        nl.item(i).normalize();
                        if (version == 2) {
                            consignee = new Producer(nl.item(i), conn, shippingDate, 2);
                        } else {
                            consignee = new Producer(nl.item(i), conn, shippingDate);
                        }

                        break;
                    }
                }
                if (nl.item(i).getNodeName() == "wb:NUMBER") {
                    number = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wb:Date") {
                    try {
                        strDate = nl.item(i).getTextContent();
                        date = format.parse(nl.item(i).getTextContent());
                        //format.parse(nl.item(i).getTextContent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (nl.item(i).getNodeName() == "wb:Type") {
                    type = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wb:UnitType") {
                    unitType = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wb:ShippingDate") {
                    try {
                        shippingDate = format.parse(nl.item(i).getTextContent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (nl.item(i).getNodeName() == "wb:Shipper") {
                    if (version == 2) {
                        shiper = new Producer(nl.item(i), conn, shippingDate, 2);
                    } else {
                        shiper = new Producer(nl.item(i), conn, shippingDate);
                    }
                }
                if (nl.item(i).getNodeName() == "wb:Consignee") {
                    if (version == 2) {
                        consignee = new Producer(nl.item(i), conn, shippingDate, 2);
                    } else {
                        consignee = new Producer(nl.item(i), conn, shippingDate);
                    }
                }
                if (nl.item(i).getNodeName() == "wb:Transport") {

                }

            }
        }
    }

    private void parseContent(Node node) {
        if (node instanceof Element) {
            Element el = (Element)node;
            NodeList nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/TTNSingle_v2", "Position");
            //NodeList nl = el.getElementsByTagName("wb:Position");
            //        NodeList nl = node.getChildNodes();
            positions = new WBPosition[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                positions[i] = new WBPosition(nl.item(i), conn, this.date);
                positions[i].SaveToBase(f_id);
            }

        }
    }

    public void UpdateRegisterInfo() throws SQLException {
        ResultSet rs;
        PreparedStatement stmt;

        String sqlGetMemo = "select f_fix_source from t_egais_in_ttn where f_id=%1$s";
        ByteArrayInputStream inp; // = new ByteArrayInputStream();
        FileOutputStream fl;
        String sqlQuery = String.format(sqlGetMemo, f_id);

        stmt = conn.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();
        rs.next();
        byte[] b_source;
        Blob source = null;
        source = rs.getBlob("F_FIX_SOURCE");
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
        Document document;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        ByteArrayInputStream is = new ByteArrayInputStream(b_source);
        try {
            builder = builderFactory.newDocumentBuilder();

            document = builder.parse(is);

            Node root = document.getFirstChild();
            xmlNode = document.getFirstChild();
            ParseInformReg(1);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void ParseInformReg(int version) {
        if (xmlNode instanceof Element) {
            Element el = (Element)xmlNode;
            //            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            NodeList nlDoc = el.getChildNodes();
            Node wbHead = el.getFirstChild();
            Node wbContent = el.getFirstChild();
            for (int l = 0; l < nlDoc.getLength(); l++) {
                if (nlDoc.item(l).getNodeName() == "wbr:Header") {
                    wbHead = nlDoc.item(l);
                }
                if (nlDoc.item(l).getNodeName() == "wbr:Content") {
                    wbContent = nlDoc.item(l);
                }
            }
            NodeList nl = wbHead.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName() == "wbr:Identity") {
                    identity = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wbr:WBRegId") {
                    egais_regid = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wbr:EGAISFixNumber") {
                    egais_fix_num = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wbr:EGAISFixDate") {
                    try {
                        egaisFixdate = format.parse(nl.item(i).getTextContent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                if (nl.item(i).getNodeName() == "wbr:Shipper") {
                    if (version == 2) {
                        shiper = new Producer(nl.item(i), conn, egaisFixdate, 2);
                    } else {
                        shiper = new Producer(nl.item(i), conn, egaisFixdate);
                    }
                }
                if (nl.item(i).getNodeName() == "wbr:Consignee") {
                    if (version == 2) {
                        consignee = new Producer(nl.item(i), conn, egaisFixdate, 2);
                    } else {
                        consignee = new Producer(nl.item(i), conn, egaisFixdate);
                    }
                }

                if (nl.item(i).getNodeName() == "wbr:WBNUMBER") {
                    number = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wbr:WBDate") {
                    strDate = nl.item(i).getTextContent();
                    try {
                        date = format.parse(nl.item(i).getTextContent());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }


            NodeList positions = wbContent.getChildNodes();
            PosRegInfo ps;
            posRegInfo = new ArrayList<PosRegInfo>();
            for (int j = 0; j < positions.getLength(); j++) {
                if (positions.item(j).getNodeName() == "wbr:Position") {
                    NodeList posAttr = positions.item(j).getChildNodes();
                    ps = new PosRegInfo();
                    for (int k = 0; k < posAttr.getLength(); k++) {
                        if (posAttr.item(k).getNodeName() == "wbr:Identity") {
                            ps.ident = posAttr.item(k).getTextContent();
                        }
                        if (posAttr.item(k).getNodeName() == "wbr:InformBRegId") {
                            ps.new_form_B = posAttr.item(k).getTextContent();
                        }
                        if (posAttr.item(k).getNodeName() == "wbr:InformF2RegId") {
                            ps.new_form_B = posAttr.item(k).getTextContent();
                        }

                    }
                    posRegInfo.add(ps);
                }
            }

        }
        saveToBaseRegInfo();

    }

    private void saveToBaseRegInfo() {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        boolean typeTTN=false;
        //DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

        if (shiper.clientRegID.equals(Console.SELF_REGID)) {
            sqlQuery =
                String.format(SET_SQL_REGINFO_OUT, null, number, strDate, consignee.f_id, identity, this.egais_regid,
                              this.egais_fix_num, format.format(this.egaisFixdate));
        } else {
            typeTTN=true;
            sqlQuery =
                String.format(SET_SQL_REGINFO, null, number, strDate, shiper.f_id, identity, this.egais_regid,
                              this.egais_fix_num, format.format(this.egaisFixdate));

        }

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
                f_id = rs.getInt("f_id");
                if (rs.getInt("F_REMOVE_URL") > 0) {
                    remove_url = true;
                } else {
                    remove_url = false;
                }
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        PosRegInfo ps;
        if (f_id > 0) {
            for (int i = 0; i < posRegInfo.size(); i++) {
                ps = posRegInfo.get(i);
                sqlQuery = String.format(SET_SQL_REGINFO_STR, f_id, ps.ident, ps.new_form_B,typeTTN ? 1:0);
                try {
                    stmt = conn.prepareStatement(sqlQuery);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        int p_id = rs.getInt("F_ID");
                    }
                } catch (SQLException e) {
                    System.out.println(sqlQuery);
                    e.printStackTrace();
                }

            }
        }


    }

    public void confirmWB(String fileName) throws CloneNotSupportedException, ParserConfigurationException,
                                                  SAXException, IOException {
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
        content.setTextContent(format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wa:WBRegId");
        content.setTextContent(egais_regid);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:Content");
        wbAct.appendChild(content);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);
        //confirmDoc.appendChild(header);


        DOMSource ds = new DOMSource(confirmDoc);
        OutputStream outt;
        StreamResult streamResult = new StreamResult();

        try {
            outt = new FileOutputStream(fileName);
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
        }


    }

    public Node confirmWB(boolean confirm) throws CloneNotSupportedException, ParserConfigurationException,
                                                  SAXException, IOException {
        String getContent = "select * from PR_T_EGAIS_TTN_IN_GET_CONFIRM(%1$s)";
        java.util.Date date = new java.util.Date();
        Document confirmTicket;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));


        Element confirmDoc = //confirmTicket.createElementNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01","WayBillAct" );
            //confirmTicket.createElement("ns:WayBillAct");
            confirmTicket.getDocumentElement();
        Element wbAct = confirmTicket.createElement("ns:WayBillAct_v2");
        Element header = confirmTicket.createElement("wa:Header");
        wbAct.appendChild(header);
        Element accept = confirmTicket.createElement("wa:IsAccept");
        if (confirm) {
            accept.setTextContent("Accepted");
        } else {
            accept.setTextContent("Rejected");
        }
        //header.appendChild(content);
        Element content;
        content = confirmTicket.createElement("wa:ACTNUMBER");
        content.setTextContent(this.number);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:ActDate");
        content.setTextContent(format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wa:WBRegId");
        content.setTextContent(egais_regid);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:Content");
        Element xmlPostion;
        Element xmlPosContent;
        if (confirm) {
            ResultSet rs;
            PreparedStatement stmt;

            String sqlQuery;
            sqlQuery = String.format(getContent, f_id);
            try {
                stmt = conn.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    if (rs.getInt("F_IDENT") > 0) {
                        xmlPostion = confirmTicket.createElement("wa:Position");
                        xmlPosContent = confirmTicket.createElement("wa:Identity");
                        xmlPosContent.setTextContent(rs.getString("F_IDENT"));
                        xmlPostion.appendChild(xmlPosContent);

                        xmlPosContent = confirmTicket.createElement("wa:RealQuantity");
                        xmlPosContent.setTextContent(String.valueOf(rs.getInt("F_QUANT")));
                        xmlPostion.appendChild(xmlPosContent);


                        xmlPosContent = confirmTicket.createElement("wa:InformF2RegId");
                        xmlPosContent.setTextContent(rs.getString("F_FORM_B_EGAIS"));
                        xmlPostion.appendChild(xmlPosContent);
                        content.appendChild(xmlPostion);
                    }

                }
            } catch (SQLException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();
            }

        }
        wbAct.appendChild(content);
        if (content.hasChildNodes()) {

            accept.setTextContent("Differences");
        }
        header.appendChild(accept);

        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);
        confirmDoc.normalize();


        return confirmDoc;
    }

    public void SetWB_GUID(String guid) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery = String.format(SET_SQL_UID_WB, guid, this.f_id);
        try {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.execute();
            //            rs=
            //                stmt.executeQuery();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public Node confirmWBTicket(String param) throws ParserConfigurationException, SAXException, IOException {
        java.util.Date date = new java.util.Date();
        Document confirmTicket;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));


        Element confirmDoc = //confirmTicket.createElementNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01","WayBillAct" );
            //confirmTicket.createElement("ns:WayBillAct");
            confirmTicket.getDocumentElement();

        Element wbAct = confirmTicket.createElement("ns:ConfirmTicket");
        Element header = confirmTicket.createElement("wt:Header");
        wbAct.appendChild(header);
        Element content = confirmTicket.createElement("wt:IsConfirm");
        content.setTextContent(param);
        header.appendChild(content);
        content = confirmTicket.createElement("wt:TicketNumber");
        content.setTextContent(this.number);
        header.appendChild(content);
        content = confirmTicket.createElement("wt:TicketDate");
        content.setTextContent(format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wt:WBRegId");
        content.setTextContent(egais_regid);
        header.appendChild(content);
        content = confirmTicket.createElement("wt:Note");
        wbAct.appendChild(content);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);


        /*        <ns:ConfirmTicket>
        <wt:Header>
         <wt:IsConfirm>Accepted</wt:IsConfirm>
         <wt:TicketNumber>00008</wt:TicketNumber>
         <wt:TicketDate>2016-01-14</wt:TicketDate>
         <wt:WBRegId>TTN-0001597887</wt:WBRegId>
         <wt:Note>Принимаем</wt:Note>
        </wt:Header>
        </ns:ConfirmTicket>*/


        return confirmDoc;

    }

    public void SaveSourceToFile(String fileName) throws SQLException, IOException {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;

        Blob source = null;
        String get_source = "select f_source,f_fix_source from t_egais_in_ttn where f_id=%1$s";
        ByteArrayInputStream inp; // = new ByteArrayInputStream();
        FileOutputStream fl;
        sqlQuery = String.format(get_source, f_id);

        stmt = conn.prepareStatement(sqlQuery);
        rs = stmt.executeQuery();
        rs.next();
        byte[] b_source;

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
        fl = new FileOutputStream(new File(fileName + "//" + String.valueOf(f_id) + ".xml"));
        //        inp = new ByteArrayInputStream(b_source);
        fl.write(b_source);
        fl.close();
        source = rs.getBlob("F_FIX_SOURCE");
        if (source != null) {
            b_source = source.getBytes(1, (int)source.length());
            g = (int)source.length();
            for (int k = 0; k < b_source.length; k++) {
                if (k > 1) {
                    if (b_source[k - 1] == (byte)208 && b_source[k] == (byte)0) {
                        b_source[k - 1] = (byte)208;
                        b_source[k] = (byte)152;
                    }
                }
            }
            fl = new FileOutputStream(new File(fileName + "//" + String.valueOf(f_id) + "_FIX.xml"));
            //        inp = new ByteArrayInputStream(b_source);
            fl.write(b_source);
        }

    }
}

