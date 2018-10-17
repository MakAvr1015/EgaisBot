package egaisbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

import ru.fsrar.wegais.actwriteoff_v3.ActWriteOffPositionType;
import ru.fsrar.wegais.actwriteoff_v3.ActWriteOffTypeV3;
import ru.fsrar.wegais.actwriteoff_v3.InformF1F2;
import ru.fsrar.wegais.commonenum.TypeWriteOff;
import ru.fsrar.wegais.productref_v2.InformF1Type;
import ru.fsrar.wegais.productref_v2.InformF2TypeItem;
import ru.fsrar.wegais.wb_doc_single_01.Documents;

public class ActWriteOff {
    private static String GET_SQL = "select * from PR_T_EGAIS_ACT_WRITE_OFF_GET(%1$s)";
    private static String GET_SQL_STR = "select * from PR_T_EGAIS_ACT_WRITE_OFF_S_GET(%1$s)";
    private static String SET_SQL_GUID = "execute procedure PR_EGAIS_ACT_WRITE_OFF_SET_GUID(%1$s,%2$s)";
    private int f_id;
    private String f_number;
    private Date f_date;
    private int f_state;
    private int f_type;
    private String f_descr;
    private String f_awo_type;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public class AWPosition {
        //        private static String GET_SQL="";
        public int f_id;
        public int f_quant;
        public int f_product;
        public int f_form_b;
        public int f_form_a;
        public String f_product_name;
        public String f_product_egais;
        public String f_form_b_egais;
        public String f_form_a_egais;

        public AWPosition() {

        }
    }
    private ArrayList<AWPosition> positions;
    private Connection conn;

