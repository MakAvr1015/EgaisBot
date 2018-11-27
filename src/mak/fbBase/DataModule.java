package mak.fbBase;

import egaisbot.ActMarkOff;
import egaisbot.ActWriteOff;

import egaisbot.ActWriteOff.AWPosition;

import egaisbot.Console;
import egaisbot.EGAISparser;


import egaisbot.HTTPQueue;
import egaisbot.Producer;
import egaisbot.WBPosition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import java.io.InputStream;

import java.math.BigDecimal;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import ru.fsrar.wegais.actinventoryinformf2reg.ActInventoryInformF2Reg;
import ru.fsrar.wegais.actttnsingle_v3.AcceptType;
import ru.fsrar.wegais.actttnsingle_v3.WayBillActTypeV3;
import ru.fsrar.wegais.clientref_v2.FLType;
import ru.fsrar.wegais.clientref_v2.FOType;
import ru.fsrar.wegais.clientref_v2.OrgAddressTypeFOTS;
import ru.fsrar.wegais.clientref_v2.OrgAddressTypeULFL;
import ru.fsrar.wegais.clientref_v2.OrgInfoExV2;
import ru.fsrar.wegais.clientref_v2.OrgInfoReplyV2;
import ru.fsrar.wegais.clientref_v2.OrgInfoRusV2;
import ru.fsrar.wegais.clientref_v2.OrgInfoV2;
import ru.fsrar.wegais.clientref_v2.TSType;
import ru.fsrar.wegais.clientref_v2.ULType;
import ru.fsrar.wegais.commonv3.AMCforDocType;
import ru.fsrar.wegais.commonv3.BoxamcType;
import ru.fsrar.wegais.commonv3.Boxtype;
import ru.fsrar.wegais.commonv3.InformF2TypeItemBC;
import ru.fsrar.wegais.commonv3.MarkInfoTypeBC;
//import ru.fsrar.wegais.productref_v2.InformF1Type;
import ru.fsrar.wegais.productref_v2.InformF2Type;
import ru.fsrar.wegais.productref_v2.ProductInfoReplyV2;
import ru.fsrar.wegais.productref_v2.ProductInfoV2;
import ru.fsrar.wegais.productref_v2.ProductType;
import ru.fsrar.wegais.replyap.ReplyAP;
import ru.fsrar.wegais.replyap_v2.ReplyAPV2;
import ru.fsrar.wegais.replyclient_v2.ReplyClientV2;
import ru.fsrar.wegais.replyform1.ReplyForm1;
import ru.fsrar.wegais.replyform2.ReplyForm2;
import ru.fsrar.wegais.replyhistform2.ReplyHistForm2;
import ru.fsrar.wegais.replyrestbcode.ReplyRestBCode;
import ru.fsrar.wegais.replyrests.ReplyRests;
import ru.fsrar.wegais.replyrests.StockPositionType;
import ru.fsrar.wegais.replyrests_v2.ReplyRestsV2;
import ru.fsrar.wegais.ticket.TicketType;
import ru.fsrar.wegais.ttninformf2reg.InformF2PositionType;
import ru.fsrar.wegais.ttninformf2reg.WayBillInformF2RegType;
import ru.fsrar.wegais.ttnsingle_v2.PositionType;
import ru.fsrar.wegais.ttnsingle_v2.WayBillTypeV2;
import ru.fsrar.wegais.ttnsingle_v3.TransportType;
import ru.fsrar.wegais.ttnsingle_v3.WayBillTypeV3;
import ru.fsrar.wegais.ttnsingle_v3.WbType;
import ru.fsrar.wegais.ttnsingle_v3.WbUnitType;
import ru.fsrar.wegais.wb_doc_single_01.DocBody;
import ru.fsrar.wegais.wb_doc_single_01.Documents;
import ru.fsrar.wegais.wb_doc_single_01.SenderInfo;

public class DataModule {
    public static final String WB_OUT = "WayBillOut";
    public static final String WB_IN_ACCEPT = "WayBillAccept";
    public static final String ACT_WRITE_OFF = "ActWriteOff";
    public static final String ACT_MARK_OFF = "ActMarkOff";
    private static String SELF_FSRAR_ID = "010000002477";
    public static final String WB_IN_REJECT = "WayBillReject";
    public static final String WB_OUT_REJECT = "WayBillOutReject";
    private static String SET_FORM1 =
        "execute procedure PR_T_EGAIS_FORM_A_REQ_SET('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s',%7$s,%8$s,null,%9$s,%10$s)"; ;
    private static String SET_REGINFO_OUT =
        "select * from PR_T_EGAIS_TTN_OUT_FORMREG_SET(%1$s,'%2$s','%3$s',%4$s,%5$s,'%6$s',%7$s,'%8$s',null)";
    private static String SET_REPLY_F2 = "select * from PR_T_EGAIS_REPLY_F2('%1$s','%2$s',%3$s,%4$s,'%5$s','%6$s')";

    Connection connection;

    private static String SET_TICKET =
        "select * from PR_T_EGAIS_TICKET_SET('%1$s'," +
        "'%2$s',%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s,%10$s,null,null)"; //,?,?" + ")";
    private static String SET_WBHEAD_IN =
        "select f_id,f_remove_url from PR_T_EGAIS_TTN_SET" +
        "('%1$s','%2$s','%3$s',%4$s,%5$s,%6$s,%7$s,%8$s,'%9$s',%10$s,null)";
    private static String SET_PARTNER =
        "select f_id from PR_T_EGAIS_PARTNER_SET(%1$s,%2$s,%3$s,%4$s,%5$s,%6$s,%7$s,null,%8$s,null,%9$s,%10$s,%11$s)";
    private static String SET_WBPOS_IN =
        "SELECT * FROM PR_T_EGAIS_TTN_POSITION_SET(%1$s, '%2$s', %3$s, %4$s, %5$s, %6$s, null)";
    private static String SET_PRODUCT =
        "select f_id from PR_T_EGAIS_PRODUCT_SET('%1$s','%2$s','%3$s','%4$s',%5$s,%6$s,'%7$s',%8$s,%9$s,null,%10$s)";
    private static String SET_InformF1 = "SELECT * FROM PR_T_EGAIS_FORM_A_SET(%1$s, '%2$s')";
    private static String SET_InformF2 = "SELECT * FROM PR_T_EGAIS_FORM_B_SET(%1$s, '%2$s', %3$s)";
    private static String SET_WBACT_IN =
        "select * from PR_T_EGAIS_TTN_ACT_SET('%1$s','%2$s','%3$s','%4$s','%5$s',null)";
    private static String SET_WBACT_IN_POS = "select f_id from PR_T_EGAIS_TTN_ACT_POS_SET(%1$s,%2$s,'%3$s',%4$s)";
    private static String SET_AMC = "select * from PR_T_EGAIS_AMC_SET('%1$s','%2$s',%3$s)";
    private static String GET_WB_OUT = "select * from PR_T_EGAIS_TTN_OUT_GET(%1$s)";
    private static String GET_WB_OUT_STR_S = "select * from PR_EGAIS_TTN_OUT_POS_S(%1$s)";
    private static String GET_PARTNER = "select * from PR_T_EGAIS_PARTNER_GET(%1$s)";
    private static String GET_PRODUCT = "select * from pr_t_egais_product_get(%1$s,%2$s)";
    private static String GET_WB_IN = "select * from PR_T_EGAIS_TTN_GET(%1$s)";
    private static String SET_REST_HEAD = "select * from PR_T_EGAIS_REST_SET('%1$s','%2$s',null)";
    private static String SET_REST_STR = "select * from PR_T_EGAIS_REST_STR_SET(%1$s,'%2$s','%3$s','%4$s',%5$s)";
    private static String SET_SQL_REGINFO_OUT =
        "select * from PR_T_EGAIS_TTN_OUT_FORMREG_SET" + "(%1$s,'%2$s','%3$s',%4$s,'%5$s','%6$s','%7$s','%8$s',null)";

