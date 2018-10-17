package egaisbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Types;
import java.sql.SQLException;

import java.sql.Types;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mak.fbBase.DataModule;

import org.firebirdsql.pool.FBWrappingDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;

import ru.fsrar.wegais.wb_doc_single_01.Documents;

public class HTTPQueue {
    
    

    
    public class QueueSrc {
        int f_id;
        String f_type;
        int f_source_id;
        String f_param;
        public String f_url;
        Node src_xml;


        QueueSrc(int p_id, String p_type, int p_source_id, String p_param) {
            f_id = p_id;
            f_type = p_type;
            f_source_id = p_source_id;
            f_param = p_param;
            src_xml = null;
        }

        public void SetGuid(String p_guid) throws SQLException {
            PreparedStatement stmt;

            String sqlQuery = String.format(SQL_UPD_QUEUE, f_id, "'" + p_guid + "'");
            stmt = conn.prepareStatement(sqlQuery);
            stmt.setNull(1, Types.BLOB);
            stmt.execute();
            stmt.close();
            if (f_type.compareTo("WayBillOut") == 0) {
                WayBillOut wb = new WayBillOut(f_source_id, conn);
                wb.SetState(p_guid, 2);
            } else if (f_type.compareTo("WayBill") == 0) {
                WayBill wb = new WayBill(f_source_id, conn);
                wb.SetWB_GUID(p_guid);
            } else if (f_type.compareTo("ActWriteOff") == 0) {
                ActWriteOff wb = new ActWriteOff(f_source_id, conn);
                wb.Set_GUID(p_guid);
            }
        }

        public void SetXML(Node p_xml) throws SQLException {
            PreparedStatement stmt;

            String sqlQuery = String.format(SQL_UPD_QUEUE, f_id, null);

            DOMSource ds = new DOMSource(p_xml);
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

            stmt = conn.prepareStatement(sqlQuery);
            stmt.setBlob(1, ts);
            stmt.execute();
            stmt.close();
        }