    public ActWriteOff(int p_id, Connection lConn) {
        super();
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        if (p_id > 0) {
            f_id = p_id;
        }
        conn = lConn;
        sqlQuery = String.format(GET_SQL, f_id);

        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_number = rs.getString("F_NUMBER");
                f_date = rs.getDate("F_DATE");
                f_state = rs.getInt("F_STATE");
                f_descr = rs.getString("F_DESCR");
                f_awo_type = rs.getString("F_AWO_TYPE");
                f_type = rs.getInt("F_TYPE");
            }
            sqlQuery = String.format(GET_SQL_STR, f_id);
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            positions = new ArrayList<AWPosition>();
            AWPosition lPos;
            while (rs.next()) {
                lPos = new AWPosition();
                lPos.f_id = rs.getInt("F_ID");
                lPos.f_product = rs.getInt("F_PRODUCT");
                lPos.f_form_b = rs.getInt("F_FORM_B");
                lPos.f_quant = rs.getInt("F_QUANT");
                lPos.f_product_egais = rs.getString("F_PRODUCT_EGAIS");
                lPos.f_product_name = rs.getString("F_PRODUCT_NAME");
                lPos.f_form_b_egais = rs.getString("F_FORM_B_EGAIS_NEW");
                lPos.f_form_a = rs.getInt("F_FORM_A");
                lPos.f_form_a_egais = rs.getString("F_FORM_A_EGAIS");
                positions.add(lPos);
            }


        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();

        }
    }

    public void FormXMLFile(String fileName) throws CloneNotSupportedException, ParserConfigurationException,
                                                    SAXException, IOException {
        Document confirmTicket;
        //        java.util.Date date=new java.util.Date();
        Node root = Console.xmlTemplate.cloneNode(true);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));


        Element content;
        Element body;
        Element pos;
        AWPosition l_awp;
        Element formF1F2;
        Element form_b;
        Element confirmDoc = confirmTicket.getDocumentElement();
        Element wbAct = confirmTicket.createElement("ns:ActWriteOff_v2");
        Element header = confirmTicket.createElement("awr:Header");
        wbAct.appendChild(header);

        content = confirmTicket.createElement("awr:ActNumber");
        content.setTextContent(this.f_number);
        header.appendChild(content);
        content = confirmTicket.createElement("awr:ActDate");
        content.setTextContent(format.format(f_date));
        header.appendChild(content);
        content = confirmTicket.createElement("awr:TypeWriteOff");
        content.setTextContent(f_awo_type);
        header.appendChild(content);
        content = confirmTicket.createElement("awr:Note");
        content.setTextContent(f_descr);
        header.appendChild(content);

        body = confirmTicket.createElement("awr:Content");

        for (int i = 0; i < positions.size(); i++) {
            pos = confirmTicket.createElement("awr:Position");
            l_awp = positions.get(i);
            content = confirmTicket.createElement("awr:Identity");
            content.setTextContent(Integer.toString(i + 1));
            pos.appendChild(content);
            content = confirmTicket.createElement("awr:Quantity");
            content.setTextContent(Integer.toString(l_awp.f_quant));
            pos.appendChild(content);

            content = confirmTicket.createElement("awr:InformF1F2");
            formF1F2 = confirmTicket.createElement("awr:InformF2");
            form_b = confirmTicket.createElement("pref:F2RegId");
            form_b.setTextContent(l_awp.f_form_b_egais);
            formF1F2.appendChild(form_b);
            content.appendChild(formF1F2);

            pos.appendChild(content);
            body.appendChild(pos);
        }
        wbAct.appendChild(body);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);

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

    public Node GetXML() throws ParserConfigurationException, SAXException, IOException {
        Document confirmTicket;
        //        java.util.Date date=new java.util.Date();
        Node root = Console.xmlTemplate.cloneNode(true);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        confirmTicket = db.parse(new File(Console.XML_TEMPLATE));

        Element formF1F2;
        Element content;
        Element body;
        Element pos;
        AWPosition l_awp;
        Element form_b;
        Element confirmDoc = confirmTicket.getDocumentElement();
        Element wbAct = confirmTicket.createElement("ns:ActWriteOff_v2");
        Element header = confirmTicket.createElement("awr:Header");
        wbAct.appendChild(header);

        content = confirmTicket.createElement("awr:ActNumber");
        content.setTextContent(this.f_number);
        header.appendChild(content);
        content = confirmTicket.createElement("awr:ActDate");
        content.setTextContent(format.format(f_date));
        header.appendChild(content);
        content = confirmTicket.createElement("awr:TypeWriteOff");
        content.setTextContent(f_awo_type);
        header.appendChild(content);
        content = confirmTicket.createElement("awr:Note");
        content.setTextContent(f_descr);
        header.appendChild(content);

        body = confirmTicket.createElement("awr:Content");

        for (int i = 0; i < positions.size(); i++) {
            pos = confirmTicket.createElement("awr:Position");
            l_awp = positions.get(i);
            content = confirmTicket.createElement("awr:Identity");
            content.setTextContent(Integer.toString(i + 1));
            pos.appendChild(content);
            content = confirmTicket.createElement("awr:Quantity");
            content.setTextContent(Integer.toString(l_awp.f_quant));
            pos.appendChild(content);
            content = confirmTicket.createElement("awr:InformF1F2");
            formF1F2 = confirmTicket.createElement("awr:InformF2");
            form_b = confirmTicket.createElement("pref:F2RegId");
            form_b.setTextContent(l_awp.f_form_b_egais);
            formF1F2.appendChild(form_b);
            content.appendChild(formF1F2);
            pos.appendChild(content);
            body.appendChild(pos);
        }
        wbAct.appendChild(body);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);

        return confirmDoc;
    }

    public ActWriteOffTypeV3 GetActWriteOffV3() {
        ActWriteOffTypeV3 avOff;
        avOff = new ActWriteOffTypeV3();
        ActWriteOffTypeV3.Header avHead;
        avOff.setIdentity(f_number);
        avHead = new ActWriteOffTypeV3.Header();
        avHead.setActNumber(f_number);
        
        avHead.setNote(f_descr);
        switch (f_type) {
        case 1:
            avHead.setTypeWriteOff(TypeWriteOff.ПЕРЕСОРТИЦА);
            break;
        case 2:
            avHead.setTypeWriteOff(TypeWriteOff.НЕДОСТАЧА);
            break;
        case 3:
            avHead.setTypeWriteOff(TypeWriteOff.УЦЕНКА);
            break;
        case 4:
            avHead.setTypeWriteOff(TypeWriteOff.ПОРЧА);
            break;
        case 5:
            avHead.setTypeWriteOff(TypeWriteOff.ПОТЕРИ);
            break;
        case 6:
            avHead.setTypeWriteOff(TypeWriteOff.ПРОВЕРКИ);
            break;
        case 7:
            avHead.setTypeWriteOff(TypeWriteOff.АРЕСТ);
            break;

        }
        GregorianCalendar gregory = new GregorianCalendar();
        gregory.setTime(f_date);
        
        XMLGregorianCalendar calendar = null;
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(gregory.get(Calendar.YEAR) ,gregory.get(Calendar.MONTH)+1,gregory.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
                                      //.newXMLGregorianCalendar(gregory);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        avHead.setActDate(calendar);
        avOff.setHeader(avHead);
        ActWriteOffTypeV3.Content avContent;
        avContent = new ActWriteOffTypeV3.Content();
        ActWriteOffPositionType pr;
        for (int i = 0; i < positions.size(); i++) {
            pr = new ActWriteOffPositionType();
            pr.setIdentity(Integer.toString(i + 1));
            pr.setQuantity(BigDecimal.valueOf(positions.get(i).f_quant));
            InformF1F2 ifa = new InformF1F2();
/*            ifa.setInformF1(new InformF1Type());
            ifa.getInformF1().setRegId(positions.get(i).f_form_a_egais);*/
            ifa.setInformF2(new InformF2TypeItem());
            ifa.getInformF2().setF2RegId(positions.get(i).f_form_b_egais);
            pr.setInformF1F2(ifa);
            avContent.getPosition().add(pr);
        }
        avOff.setContent(avContent);
        return avOff;
    }

    public void Set_GUID(String p_guid) {
        PreparedStatement stmt;
        String sqlQuery;
        if (p_guid != null) {
            p_guid = "'" + p_guid + "'";
        } else
            p_guid = "null";
        sqlQuery = String.format(SET_SQL_GUID, this.f_id, p_guid);
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
}