    private static String SET_SQL_REGINFO =
        "select * from PR_T_EGAIS_TTN_FORMREG_SET" + "(%1$s,'%2$s','%3$s',%4$s,'%5$s','%6$s','%7$s','%8$s',null)";
    private static String SET_SQL_REGINFO_STR =
        "select f_id from PR_T_EGAIS_TTN_FORMREG_SET_S(" + "%1$s,'%2$s','%3$s',%4$s)";
    private static String SET_AMC_UNACTIVE = "execute procedure PR_T_EGAIS_AMC_SET_UNACTIVE('%1$s')";
    private static String SET_AMC2 = "execute procedure PR_T_EGAIS_AMC_SET2('%1$s', '%2$s')";
    private static DataModule dm;


    public static synchronized DataModule getInstance() {
        if (dm == null)
            dm = new DataModule();
        return dm;
    }

    public DataModule() {
        super();
    }

    public void setConnection(Connection cn) {
        if (connection == null)
            connection = cn;
    }

    public Documents GetDocuments(int p_id, String p_docType) {
        Documents documents = new Documents();
        documents.setOwner(new SenderInfo());
        documents.getOwner().setFSRARID(SELF_FSRAR_ID);
        DocBody doc = new DocBody();
        switch (p_docType) {
        case WB_OUT:
            doc.setWayBillV3(LoadWBOut_V3(p_id));
            break;
        case WB_IN_ACCEPT:
            doc.setWayBillActV3(LoadWBIn_act(p_id, true));
            break;
        case WB_IN_REJECT:
            doc.setWayBillActV3(LoadWBIn_act(p_id, false));
            break;
        case WB_OUT_REJECT:
            doc.setWayBillActV3(LoadWBOut_act(p_id, false));
            break;
        case ACT_WRITE_OFF:
            ActWriteOff awo = new ActWriteOff(p_id, connection);
            doc.setActWriteOffV3(awo.GetActWriteOffV3());
            break;
        case ACT_MARK_OFF:
            ActMarkOff amo = new ActMarkOff(p_id, connection);
            doc.setActUnFixBarCode(amo.GetActMarkOff());
            break;
        }


        documents.setDocument(doc);
        return documents;
    }