        public Node GetXML() throws ParserConfigurationException, SAXException, IOException, SQLException,
                                    CloneNotSupportedException {
            Document result;
            if (src_xml == null) {
                if (f_type.compareTo("QueryPartner") == 0) {
                    f_url = Console.UTM_HOST + Producer.URL_TARGET;
                    src_xml = Producer.GetQuery(f_param);
                }
                if (f_type.compareTo("WayBillOut") == 0) {
                    f_url = Console.UTM_HOST + WayBillOut.URL_TARGET;

                    WayBillOut wb_out = new WayBillOut(f_source_id, conn);
                    if (f_param == null) {
                        //                        src_xml = wb_out.GetXML(1);
                        //src_xml = wb_out.GetXML(2);
                        DataModule dm = DataModule.getInstance();
                        Documents doc = dm.GetDocuments(f_source_id, DataModule.WB_OUT);
                        src_xml = SetDocumentsToXML(doc);


                    } else if (f_param.compareTo("Accepted") == 0) {
                        f_url = Console.UTM_HOST + "/in/WayBillAct_v2";
                        src_xml = wb_out.GetAccept("Accepted");
                    } else if (f_param.compareTo("Rejected") == 0) {
                        f_url = Console.UTM_HOST + "/in/WayBillAct_v3";
                        DataModule dm = DataModule.getInstance();
                        Documents doc = dm.GetDocuments(f_source_id, DataModule.WB_OUT_REJECT);
                        src_xml = SetDocumentsToXML(doc);
                        //                        src_xml = wb_out.GetAccept("Rejected");
                    }
                }
                if (f_type.compareTo("WayBill") == 0) {
                    WayBill wb = new WayBill(f_source_id, conn);
                    if (f_param == null) {
                        //src_xml = wb.GetXML();



                    } else if (f_param.compareTo("Accepted") == 0) {
                        f_url = Console.UTM_HOST + "/in/WayBillAct_v3";
                        DataModule dm = DataModule.getInstance();
                        Documents doc = dm.GetDocuments(f_source_id, DataModule.WB_IN_ACCEPT);
                        src_xml = SetDocumentsToXML(doc);
                        //                                                src_xml = wb.confirmWB(true);
                    } else if (f_param.compareTo("Rejected") == 0) {
                        f_url = Console.UTM_HOST + "/in/WayBillAct_v3";
                        DataModule dm = DataModule.getInstance();
                        Documents doc = dm.GetDocuments(f_source_id, DataModule.WB_IN_REJECT);
                        src_xml = SetDocumentsToXML(doc);
                        //                                                src_xml = wb.confirmWB(false);
                    }
                }
            }
            if (f_type.compareTo("ConfirmTicket") == 0) {
                WayBillOut wb = new WayBillOut(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/WayBillTicket";
                src_xml = wb.confirmWBTicket(f_param);
            }
            if (f_type.compareTo("QueryFormA") == 0) {
                InformA infA = new InformA(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/QueryFormF1";
                src_xml = infA.getQuery();
            }
            if (f_type.compareTo("QueryRests") == 0) {
                Rest rest = new Rest();
                f_url = Console.UTM_HOST + "/in/QueryRests_v2"; //"/in/QueryRests";
                src_xml = rest.getQuery();
            }
            if (f_type.compareTo("ActWriteOff") == 0) {
                f_url = Console.UTM_HOST + "/in/ActWriteOff_v3";
                DataModule dm = DataModule.getInstance();
                Documents doc = dm.GetDocuments(f_source_id, DataModule.ACT_WRITE_OFF);
                src_xml = SetDocumentsToXML(doc);

                /*ActWriteOff awo = new ActWriteOff(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/ActWriteOff_v2";
                src_xml = awo.GetXML();*/

            }
            if (f_type.compareTo("ProductAP") == 0) {
                Product prd = new Product(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/QueryAP_v2";
                src_xml = prd.GetQuery(f_param);
            }
            if (f_type.compareTo("QueryForm2History") == 0) {
                InformB ifb = new InformB(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/QueryForm2History";
                Documents doc = ifb.GetQueryHistory();
                src_xml = SetDocumentsToXML(doc);
            }
            if (f_type.compareTo("QueryFormF2") == 0) {
                InformB ifb = new InformB(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/QueryFormF2";
                Documents doc = ifb.GetQueryFormF2();
                src_xml = SetDocumentsToXML(doc);
            }
            if (f_type.compareTo("QueryMarkF2")==0){
                InformB ifb = new InformB(f_source_id, conn);
                f_url = Console.UTM_HOST + "/in/QueryRestBCode";
                src_xml = ifb.GetQueryRestBCode();
                
            }
            if (f_type.compareTo("ActUnfixMark")==0){
                DataModule dm = DataModule.getInstance();
                Documents doc = dm.GetDocuments(f_source_id, DataModule.ACT_MARK_OFF);
                f_url = Console.UTM_HOST + "/in/ActUnFixBarCode";
                src_xml = SetDocumentsToXML(doc);
                
            }
            return src_xml;
        }

        private Node SetDocumentsToXML(Documents doc) {
            Node to_xml = null;
            try {
                JAXBContext context = JAXBContext.newInstance(Documents.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                to_xml = db.newDocument();
                marshaller.marshal(doc, to_xml);
            } catch (JAXBException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            return to_xml;
        }
    }
    public Document document;
    Connection conn;
    public ArrayList<QueueSrc> queueList;
    private static String GET_QUEUE_SQL = "select * from PR_T_EGAIS_QUEUE_S";
    private static String SQL_UPD_QUEUE = "execute procedure PR_T_EGAIS_QUEUE_UPD(%1$s,%2$s,?)";
    private static String SQL_CHK_QUEUE = "select count(f_id) as f_queue from t_egais_queue where f_GUID='%1$s'";

    public HTTPQueue(Connection l_conn) {
        super();
        conn = l_conn;
        queueList = new ArrayList<QueueSrc>();
    }

    public static boolean CheckGUID(String guid, Connection p_conn) {
        boolean result;
        result = false;

        PreparedStatement stmt;

        String sqlQuery = String.format(SQL_CHK_QUEUE, guid);
        try {
            stmt = p_conn.prepareStatement(sqlQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("f_queue") > 0) {
                    result = true;
                }
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public boolean CheckGUID(String guid) {
        boolean result;
        result = false;

        PreparedStatement stmt;

        String sqlQuery = String.format(SQL_CHK_QUEUE, guid);
        try {
            stmt = conn.prepareStatement(sqlQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("f_queue") > 0) {
                    result = true;
                }
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void GetQueue() throws SQLException {


        ResultSet rs;
        PreparedStatement stmt;

        stmt = conn.prepareStatement(GET_QUEUE_SQL);
        rs = stmt.executeQuery();
        QueueSrc queueItem;
        while (rs.next()) {
            queueItem =
                new QueueSrc(rs.getInt("F_ID"), rs.getString("F_TYPE"), rs.getInt("F_SOURCE_ID"),
                             rs.getString("F_PARAM"));
            queueList.add(queueItem);
        }

        stmt.close();
    }

    public void MakeXML() {
        Document result;
        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            result = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<WayBillOut> GetOutWB() {
        String sqlGetQueue = "select f_id from t_egais_ttn_out where f_state =1";
        ArrayList<WayBillOut> listWB = new ArrayList<WayBillOut>();
        ArrayList<Integer> listId = new ArrayList<Integer>();
        WayBillOut wbOut;

        ResultSet rs;
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(sqlGetQueue);
            rs = stmt.executeQuery();
            while (rs.next()) {
                listId.add(rs.getInt("F_ID"));
            }
            for (int i = 0; i < listId.size(); i++) {
                wbOut = new WayBillOut(listId.get(i), conn);
                listWB.add(wbOut);
            }

            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlGetQueue);
            e.printStackTrace();
        }

        return listWB;
    }

    public ArrayList<Producer> GetClientQ() {
        String sqlGetQueue = "select f_id from t_egais_ttn_out where f_state =1";
        ArrayList<Producer> listClient = new ArrayList<Producer>();
        ArrayList<Integer> listId = new ArrayList<Integer>();
        Producer client;
        ResultSet rs;
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(sqlGetQueue);
            rs = stmt.executeQuery();
            while (rs.next()) {
                listId.add(rs.getInt("F_ID"));
            }
            for (int i = 0; i < listId.size(); i++) {
                client = new Producer(listId.get(i), conn);
                listClient.add(client);
            }

        } catch (SQLException e) {
            System.out.println(sqlGetQueue);
            e.printStackTrace();
        }

        return listClient;
    }

}
