package egaisbot;

import egaisbot.ActWriteOff.AWPosition;

import java.io.File;

import java.io.IOException;

//import java.security.Identity;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class WayBillOut {
    private Connection conn;
    private int f_id;
    private String f_number;
    private String f_ttn_type;
    private Date f_dat;
    private String f_tran_type;
    private String f_tran_company;
    private String f_tran_car;
    private String f_tran_customer;
    private String f_tran_driver;
    private String f_tran_loadpoint;
    private String f_egaisId;
    private String f_tran_unloadpoint;
    private Producer f_shiper; //Отправитель
    private Producer f_consignee; //Получатель
    private ArrayList<AWPosition> posList;
    private ArrayList<WBPosition> positions; //<Product> positions;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static String URL_TARGET = "/in/WayBill_v3";//"/in/WayBill_v2";// "/in/WayBill";
    public static String GET_SQL_WB = "select * from PR_T_EGAIS_TTN_OUT_GET(%1$s)";
    public static String GET_SQL_WB_STR_S = "select * from PR_T_EGAIS_TTN_OUT_STR_S(%1$s)";
    public static String SET_SQL_GUID = "execute procedure PR_T_EGAIS_TTN_OUT_STATE_SET(%1$s,%2$s,%3$s)";

    public WayBillOut(int p_id, Connection lConn) {
        super();
        conn = lConn;
        int str_id;
        int f_consign_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery, sqlPos;
        posList = new ArrayList<AWPosition>();
        positions = new ArrayList<WBPosition>();
        //DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        sqlQuery = String.format(GET_SQL_WB, p_id);
        System.out.println(String.valueOf(p_id));
        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                this.f_number = rs.getString("F_NUMBER");
                this.f_dat = rs.getDate("F_DATE");
                f_ttn_type=rs.getString("F_TTN_TYPE");
                f_consign_id = rs.getInt("F_PARTNER");
                f_tran_type = rs.getString("F_TRAN_TYPE");
                f_tran_company = rs.getString("F_TRAN_COMPANY");
                f_tran_car = rs.getString("F_TRAN_CAR");
                f_tran_customer = rs.getString("F_TRAN_CUSTOMER");
                f_tran_driver = rs.getString("F_TRAN_DRIVER");
                f_tran_loadpoint = rs.getString("F_TRAN_LOADPOINT");
                f_tran_unloadpoint = rs.getString("F_TRAN_UNLOADPOINT");
                f_egaisId = rs.getString("F_REGID");
            }
            if (f_consign_id > 0) {
                f_consignee = new Producer(f_consign_id, conn);
            }
            f_shiper = new Producer(2280, conn);
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        sqlQuery = String.format(GET_SQL_WB_STR_S, p_id);
        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            WBPosition pr;

            while (rs.next()) {

                sqlPos = String.format("select * from t_egais_ttn_out_positions where f_id=%1$s", rs.getInt("F_ID"));
                pr = new WBPosition(sqlPos, conn);
                //                pr = new Product(rs.getInt("F_ID"), conn);
                positions.add(pr);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        for (int i = 0; i < positions.size(); i++) {
            positions.get(i).getFromDB();
        }

    }

    public void SetState(String p_guid, int p_state) {
        PreparedStatement stmt;
        String sqlQuery;
        if (p_guid != null) {
            p_guid = "'" + p_guid + "'";
        }
        sqlQuery = String.format(SET_SQL_GUID, f_id, p_state, p_guid);

        try {
            stmt = conn.prepareStatement(sqlQuery);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    public Document GetXML(int version) throws ParserConfigurationException, SAXException, IOException, SQLException {
        DocumentBuilderFactory builderFactory;
        DocumentBuilder builder = null;
        builderFactory = DocumentBuilderFactory.newInstance();
        builder = builderFactory.newDocumentBuilder();
        Document result = builder.parse(new File(Console.XML_TEMPLATE));
        Element wbOut = result.createElement("ns:WayBill_v2");//"ns:WayBill");
        Element content = result.createElement("wb:Identity");
        content.setTextContent(String.valueOf(f_id));
        wbOut.appendChild(content);
        Element header = result.createElement("wb:Header");
        content = result.createElement("wb:NUMBER");
        content.setTextContent(f_number);
        header.appendChild(content);
        content = result.createElement("wb:Date");
        content.setTextContent(format.format(f_dat));
        header.appendChild(content);
        content = result.createElement("wb:ShippingDate");
        content.setTextContent(format.format(f_dat));
        header.appendChild(content);

        content = result.createElement("wb:Type");
        content.setTextContent(f_ttn_type);
        //content.setTextContent("WBInvoiceFromMe");
        header.appendChild(content);
/*        content = result.createElement("wb:UnitType");
        content.setTextContent("Packed");
        header.appendChild(content);*/
        content = result.createElement("wb:Shipper");
        content = (Element)f_shiper.GetXML_v2(content);//GetXml(content);
        header.appendChild(content);
        content = result.createElement("wb:Consignee");
        content = (Element)f_consignee.GetXML_v2(content);//GetXml(content);
        header.appendChild(content);
        Element transport = result.createElement("wb:Transport");
        content = result.createElement("wb:TRAN_TYPE");
        content.setTextContent(f_tran_type);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_COMPANY");
        content.setTextContent(f_tran_company);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_CAR");
        content.setTextContent(f_tran_car);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_CUSTOMER");
        content.setTextContent(f_tran_customer);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_DRIVER");
        content.setTextContent(f_tran_driver);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_LOADPOINT");
        content.setTextContent(f_tran_loadpoint);
        transport.appendChild(content);

        content = result.createElement("wb:TRAN_UNLOADPOINT");
        content.setTextContent(f_tran_unloadpoint);
        transport.appendChild(content);


        header.appendChild(transport);
        content = result.createElement("wb:Content");

        WBPosition pr;
        for (int k = 0; k < positions.size(); k++) {
            pr = positions.get(k);
            Element el = result.createElement("wb:Position");
            content.appendChild(pr.getXml(el,version));
        }
        wbOut.appendChild(header);
        wbOut.appendChild(content);
        NodeList nl = result.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbOut);

        //        result.appendChild(wbOut)        ;
        return result;
    }

    public Node GetAccept(String p_confirm) {

        java.util.Date date = new java.util.Date();
        Document confirmTicket=null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            confirmTicket = db.parse(new File(Console.XML_TEMPLATE));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Element confirmDoc = //confirmTicket.createElementNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01","WayBillAct" );
            //confirmTicket.createElement("ns:WayBillAct");
            confirmTicket.getDocumentElement();
        Element wbAct = confirmTicket.createElement("ns:WayBillAct_v2");
        Element header = confirmTicket.createElement("wa:Header");
        wbAct.appendChild(header);
        Element content = confirmTicket.createElement("wa:IsAccept");
        content.setTextContent(p_confirm);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:ACTNUMBER");
        content.setTextContent(this.f_number);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:ActDate");
        content.setTextContent(EGAISparser.format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wa:WBRegId");
        content.setTextContent(f_egaisId);
        header.appendChild(content);
        content = confirmTicket.createElement("wa:Content");
        wbAct.appendChild(content);
        NodeList nl = confirmDoc.getElementsByTagName("ns:Document");
        nl.item(0).appendChild(wbAct);
        confirmDoc.normalize();


        return confirmDoc;


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
        content.setTextContent(this.f_number);
        header.appendChild(content);
        content = confirmTicket.createElement("wt:TicketDate");
        content.setTextContent(format.format(date));
        header.appendChild(content);
        content = confirmTicket.createElement("wt:WBRegId");
        content.setTextContent(f_egaisId);
        header.appendChild(content);
        content = confirmTicket.createElement("wt:Note");
        if (param.compareTo("Accepted")==0){
            content.setTextContent("Подтверждаем"); 
        }else {
            content.setTextContent("Не подтверждаем");
        }
        header.appendChild(content);
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
    
}