    private boolean SaveTicket(TicketType p_Ttype) {
        boolean result = false;

        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        String lstrOperDate, lsOperRes, lsOperComment;
        DateFormat lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (p_Ttype.getOperationResult() != null) {
                lstrOperDate = "'" + lformat.format(p_Ttype.getTicketDate().toGregorianCalendar().getTime()) + "'";
                lsOperRes = "'" + p_Ttype.getOperationResult().getOperationResult().value() + "'";
                lsOperComment = "'" + p_Ttype.getOperationResult().getOperationComment() + "'";
            } else {
                lstrOperDate = "null";
                lsOperRes = "null";
                lsOperComment = "null";
            }
            sqlQuery =
                String.format(SET_TICKET, p_Ttype.getTransportId(),
                              lformat.format(p_Ttype.getTicketDate().toGregorianCalendar().getTime()),
                              "'" + p_Ttype.getRegID() + "'", "'" + p_Ttype.getDocType() + "'",
                              p_Ttype.getResult() != null ?
                              "'" + p_Ttype.getResult().getConclusion().value().replace("'", "''") + "'" : "null",
                              p_Ttype.getResult() != null ?
                              "'" + p_Ttype.getResult().getComments().replace("'", "''") + "'" : "null",
                              "'" + p_Ttype.getDocType() + "'", lstrOperDate, lsOperRes, lsOperComment);
            //            DOMSource ds = new DOMSource(resNode);
            ByteArrayOutputStream outt = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(outt);

            //            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
            //            InputStream ts = new ByteArrayInputStream(outt.toByteArray());
            stmt = connection.prepareStatement(sqlQuery);
            /*            if (resNode != null) {
                stmt.setBlob(1, ts);
            } else
                stmt.setNull(1, java.sql.Types.BLOB);*/
            //.setBlob(1,)
            //            ds = new DOMSource(operNode);
            //            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
            /*            ts = new ByteArrayInputStream(outt.toByteArray());
            if (operNode != null) {
                stmt.setBlob(2, ts);
            } else
                stmt.setNull(2, java.sql.Types.BLOB);*/
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("f_remove_url") == 1) {
                    result = true;
                }
            }


        } /* catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }*/ catch (SQLException e) {
            //            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return result;

    }

    public boolean saveDocuments(Documents documents, int p_recno, String p_guid) {
        boolean result = false;
        if (documents.getDocument() != null) {
            /*            try{
            JAXBContext context = JAXBContext.newInstance(Documents.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(documents, new File("d:\\doc.xml"));
            }
            catch (JAXBException e) {
                        e.printStackTrace();
                    }            */
            if (documents.getDocument().getTicket() != null) {
                result = SaveTicket(documents.getDocument().getTicket());
            }
            if (documents.getDocument().getWayBillV3() != null) {
                result = SaveWayBillV3(documents.getDocument().getWayBillV3(), p_recno);
            }
            if (documents.getDocument().getTTNInformF2Reg() != null) {
                result = SavetTTNInformF2Reg(documents.getDocument().getTTNInformF2Reg());


            }
            if (documents.getDocument().getWayBillActV3() != null) {
                result = SaveWayBillActV3(documents.getDocument().getWayBillActV3(), p_recno);
            }
            if (documents.getDocument().getReplyRestsV2() != null) {
                result = SaveRestsV2(documents.getDocument().getReplyRestsV2(), p_guid);
            }
            if (documents.getDocument().getReplyForm1() != null) {
                result = SaveReplyForm1(documents.getDocument().getReplyForm1());
            }
            if (documents.getDocument().getReplyClientV2() != null) {
                result = SaveReplyClientV2(documents.getDocument().getReplyClientV2());
            }
            /*            if (documents.getDocument().getActInventoryInformF2Reg()!=null){
                result =SaveActInventoryInformF2Reg(documents.getDocument().getActInventoryInformF2Reg());
            }*/
            /*            if (documents.getDocument().getReplyHistForm2()!=null){
                result = saveReplyHistForm2(documents.getDocument().getReplyHistForm2());
            }*/
            if (documents.getDocument().getReplyForm2() != null) {
                result = SaveReplyForm2(documents.getDocument().getReplyForm2(), p_guid);
            }
            if (documents.getDocument().getReplyRestBCode() != null) {
                result = SaveReplyRestBCode(documents.getDocument().getReplyRestBCode(), p_guid);
            }
            if (documents.getDocument().getReplyAP() != null) {
                result = SaveReplyAp(documents.getDocument().getReplyAP(), p_guid);
            }
            if (documents.getDocument().getReplyAPV2() != null) {
                result = SaveReplyApV2(documents.getDocument().getReplyAPV2(), p_guid);
            }

        }
        return result;
    }

    private boolean SaveWayBillV3(WayBillTypeV3 wayBillTypeV3, int p_urlNo) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        boolean result = false;
        int f_head = 0;
        WayBillTypeV3.Header header = wayBillTypeV3.getHeader();
        int f_consignee = SaveOrgInfoV2(header.getConsignee());
        int f_shiper = SaveOrgInfoRusV2(header.getShipper());
        sqlQuery =
            String.format(SET_WBHEAD_IN, header.getNUMBER(),
                          EGAISparser.format.format(header.getDate().toGregorianCalendar().getTime()),
                          header.getType().value(), null, f_shiper, f_consignee, null, null,
                          wayBillTypeV3.getIdentity(), p_urlNo);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_head = rs.getInt("F_ID");
                /*                if (rs.getInt("f_remove_url") == 1) {
                    this.remove_url = true;
                }*/
            }
            result = true;
        } catch (SQLException e) {
            result = false;
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        if (result) {
            List<ru.fsrar.wegais.ttnsingle_v3.PositionType> positions = wayBillTypeV3.getContent().getPosition();
            for (int i = 0; i < positions.size(); i++) {
                result = SaveWbPositionV3(positions.get(i), f_head, "WB_IN");
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

    private void SaveWayBillV2(WayBillTypeV2 wayBillTypeV2, int p_urlNo) {

        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        int f_head = 0;

        WayBillTypeV2.Header header = wayBillTypeV2.getHeader();
        int f_consignee = SaveOrgInfoV2(header.getConsignee());
        int f_shiper = SaveOrgInfoRusV2(header.getShipper());
        sqlQuery =
            String.format(SET_WBHEAD_IN, header.getNUMBER(),
                          EGAISparser.format.format(header.getDate().toGregorianCalendar().getTime()),
                          header.getType().value(), null, f_shiper, f_consignee, null, null,
                          wayBillTypeV2.getIdentity(), p_urlNo);

        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_head = rs.getInt("F_ID");
                /*                if (rs.getInt("f_remove_url") == 1) {
                    this.remove_url = true;
                }*/
            }

        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        List<PositionType> positions = wayBillTypeV2.getContent().getPosition();
        for (int i = 0; i < positions.size(); i++) {
            SaveWbPositionV2(positions.get(i), f_head, "INPUT");
        }

    }

    private int SaveOrgInfoV2(OrgInfoV2 orgInfoV2) {
        int f_id = 0;
        String v_inn = null, v_kpp = null, v_fullName = null, v_shortName = null, v_clientRegID = null, v_address =
            null, v_country = null, v_clientType = null, v_versionWb = null, v_regionCode = null;
        java.util.Date v_date;
        v_date = new java.util.Date();
        ResultSet rs;
        PreparedStatement stmt;
        if (orgInfoV2.getFL() != null) {
            FLType flClient = orgInfoV2.getFL();
            v_clientType = "FL";
            v_inn = flClient.getINN();
            v_shortName = flClient.getShortName();
            v_fullName = flClient.getFullName();
            v_clientRegID = flClient.getClientRegId();
            v_address = flClient.getAddress().getDescription();
            v_country = flClient.getAddress().getCountry();
            v_versionWb = "2";
            v_regionCode = flClient.getAddress().getRegionCode();
        }
        if (orgInfoV2.getFO() != null) {
            FOType foClient = orgInfoV2.getFO();
            v_clientType = "FO";
            v_shortName = foClient.getShortName();
            v_fullName = foClient.getFullName();
            v_clientRegID = foClient.getClientRegId();
            v_address = foClient.getAddress().getDescription();
            v_country = foClient.getAddress().getCountry();
            //            v_versionWb = "2";
        }
        if (orgInfoV2.getTS() != null) {
            TSType tsClient = orgInfoV2.getTS();
            v_clientType = "TS";
            v_shortName = tsClient.getShortName();
            v_fullName = tsClient.getFullName();
            v_clientRegID = tsClient.getClientRegId();
            v_address = tsClient.getAddress().getDescription();
            v_country = tsClient.getAddress().getCountry();
            //            v_versionWb = "2";
        }
        if (orgInfoV2.getUL() != null) {
            ULType ulClient = orgInfoV2.getUL();
            v_clientType = "UL";
            v_inn = ulClient.getINN();
            v_kpp = ulClient.getKPP();
            v_shortName = ulClient.getShortName();
            v_fullName = ulClient.getFullName();
            v_clientRegID = ulClient.getClientRegId();
            v_address = ulClient.getAddress().getDescription();
            v_country = ulClient.getAddress().getCountry();
            //            v_versionWb = "2";
            v_regionCode = ulClient.getAddress().getRegionCode();
        }

        f_id =
            SavePartnerToBase(v_shortName, v_fullName, v_clientRegID, v_inn, v_kpp, v_address, v_country, v_regionCode,
                              v_versionWb, v_clientType);
        /*        String sqlQuery =
            String.format(SET_PARTNER, v_inn, (v_kpp != null) ? v_kpp : "null",
                          (v_fullName != null) ? "'" + v_fullName + "'" : "null",
                          (v_shortName != null) ? "'" + v_shortName + "'" : "null", "'" + v_clientRegID + "'",
                          (v_address != null) ? "'" + v_address + "'" : "null",
                          (v_country != null) ? "'" + v_country + "'" : "null",
                          "'" + EGAISparser.format.format(v_date) + "'", "'" + v_clientType + "'",
                          (v_versionWb != null) ? v_versionWb : "null",
                          (v_regionCode != null) ? "'" + v_regionCode + "'" : "null");
        try {*/
        /*            JAXBContext context = JAXBContext.newInstance(OrgInfoV2.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(orgInfoV2, new File("d:\\orgInfoV2.xml"));*/

        //           stmt = connection.prepareStatement(sqlQuery);
        /*        stmt.setBlob(1, ts);
        stmt.setBlob(2, ts_v2);*/
        /*          rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        } /*catch (JAXBException e) {
            e.printStackTrace();
        }*/
        return f_id;
    }

    private int SaveOrgInfoRusV2(OrgInfoRusV2 orgInfoRusV2) {
        int f_id = 0;
        String v_inn = null, v_kpp = null, v_fullName = null, v_shortName = null, v_clientRegID = null, v_address =
            null, v_country = null, v_clientType = null, v_versionWb = null, v_regionCode = null;
        java.util.Date v_date;
        v_date = new java.util.Date();
        ResultSet rs;
        PreparedStatement stmt;
        if (orgInfoRusV2.getFL() != null) {
            FLType flClient = orgInfoRusV2.getFL();
            v_clientType = "FL";
            v_inn = flClient.getINN();
            v_shortName = flClient.getShortName();
            v_fullName = flClient.getFullName();
            v_clientRegID = flClient.getClientRegId();
            v_address = flClient.getAddress().getDescription();
            v_country = flClient.getAddress().getCountry();
            v_versionWb = "2";
            v_regionCode = flClient.getAddress().getRegionCode();
        }
        if (orgInfoRusV2.getUL() != null) {
            ULType ulClient = orgInfoRusV2.getUL();
            v_clientType = "UL";
            v_inn = ulClient.getINN();
            v_kpp = ulClient.getKPP();
            v_shortName = ulClient.getShortName();
            v_fullName = ulClient.getFullName();
            v_clientRegID = ulClient.getClientRegId();
            v_address = ulClient.getAddress().getDescription();
            v_country = ulClient.getAddress().getCountry();
            //            v_versionWb = "2";
            v_regionCode = ulClient.getAddress().getRegionCode();
        }


        String sqlQuery =
            String.format(SET_PARTNER, v_inn, (v_kpp != null) ? v_kpp : "null",
                          (v_fullName != null) ? "'" + v_fullName + "'" : "null",
                          (v_shortName != null) ? "'" + v_shortName + "'" : "null", "'" + v_clientRegID + "'",
                          (v_address != null) ? "'" + v_address + "'" : "null",
                          (v_country != null) ? "'" + v_country + "'" : "null",
                          "'" + EGAISparser.format.format(v_date) + "'", "'" + v_clientType + "'",
                          (v_versionWb != null) ? v_versionWb : "null",
                          (v_regionCode != null) ? "'" + v_regionCode + "'" : "null");
        try {
            /*            JAXBContext context = JAXBContext.newInstance(OrgInfoV2.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(orgInfoV2, new File("d:\\orgInfoV2.xml"));*/

            stmt = connection.prepareStatement(sqlQuery);
            /*        stmt.setBlob(1, ts);
        stmt.setBlob(2, ts_v2);*/
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        } /*catch (JAXBException e) {
            e.printStackTrace();
        }*/
        return f_id;
    }

    private void SaveWbPositionV2(PositionType positionType, int p_ttn, String wbType) {
        ResultSet rs;
        PreparedStatement stmt;
        int f_product;
        String sqlQuery;
        int f_formA;
        int f_position = 0;
        if (wbType == "WB_IN") {
            f_product = SaveProductInfoV2(positionType.getProduct());
            f_formA = SaveInformF1(positionType.getInformF1().getRegId(), f_product);
            sqlQuery =
                String.format(SET_WBPOS_IN, p_ttn, positionType.getIdentity(), positionType.getQuantity(),
                              positionType.getPrice(), f_product, f_formA);
            try {
                stmt = connection.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    f_position = rs.getInt(1);
                }
                if (f_position > 0) {
                    SaveInformF2(positionType.getInformF2(), f_product, f_position);
                }
            } catch (SQLException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();
            }

        }
    }


    private int SaveProductInfoV2(ProductInfoV2 productInfoV2) {
        int f_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        int f_importer = 0, f_producer;
        f_producer = SaveOrgInfoV2(productInfoV2.getProducer());
        f_id =
            SaveProductToBase(productInfoV2.getAlcCode(), productInfoV2.getFullName(), productInfoV2.getShortName(),
                              productInfoV2.getCapacity(), productInfoV2.getAlcVolume(),
                              productInfoV2.getProductVCode(), f_producer);
        /*        sqlQuery =
            String.format(SET_PRODUCT, productInfoV2.getAlcCode(), "", productInfoV2.getFullName().replaceAll("'", "''"),
                          productInfoV2.getShortName().replaceAll("'", "''"), productInfoV2.getCapacity(), productInfoV2.getAlcVolume(),
                          productInfoV2.getProductVCode(), (f_producer > 0) ? f_producer : null,
                          (f_importer > 0) ? f_importer : null, 2);*/

        /*        DOMSource ds = new DOMSource(xmlNode);
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
        InputStream ts = new ByteArrayInputStream(outt.toByteArray());*/

        /*try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }*/

        return f_id;
    }

    private int SaveInformF1(String informF1Type, int p_product) {
        int f_id = 0;

        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_InformF1, p_product, informF1Type);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

        return f_id;
    }

    private void SaveInformF2(InformF2Type informF2Type, int p_product, int p_posId) {
        int f_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_InformF2, p_product, informF2Type.getInformF2Item().getF2RegId(), p_posId);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
            if (f_id > 0 & informF2Type.getInformF2Item().getMarkInfo() != null)
            //                         markInfo!=null)
            {
                //                markInfo.SaveToBase(f_id,lConn);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    private boolean SaveWbPositionV3(ru.fsrar.wegais.ttnsingle_v3.PositionType positionType, int p_ttn, String string) {
        ResultSet rs;
        boolean result = false;
        PreparedStatement stmt;
        int f_product;
        String sqlQuery;
        int f_formA;
        int f_position = 0;
        if (string == "WB_IN") {
            f_product = SaveProductInfoV2(positionType.getProduct());
            f_formA = SaveInformF1(positionType.getFARegId(), f_product);
            sqlQuery =
                String.format(SET_WBPOS_IN, p_ttn, positionType.getIdentity(), positionType.getQuantity(),
                              positionType.getPrice(), f_product, f_formA);
            try {
                stmt = connection.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    f_position = rs.getInt(1);
                }
                int f_formB;
                if (f_position > 0) {
                    f_formB = SaveInformF2_v3(positionType.getInformF2(), f_product, f_position);
                }
                if (positionType.getBoxInfo() != null) {
                    ru.fsrar.wegais.ttnsingle_v3.PositionType.BoxInfo bxInfo = positionType.getBoxInfo();
                    List<Boxtype> lstBoxes = bxInfo.getBoxtree();
                    for (int i = 0; i < lstBoxes.size(); i++) {
                        //                        lstBoxes.get(i).getContent().








                    }
                }
                result = true;
            } catch (SQLException e) {
                result = false;
                System.out.println(sqlQuery);
                e.printStackTrace();
            }

        }
        return result;
    }

    private int SaveInformF2_v3(InformF2TypeItemBC informF2Type, int p_product, int p_posId) {
        int f_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_InformF2, p_product, informF2Type.getF2RegId(), p_posId);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
            MarkInfoTypeBC markInfo = informF2Type.getMarkInfo();
            if (markInfo != null) {
                SaveMarkInfoV3(markInfo, f_id);
            }

        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        return f_id;
    }

    private boolean SaveWayBillActV3(WayBillActTypeV3 wayBillActTypeV3, int p_urlNo) {

        String accept = null, actNum = null, actRegId = null, note = null, dat = null;
        int head_id = 0;
        boolean remove_url = false;
        ResultSet rs;
        PreparedStatement stmt;
        WayBillActTypeV3.Header header = wayBillActTypeV3.getHeader();
        actRegId = header.getWBRegId();
        if (header.getACTNUMBER() != null) {
            actNum = header.getACTNUMBER();
        }
        if (header.getActDate() != null) {
            dat = EGAISparser.format.format(header.getActDate().toGregorianCalendar().getTime());
        }
        if (header.getNote() != null) {
            note = header.getNote();
        }
        if (header.getIsAccept() != null) {
            accept = header.getIsAccept().value();
        }

        String sqlQuery;
        sqlQuery = String.format(SET_WBACT_IN, actNum, dat, actRegId, note, accept);
        /*        DOMSource ds = new DOMSource(xmlNode);
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
        InputStream ts = new ByteArrayInputStream(outt.toByteArray());*/

        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                head_id = rs.getInt("F_ID");
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
        WayBillActTypeV3.Content content = wayBillActTypeV3.getContent();
        if (content != null) {
            List<ru.fsrar.wegais.actttnsingle_v3.PositionType> positions = content.getPosition();
            if (head_id > 0) {
                for (int i = 0; i < positions.size(); i++) {

                    sqlQuery =
                        String.format(SET_WBACT_IN_POS, head_id, positions.get(i).getIdentity(),
                                      positions.get(i).getInformF2RegId(), positions.get(i).getRealQuantity());

                    try {
                        stmt = connection.prepareStatement(sqlQuery);
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
        }
        return remove_url;

    }

    private void SaveMarkInfoV3(MarkInfoTypeBC markInfoTypeBC, int p_formB) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        List<BoxamcType> boxes = markInfoTypeBC.getBoxpos();
        for (int i = 0; i < boxes.size(); i++) {
            SaveBoxV3(boxes.get(i), p_formB);
        }
    }

    private void SaveBoxV3(BoxamcType boxamcType, int p_formB) {
        int v_id;
        List<String> amcList = boxamcType.getAmclist().getAmc();
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        for (int i = 0; i < amcList.size(); i++) {
            sqlQuery = String.format(SET_AMC, amcList.get(i), boxamcType.getBoxnumber(), p_formB);
            try {
                stmt = connection.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    v_id = rs.getInt("F_ID");
                }
            } catch (SQLException e) {
                System.out.println(sqlQuery);
                e.printStackTrace();

            }

        }
    }

    public WayBillTypeV3 LoadWBOut_V3(int p_source_id) {
        int f_consign_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery, sqlPos;
        WayBillTypeV3 wayBillTypeV3;
        GregorianCalendar gregory = new GregorianCalendar();
        sqlQuery = String.format(GET_WB_OUT, p_source_id);
        wayBillTypeV3 = new WayBillTypeV3();
        WayBillTypeV3.Header header = new WayBillTypeV3.Header();
        System.out.println(String.valueOf(p_source_id));
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {

                //                f_id = rs.getInt("F_ID");
                header.setNUMBER(rs.getString("F_NUMBER"));
                gregory.setTime(rs.getDate("F_DATE"));
                XMLGregorianCalendar calendar = null;
                try {
                    calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }

                header.setDate(calendar);
                header.setShippingDate(calendar);
                wayBillTypeV3.setIdentity(rs.getString("F_ID"));
                WbType wbType = WbType.fromValue(rs.getString("F_TTN_TYPE"));
                header.setType(wbType);
                f_consign_id = rs.getInt("F_PARTNER");
                header.setTransport(new TransportType());
                header.getTransport().setTRANTYPE(rs.getString("F_TRAN_TYPE"));
                header.getTransport().setTRANCOMPANY(rs.getString("F_TRAN_COMPANY"));
                header.getTransport().setTRANCAR(rs.getString("F_TRAN_CAR"));
                header.getTransport().setTRANCUSTOMER(rs.getString("F_TRAN_CUSTOMER"));
                header.getTransport().setTRANDRIVER(rs.getString("F_TRAN_DRIVER"));
                header.getTransport().setTRANLOADPOINT(rs.getString("F_TRAN_LOADPOINT"));
                header.getTransport().setTRANUNLOADPOINT(rs.getString("F_TRAN_UNLOADPOINT"));
                wayBillTypeV3.setIdentity(rs.getString("F_REGID"));
                //                f_egaisId = rs.getString("F_REGID");
            }
            if (f_consign_id > 0) {
                header.setConsignee(LoadOrgInfoV2(f_consign_id));
            }
            header.setShipper(LoadOrgInfoRusV2(2280));
            //            f_shiper = new Producer(2280, conn);
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        wayBillTypeV3.setHeader(header);
        WayBillTypeV3.Content content = new WayBillTypeV3.Content();

        sqlQuery = String.format(GET_WB_OUT_STR_S, p_source_id);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            ru.fsrar.wegais.ttnsingle_v3.PositionType pr;

            while (rs.next()) {

                pr = new ru.fsrar.wegais.ttnsingle_v3.PositionType();
                pr.setIdentity(rs.getString("F_IDENT"));
                pr.setQuantity(rs.getBigDecimal("F_QUAN"));
                pr.setPrice(rs.getBigDecimal("F_PRICE"));
                pr.setFARegId(rs.getString("F_FORM_A_EGAIS"));
                pr.setProduct(new ProductInfoV2());
                pr.getProduct().setProductVCode(rs.getString("F_PRODUCT_EGAIS"));
                pr.setInformF2(new InformF2TypeItemBC());
                pr.getInformF2().setF2RegId(rs.getString("F_FORM_B_EGAIS"));
                content.getPosition().add(pr);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        for (int i = 0; i < content.getPosition().size(); i++) {
            ProductInfoV2 prod = LoadrProductInfoV2(0, content.getPosition().get(i).getProduct().getProductVCode());
            content.getPosition().get(i).setProduct(prod);
            //            InformF2TypeItemBC formB = LoadInformF2TypeItemBC(0,content.getPosition().get(i).getInformF2().getF2RegId());
            InformF2TypeItemBC inform2 = content.getPosition().get(i).getInformF2();
            inform2.setMarkInfo(LoadMarkInfoTTN_Out_pos(p_source_id, content.getPosition().get(i).getIdentity()));
            content.getPosition().get(i).setInformF2(inform2);
        }
        wayBillTypeV3.setContent(content);
        return wayBillTypeV3;
    }

    public OrgInfoV2 LoadOrgInfoV2(int p_id) {

        String sqlQuery = String.format(GET_PARTNER, p_id);
        OrgInfoV2 partner = new OrgInfoV2();
        String clientType = "";
        String inn = "", kpp = "", shortName = "", fullName = "", country = "", address = "", clientRegID =
            "", regionCode = "";
        Statement stmt;
        ResultSet rs;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            System.out.println(sqlQuery);
            while (rs.next()) {

                clientType = rs.getString("F_CLIENTTYPE");


                inn = rs.getString("F_INN");
                kpp = rs.getString("F_KPP");
                shortName = rs.getString("F_NAME");
                fullName = rs.getString("F_FULL_NAME");
                country = rs.getString("F_COUNTRY");
                address = rs.getString("F_ADRES");
                clientRegID = rs.getString("F_EGAIS_ID");
                regionCode = rs.getString("F_REGION_CODE");
                if (rs.getString("F_CLIENTTYPE").isEmpty()) {
                    System.out.println(sqlQuery);
                    clientType = "null";
                } else {
                    clientType = rs.getString("F_CLIENTTYPE");
                }

            }
            if (clientType.lastIndexOf("UL") != -1) {
                partner.setUL(new ULType());
                partner.getUL().setINN(inn);
                partner.getUL().setKPP(kpp);
                partner.getUL().setShortName(shortName);
                partner.getUL().setFullName(fullName);
                partner.getUL().setClientRegId(clientRegID);
                partner.getUL().setAddress(new OrgAddressTypeULFL());
                partner.getUL().getAddress().setCountry(country);
                partner.getUL().getAddress().setRegionCode(regionCode);
                partner.getUL().getAddress().setDescription(address);
            }
            if (clientType.lastIndexOf("FL") != -1) {
                partner.setFL(new FLType());
                partner.getFL().setClientRegId(clientRegID);
                partner.getFL().setFullName(fullName);
                partner.getFL().setINN(inn);
                partner.getFL().setShortName(shortName);
                partner.getFL().setAddress(new OrgAddressTypeULFL());
                partner.getFL().getAddress().setCountry(country);
                partner.getFL().getAddress().setDescription(address);
                partner.getFL().getAddress().setRegionCode(regionCode);
            }
            if (clientType.lastIndexOf("FO") != -1) {
                partner.setFO(new FOType());
                partner.getFO().setClientRegId(clientRegID);
                partner.getFO().setFullName(fullName);
                partner.getFO().setShortName(shortName);
                partner.getFO().setAddress(new OrgAddressTypeFOTS());
                partner.getFO().getAddress().setCountry(country);
                partner.getFO().getAddress().setDescription(address);
            }
            if (clientType.lastIndexOf("TS") != -1) {
                partner.setTS(new TSType());
                partner.getTS().setClientRegId(clientRegID);
                partner.getTS().setFullName(fullName);
                partner.getTS().setShortName(shortName);
                partner.getTS().setAddress(new OrgAddressTypeFOTS());
                partner.getTS().getAddress().setCountry(country);
                partner.getTS().getAddress().setDescription(address);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partner;
    }

    public OrgInfoRusV2 LoadOrgInfoRusV2(int p_id) {
        String sqlQuery = String.format(GET_PARTNER, p_id);
        OrgInfoRusV2 partner = new OrgInfoRusV2();
        String clientType = "";
        String inn = "", kpp = "", shortName = "", fullName = "", country = "", address = "", clientRegID =
            "", regionCode = "";
        Statement stmt;
        ResultSet rs;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next()) {

                clientType = rs.getString("F_CLIENTTYPE");


                inn = rs.getString("F_INN");
                kpp = rs.getString("F_KPP");
                shortName = rs.getString("F_NAME");
                fullName = rs.getString("F_FULL_NAME");
                country = rs.getString("F_COUNTRY");
                address = rs.getString("F_ADRES");
                clientRegID = rs.getString("F_EGAIS_ID");
                regionCode = rs.getString("F_REGION_CODE");
                if (rs.getString("F_CLIENTTYPE").isEmpty()) {
                    clientType = "null";
                } else {
                    clientType = rs.getString("F_CLIENTTYPE");
                }

            }
            if (clientType.lastIndexOf("UL") != -1) {
                partner.setUL(new ULType());
                partner.getUL().setINN(inn);
                partner.getUL().setKPP(kpp);
                partner.getUL().setShortName(shortName);
                partner.getUL().setFullName(fullName);
                partner.getUL().setClientRegId(clientRegID);
                partner.getUL().setAddress(new OrgAddressTypeULFL());
                partner.getUL().getAddress().setCountry(country);
                partner.getUL().getAddress().setRegionCode(regionCode);
                partner.getUL().getAddress().setDescription(address);
            }
            if (clientType.lastIndexOf("FL") != -1) {
                partner.setFL(new FLType());
                partner.getFL().setClientRegId(clientRegID);
                partner.getFL().setFullName(fullName);
                partner.getFL().setINN(inn);
                partner.getFL().setShortName(shortName);
                partner.getFL().setAddress(new OrgAddressTypeULFL());
                partner.getFL().getAddress().setCountry(country);
                partner.getFL().getAddress().setDescription(address);
                partner.getFL().getAddress().setRegionCode(regionCode);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return partner;

    }

    public ProductInfoV2 LoadrProductInfoV2(int p_id, String p_egais_id) {
        ProductInfoV2 product = new ProductInfoV2();
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        int producer_id = 0;
        if (p_id > 0) {

            sqlQuery = String.format(GET_PRODUCT, p_id, "null");
        } else {
            sqlQuery = String.format(GET_PRODUCT, 0, "'" + p_egais_id + "'");
        }
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                product.setFullName(rs.getString("F_FULL_NAME"));
                product.setUnitType(ru.fsrar.wegais.productref_v2.WbUnitType.PACKED);
                product.setType(ProductType.АП);
                product.setProductVCode(rs.getString("F_PRODUCTVCODE"));
                product.setCapacity(rs.getBigDecimal("F_CAPACITY"));
                product.setAlcVolume(rs.getBigDecimal("F_ALCVOL"));
                product.setAlcCode(rs.getString("F_EGAIS_ID"));
                producer_id = rs.getInt("F_PRODUCER");

            }
            System.out.println(sqlQuery);
            product.setProducer(LoadOrgInfoV2(producer_id));

        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return product;
    }


    private MarkInfoTypeBC LoadMarkInfoTTN_Out_pos(int p_ttn_id, String p_pos_ident) {
        MarkInfoTypeBC markInfo = new MarkInfoTypeBC();
        ResultSet rs;
        PreparedStatement stmt;
        String boxNumber = "";
        BoxamcType box = null;
        String sqlQuery = String.format("select * from PR_T_EGAIS_TT_OUT_POS_AMC_S(%1$s,%2$s)", p_ttn_id, p_pos_ident);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (boxNumber != rs.getString("F_BOX_NUMBER")) {
                    if (box != null) {
                        markInfo.getBoxpos().add(box);
                    }
                    box = new BoxamcType();
                    boxNumber = rs.getString("F_BOX_NUMBER");
                    box.setBoxnumber(boxNumber);
                    box.setAmclist(new AMCforDocType());
                }
                box.getAmclist().getAmc().add(rs.getString("F_AMC"));
            }
            if (box != null) {
                markInfo.getBoxpos().add(box);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

        //PR_T_EGAIS_TT_OUT_POS_AMC_S
        if (markInfo.getBoxpos().size() == 0) {
            markInfo = null;
        }
        return markInfo;
    }

    private WayBillActTypeV3 LoadWBIn_act(int p_id, boolean confirmed) {
        WayBillActTypeV3 wbAct = new WayBillActTypeV3();
        ResultSet rs;
        PreparedStatement stmt;
        GregorianCalendar gregory = new GregorianCalendar();
        String sqlQuery = String.format(GET_WB_IN, p_id);
        wbAct.setHeader(new WayBillActTypeV3.Header());

        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            XMLGregorianCalendar calendar = null;
            while (rs.next()) {
                wbAct.getHeader().setACTNUMBER(rs.getString("F_NUMBER"));
                gregory.setTime(new Date()
                                /*rs.getDate("F_DATE")*/);
                try {
                    calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
                    wbAct.getHeader().setActDate(calendar);
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                wbAct.getHeader().setWBRegId(rs.getString("F_REGID"));
                wbAct.setIdentity(String.valueOf(rs.getInt("F_ID")));
                if (confirmed) {
                    wbAct.getHeader().setIsAccept(AcceptType.ACCEPTED);
                } else {
                    wbAct.getHeader().setIsAccept(AcceptType.REJECTED);
                }
            }
            if (confirmed) {
                sqlQuery = String.format("select * from PR_T_EGAIS_TTN_IN_GET_CONFIRM(%1$s)", p_id);
                stmt = connection.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                wbAct.setContent(new WayBillActTypeV3.Content());
                while (rs.next()) {
                    ru.fsrar.wegais.actttnsingle_v3.PositionType pos =
                        new ru.fsrar.wegais.actttnsingle_v3.PositionType();
                    pos.setIdentity(rs.getString("F_IDENT"));
                    pos.setRealQuantity(rs.getBigDecimal("F_QUANT"));
                    pos.setInformF2RegId(rs.getString("F_FORM_B_EGAIS"));
                    if (pos.getIdentity() != null) {
                        wbAct.getContent().getPosition().add(pos);
                    }

                }
                if (wbAct.getContent().getPosition().size() > 0) {
                    wbAct.getHeader().setIsAccept(AcceptType.DIFFERENCES);
                }
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return wbAct;
    }

    private boolean SaveRestsV2(ReplyRestsV2 replyRests, String p_guid) {
        ResultSet rs;
        PreparedStatement stmt;
        int head_id = 0;
        boolean result = false;
        DateFormat lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lformat.setTimeZone(TimeZone.getTimeZone("Asia/Novosibirsk"));
        Date rest_date =
            replyRests.getRestsDate().toGregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"), Locale.US,
                                                          null).getTime();
        String sqlQuery = String.format(SET_REST_HEAD, p_guid, lformat.format(rest_date));
        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                head_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
            result = true;
        } catch (SQLException e) {
            result = false;
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

        for (int i = 0; i < replyRests.getProducts().getStockPosition().size(); i++) {
            ru.fsrar.wegais.replyrests_v2.StockPositionType pos = replyRests.getProducts().getStockPosition().get(i);
            ProductInfoReplyV2 product = pos.getProduct();
            int f_product = SaveProductInfoReplyV2(product);

            sqlQuery =
                String.format(SET_REST_STR, head_id, product.getAlcCode(), pos.getInformF1RegId(),
                              pos.getInformF2RegId(), pos.getQuantity());

            try {
                stmt = connection.prepareStatement(sqlQuery);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int p_id = rs.getInt("F_ID");
                }
                rs.close();
                stmt.close();
                result = true;
            } catch (SQLException e) {
                result = false;
                System.out.println(sqlQuery);
                e.printStackTrace();
            }


        }
        if (result) {
            sqlQuery = String.format("execute procedure PR_EGAIS_REST_COMPARE(%1$s)", head_id);
            try {
                stmt = connection.prepareStatement(sqlQuery);
                stmt.execute();
                stmt.close();
                result = true;
            } catch (SQLException e) {
                result = false;
                System.out.println(sqlQuery);
                e.printStackTrace();
            }

        }
        return result;
    }

    private int SaveProductInfoReplyV2(ProductInfoReplyV2 product) {
        int f_id = 0;
        int f_producer = 0;
        f_producer = SaveOrgInfoReplyV2(product.getProducer());
        f_id =
            SaveProductToBase(product.getAlcCode(), product.getFullName(), product.getShortName(),
                              product.getCapacity(), product.getAlcVolume(), product.getProductVCode(), f_producer);
        return f_id;
    }

    private int SaveOrgInfoReplyV2(OrgInfoReplyV2 orgInfoReplyV2) {
        int f_id = 0;
        if (orgInfoReplyV2.getFL() != null) {
            f_id =
                SavePartnerToBase(orgInfoReplyV2.getFL().getShortName(), orgInfoReplyV2.getFL().getShortName(),
                                  orgInfoReplyV2.getFL().getClientRegId(), orgInfoReplyV2.getFL().getINN(), null,
                                  orgInfoReplyV2.getFL().getAddress().getDescription(),
                                  orgInfoReplyV2.getFL().getAddress().getCountry(),
                                  orgInfoReplyV2.getFL().getAddress().getRegionCode(), "'WayBill_V2'", "FL");
        }
        if (orgInfoReplyV2.getUL() != null) {
            f_id =
                SavePartnerToBase(orgInfoReplyV2.getUL().getShortName(), orgInfoReplyV2.getUL().getShortName(),
                                  orgInfoReplyV2.getUL().getClientRegId(), orgInfoReplyV2.getUL().getINN(),
                                  orgInfoReplyV2.getUL().getKPP(), orgInfoReplyV2.getUL().getAddress().getDescription(),
                                  orgInfoReplyV2.getUL().getAddress().getCountry(),
                                  orgInfoReplyV2.getUL().getAddress().getRegionCode(), "'WayBill_V2'", "UL");
        }
        if (orgInfoReplyV2.getFO() != null) {
            f_id =
                SavePartnerToBase(orgInfoReplyV2.getFO().getShortName(), orgInfoReplyV2.getFO().getShortName(),
                                  orgInfoReplyV2.getFO().getClientRegId(), null, null,
                                  orgInfoReplyV2.getFO().getAddress().getDescription(),
                                  orgInfoReplyV2.getFO().getAddress().getCountry(), null, "'WayBill_V2'", "FO");
        }
        if (orgInfoReplyV2.getTS() != null) {
            f_id =
                SavePartnerToBase(orgInfoReplyV2.getTS().getShortName(), orgInfoReplyV2.getTS().getShortName(),
                                  orgInfoReplyV2.getTS().getClientRegId(), null, null,
                                  orgInfoReplyV2.getTS().getAddress().getDescription(),
                                  orgInfoReplyV2.getTS().getAddress().getCountry(), null, "'WayBill_V2'", "TS");
        }

        return f_id;
    }

    private int SavePartnerToBase(String p_shortName, String p_fullName, String p_clientRegID, String p_inn,
                                  String p_kpp, String p_address, String p_country, String p_regionCode,
                                  String p_versionWb, String p_clientType) {
        int f_id = 0;
        String sqlQuery =
            String.format(SET_PARTNER, (p_inn != null) ? "'" + p_inn + "'" : "null",
                          (p_kpp != null) ? "'" + p_kpp + "'" : "null",
                          (p_fullName != null) ? "'" + p_fullName.replaceAll("'", "''") + "'" : "null",
                          (p_shortName != null) ? "'" + p_shortName.replaceAll("'", "''") + "'" : "null",
                          "'" + p_clientRegID + "'",
                          (p_address != null) ? "'" + p_address.replaceAll("'", "''") + "'" : "null",
                          (p_country != null) ? "'" + p_country + "'" : "null",
                          "'" + EGAISparser.format.format(new Date()) + "'",
                          (p_clientType != null) ? "'" + p_clientType + "'" : "null",
                          (p_versionWb != null) ? p_versionWb : "null",
                          (p_regionCode != null) ? "'" + p_regionCode + "'" : "null");
        try {

            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return f_id;
    }

    private int SaveProductToBase(String p_AlcCode, String p_FullName, String p_ShortName, BigDecimal p_Capacity,
                                  BigDecimal p_AlcVolume, String p_ProductVCode, int p_producer) {
        int f_id = 0;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery =
            String.format(SET_PRODUCT, p_AlcCode, "", (p_FullName != null) ? p_FullName.replaceAll("'", "''") : null,
                          (p_ShortName != null) ? p_ShortName.replaceAll("'", "''") : null, p_Capacity, p_AlcVolume,
                          p_ProductVCode, (p_producer > 0) ? p_producer : null, null, 2);

        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
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

        return f_id;
    }

    private boolean SaveReplyForm1(ReplyForm1 replyForm1) {
        boolean result = false;
        PreparedStatement stmt;
        String sqlQuery;
        sqlQuery =
            String.format(SET_FORM1, replyForm1.getInformF1RegId(), replyForm1.getOriginalDocNumber(),
                          EGAISparser.format.format(replyForm1.getOriginalDocDate().toGregorianCalendar().getTime()),
                          replyForm1.getEGAISNumber(),
                          EGAISparser.format.format(replyForm1.getEGAISDate().toGregorianCalendar().getTime()),
                          EGAISparser.format.format(replyForm1.getBottlingDate().toGregorianCalendar().getTime()),
                          (replyForm1.getGTDDate() != null) ?
                          "'" + EGAISparser.format.format(replyForm1.getGTDDate().toGregorianCalendar().getTime()) +
                          "'" : "null", replyForm1.getQuantity(),
                          (replyForm1.getGTDNUMBER() != null) ? "'" + replyForm1.getGTDNUMBER() + "'" : "null",
                          (replyForm1.getGTDDate() != null) ?
                          "'" + EGAISparser.format.format(replyForm1.getGTDDate().toGregorianCalendar().getTime()) +
                          "'" : "null");
        try {
            stmt = connection.prepareStatement(sqlQuery);
            stmt.execute();
            result = true;
        } catch (SQLException e) {
            result = false;
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        return result;
    }

    private boolean SaveReplyClientV2(ReplyClientV2 replyClientV2) {
        List<OrgInfoExV2> clientList = replyClientV2.getClients().getClient();
        boolean result = false;
        int f_id = 0;
        for (int i = 0; i < clientList.size(); i++) {
            f_id = 0;
            result = false;
            if (clientList.get(i).getOrgInfoV2().getFL() != null) {
                f_id =
                    SavePartnerToBase(clientList.get(i).getOrgInfoV2().getFL().getShortName(),
                                      clientList.get(i).getOrgInfoV2().getFL().getFullName(),
                                      clientList.get(i).getOrgInfoV2().getFL().getClientRegId(),
                                      clientList.get(i).getOrgInfoV2().getFL().getINN(), null,
                                      clientList.get(i).getOrgInfoV2().getFL().getAddress().getDescription(),
                                      clientList.get(i).getOrgInfoV2().getFL().getAddress().getCountry(),
                                      clientList.get(i).getOrgInfoV2().getFL().getAddress().getRegionCode(),
                                      "'WayBill_v2'", "FL");

            }
            if (clientList.get(i).getOrgInfoV2().getUL() != null) {
                f_id =
                    SavePartnerToBase(clientList.get(i).getOrgInfoV2().getUL().getShortName(),
                                      clientList.get(i).getOrgInfoV2().getUL().getFullName(),
                                      clientList.get(i).getOrgInfoV2().getUL().getClientRegId(),
                                      clientList.get(i).getOrgInfoV2().getUL().getINN(),
                                      clientList.get(i).getOrgInfoV2().getUL().getKPP(),
                                      clientList.get(i).getOrgInfoV2().getUL().getAddress().getDescription(),
                                      clientList.get(i).getOrgInfoV2().getUL().getAddress().getCountry(),
                                      clientList.get(i).getOrgInfoV2().getUL().getAddress().getRegionCode(),
                                      "'WayBill_v2'", "UL");

            }
            if (clientList.get(i).getOrgInfoV2().getTS() != null) {
                f_id =
                    SavePartnerToBase(clientList.get(i).getOrgInfoV2().getTS().getShortName(),
                                      clientList.get(i).getOrgInfoV2().getTS().getFullName(),
                                      clientList.get(i).getOrgInfoV2().getTS().getClientRegId(), null, null,
                                      clientList.get(i).getOrgInfoV2().getTS().getAddress().getDescription(),
                                      clientList.get(i).getOrgInfoV2().getTS().getAddress().getCountry(), null,
                                      "'WayBill_v2'", "TS");

            }
            if (clientList.get(i).getOrgInfoV2().getFO() != null) {
                f_id =
                    SavePartnerToBase(clientList.get(i).getOrgInfoV2().getFO().getShortName(),
                                      clientList.get(i).getOrgInfoV2().getFO().getFullName(),
                                      clientList.get(i).getOrgInfoV2().getFO().getClientRegId(), null, null,
                                      clientList.get(i).getOrgInfoV2().getFO().getAddress().getDescription(),
                                      clientList.get(i).getOrgInfoV2().getFO().getAddress().getCountry(), null,
                                      "'WayBill_v2'", "FO");

            }
            if (f_id > 0) {
                result = true;
            }

        }
        return result;
    }

    private boolean SavetTTNInformF2Reg(WayBillInformF2RegType wBInformF2Reg) {
        boolean result = false;
        boolean ttn_type = false;
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        WayBillInformF2RegType.Header header = wBInformF2Reg.getHeader();
        int v_consignee = SaveOrgInfoRusV2(header.getConsignee());
        int v_shiper = SaveOrgInfoRusV2(header.getShipper());
        int v_id = 0;
        String v_regid =
            (header.getShipper().getUL() != null) ? header.getShipper().getUL().getClientRegId() :
            header.getShipper().getFL().getClientRegId();
        if (v_regid.equals(Console.SELF_REGID)) {

            sqlQuery =
                String.format(SET_SQL_REGINFO_OUT, null, header.getWBNUMBER(), header.getWBDate(), v_consignee,
                              header.getIdentity(), header.getWBRegId(), header.getEGAISFixNumber(),
                              EGAISparser.format.format(header.getEGAISFixDate().toGregorianCalendar().getTime()));

        } else {
            sqlQuery =
                String.format(SET_SQL_REGINFO, null, header.getWBNUMBER(), header.getWBDate(), v_shiper,
                              header.getIdentity(), header.getWBRegId(), header.getEGAISFixNumber(),
                              EGAISparser.format.format(header.getEGAISFixDate().toGregorianCalendar().getTime()));
            ttn_type = true;
        }


        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                v_id = rs.getInt("f_id");
                if (rs.getInt("F_REMOVE_URL") > 0) {
                    result = true;
                } else {
                    result = false;
                }
            }
            if (result && (v_id > 0)) {
                List<InformF2PositionType> positions = wBInformF2Reg.getContent().getPosition();
                for (int i = 0; i < positions.size(); i++) {

                    sqlQuery =
                        String.format(SET_SQL_REGINFO_STR, v_id, positions.get(i).getIdentity(),
                                      positions.get(i).getInformF2RegId(), ttn_type ? 1 : 0);
                    try {
                        stmt = connection.prepareStatement(sqlQuery);
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            int p_id = rs.getInt("F_ID");
                        }
                    } catch (SQLException e) {
                        System.out.println(sqlQuery);
                        e.printStackTrace();
                    }

                    if (!result) {
                        break;
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

        return result;
    }


    private WayBillActTypeV3 LoadWBOut_act(int p_id, boolean confirmed) {
        WayBillActTypeV3 wbAct = new WayBillActTypeV3();
        ResultSet rs;
        PreparedStatement stmt;
        GregorianCalendar gregory = new GregorianCalendar();
        String sqlQuery = String.format(GET_WB_OUT, p_id);
        wbAct.setHeader(new WayBillActTypeV3.Header());

        try {
            stmt = connection.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            XMLGregorianCalendar calendar = null;
            while (rs.next()) {
                wbAct.getHeader().setACTNUMBER(rs.getString("F_NUMBER"));
                gregory.setTime(new Date()
                                /*rs.getDate("F_DATE")*/);
                try {
                    calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
                    wbAct.getHeader().setActDate(calendar);
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                wbAct.getHeader().setWBRegId(rs.getString("F_REGID"));
                wbAct.setIdentity(String.valueOf(rs.getInt("F_ID")));
                if (confirmed) {
                    wbAct.getHeader().setIsAccept(AcceptType.ACCEPTED);
                } else {
                    wbAct.getHeader().setIsAccept(AcceptType.REJECTED);
                }
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return wbAct;
    }

    private boolean SaveActInventoryInformF2Reg(ActInventoryInformF2Reg actInventoryInformF2Reg) {
        return false;
    }

    private boolean saveReplyHistForm2(ReplyHistForm2 replyHistForm2) {
        return false;
    }

    private boolean SaveReplyForm2(ReplyForm2 replyForm2, String p_guid) {
        String ttn_number = replyForm2.getTTNNumber();
        String regId = replyForm2.getInformF2RegId();
        BigDecimal quant = replyForm2.getQuantity();
        boolean result = false;
        int v_id = 0;
        String ttn_date = EGAISparser.format.format(replyForm2.getTTNDate().toGregorianCalendar().getTime());
        int f_product = SaveProductInfoReplyV2(replyForm2.getProduct());

        PreparedStatement stmt;
        ResultSet rs;
        String sqlQuery;
        sqlQuery = String.format(SET_REPLY_F2, ttn_number, ttn_date, f_product, quant, p_guid, regId);

        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                v_id = rs.getInt("f_str");
            }
            if (v_id != 0) {
                result = true;
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }


        return result;
    }

    private boolean SaveReplyRestBCode(ReplyRestBCode replyRestBCode, String p_guid) {
        String v_form2 = replyRestBCode.getInform2RegId();

        PreparedStatement stmt;
        ResultSet rs;
        String sqlQuery;
        sqlQuery = String.format(SET_AMC_UNACTIVE, v_form2);
        try {
            stmt = connection.prepareStatement(sqlQuery);
            //            stmt.setBlob(1, ts);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        if (replyRestBCode.getMarkInfo().size() > 0) {
            List<String> v_markInfo = replyRestBCode.getMarkInfo().get(0).getAmc();
            for (int i = 0; i < v_markInfo.size(); i++) {
                sqlQuery = String.format(SET_AMC2, v_form2, v_markInfo.get(i));

                try {
                    stmt = connection.prepareStatement(sqlQuery);
                    //            stmt.setBlob(1, ts);
                    stmt.execute();
                } catch (SQLException e) {
                    System.out.println(sqlQuery);
                    e.printStackTrace();
                }

            }
        }
        return true;
    }

    private boolean SaveReplyAp(ReplyAP replyAP, String p_guid) {
        int product_id;
        int producer_id;
        for (int i = 0; i < replyAP.getProducts().getProduct().size(); i++) {
            producer_id =
                SavePartnerToBase(replyAP.getProducts().getProduct().get(i).getProducer().getShortName(),
                                  replyAP.getProducts().getProduct().get(i).getProducer().getFullName(),
                                  replyAP.getProducts().getProduct().get(i).getProducer().getClientRegId(),
                                  (replyAP.getProducts().getProduct().get(i).getProducer().getINN() != null) ?
                                  replyAP.getProducts().getProduct().get(i).getProducer().getINN().getValue() : null,
                                  replyAP.getProducts().getProduct().get(i).getProducer().getKPP(),
                                  replyAP.getProducts().getProduct().get(i).getProducer().getAddress().getDescription(),
                                  replyAP.getProducts().getProduct().get(i).getProducer().getAddress().getCountry(),
                                  replyAP.getProducts().getProduct().get(i).getProducer().getAddress().getRegionCode(),
                                  "'WayBill_V2'", null);
            product_id =
                SaveProductToBase(replyAP.getProducts().getProduct().get(i).getAlcCode(),
                                  replyAP.getProducts().getProduct().get(i).getFullName(),
                                  replyAP.getProducts().getProduct().get(i).getShortName(),
                                  replyAP.getProducts().getProduct().get(i).getCapacity(),
                                  replyAP.getProducts().getProduct().get(i).getAlcVolume(),
                                  replyAP.getProducts().getProduct().get(i).getProductVCode(), producer_id);

        }

        return false;
    }

    private boolean SaveReplyApV2(ReplyAPV2 replyAPV2, String p_guid) {
        int product_id = 0;
        int producer_id = 0;
        boolean result = false;
        for (int i = 0; i < replyAPV2.getProducts().getProduct().size(); i++) {


            if (replyAPV2.getProducts().getProduct().get(i).getProducer().getFL() != null) {
                producer_id =
                    SavePartnerToBase(replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getShortName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getFullName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getClientRegId(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getINN(), null,
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getAddress().getDescription(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getAddress().getCountry(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFL().getAddress().getRegionCode(),
                                      "'WayBill_v2'", "FL");

            }
            if (replyAPV2.getProducts().getProduct().get(i).getProducer().getUL() != null) {
                producer_id =
                    SavePartnerToBase(replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getShortName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getFullName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getClientRegId(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getINN(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getKPP(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getAddress().getDescription(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getAddress().getCountry(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getUL().getAddress().getRegionCode(),
                                      "'WayBill_v2'", "UL");

            }
            if (replyAPV2.getProducts().getProduct().get(i).getProducer().getTS() != null) {
                producer_id =
                    SavePartnerToBase(replyAPV2.getProducts().getProduct().get(i).getProducer().getTS().getShortName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getTS().getFullName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getTS().getClientRegId(),
                                      null, null,
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getTS().getAddress().getDescription(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getTS().getAddress().getCountry(),
                                      null, "'WayBill_v2'", "TS");

            }
            if (replyAPV2.getProducts().getProduct().get(i).getProducer().getFO() != null) {
                producer_id =
                    SavePartnerToBase(replyAPV2.getProducts().getProduct().get(i).getProducer().getFO().getShortName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFO().getFullName(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFO().getClientRegId(),
                                      null, null,
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFO().getAddress().getDescription(),
                                      replyAPV2.getProducts().getProduct().get(i).getProducer().getFO().getAddress().getCountry(),
                                      null, "'WayBill_v2'", "FO");

            }
            if (producer_id != 0) {
                product_id =
                    SaveProductToBase(replyAPV2.getProducts().getProduct().get(i).getAlcCode(),
                                      replyAPV2.getProducts().getProduct().get(i).getFullName(),
                                      replyAPV2.getProducts().getProduct().get(i).getShortName(),
                                      replyAPV2.getProducts().getProduct().get(i).getCapacity(),
                                      replyAPV2.getProducts().getProduct().get(i).getAlcVolume(),
                                      replyAPV2.getProducts().getProduct().get(i).getProductVCode(), producer_id);
                if (product_id != 0) {
                    result = HTTPQueue.CheckGUID(p_guid, connection);
                }
            }

        }

        return result;
    }
}
