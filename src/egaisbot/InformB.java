package egaisbot;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import ru.fsrar.wegais.queryformf1f2.QueryFormF1F2;
import ru.fsrar.wegais.queryparameters.Parameter;
import ru.fsrar.wegais.queryparameters.QueryParameters;
import ru.fsrar.wegais.wb_doc_single_01.DocBody;
import ru.fsrar.wegais.wb_doc_single_01.Documents;
import ru.fsrar.wegais.wb_doc_single_01.SenderInfo;

public class InformB {
    private static String SET_SQL = "SELECT * FROM PR_T_EGAIS_FORM_B_SET(%1$s, '%2$s', %3$s)";
    private static String GET_SQL = "SELECT * FROM T_EGAIS_FORM_B where f_id=%1$s";
    
    public String regId;
    public String oldRegId;
    public int f_id;
    public WBMarkInfo markInfo;
    InformB(){
        
    }
    public Node getXML(Document productXml,int version) throws ParserConfigurationException {
        Element content;
        Element formBItem;
        Element formBRegID;
        if (version==2){
            content=productXml.createElement("wb:InformF2");
            formBItem=productXml.createElement("pref:InformF2Item");
            formBRegID=productXml.createElement("pref:F2RegId");
        }
        else {
            content=productXml.createElement("wb:InformB");
            formBItem=productXml.createElement("pref:InformBItem");
            formBRegID=productXml.createElement("pref:BRegId");
        }
        
        
            
        
        formBRegID.setTextContent(regId);
        formBItem.appendChild(formBRegID);
        content.appendChild(formBItem);
        return content;
    }
    
    InformB(int p_id,Connection lConn) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(GET_SQL, p_id);
        try {
            stmt = lConn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                regId=rs.getString("F_EGAIS_ID_NEW");
                oldRegId=rs.getString("F_EGAIS_ID");
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }

    InformB(Node node) {
        ParseNode(node);
    }

    public void ParseNode(Node node) {
        Element el = (Element) node;
        NodeList nl = el.getElementsByTagName("pref:BRegId");

        for (int i = 0; i < nl.getLength(); i++) {
            regId = nl.item(i).getTextContent();
        }
        nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/ProductRef_v2","F2RegId");

        for (int i = 0; i < nl.getLength(); i++) {
            regId = nl.item(i).getTextContent();
        }
        nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/CommonV3","F2RegId");
        for (int i = 0; i < nl.getLength(); i++) {
            regId = nl.item(i).getTextContent();
        }
        nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/CommonV3","MarkInfo");
        for (int i = 0; i < nl.getLength(); i++) {
            markInfo = new WBMarkInfo(nl.item(i));
            //regId = nl.item(i).getTextContent();
        }
    }

    public void SaveToBase(int p_product, int p_ttn_pos, Connection lConn) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_SQL, p_product, regId, p_ttn_pos);
        try {
            stmt = lConn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
            if (f_id>0 & markInfo!=null){
                markInfo.SaveToBase(f_id,lConn);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
    }

    Documents GetQueryHistory() {
        Documents qr;
        qr=new Documents();
        SenderInfo sender;
        sender=new SenderInfo();
        sender.setFSRARID(Console.SELF_REGID);
        qr.setOwner(sender);
        DocBody body =new DocBody();
        QueryParameters qp = new QueryParameters();
        Parameter pr = new Parameter();
        pr.setName("RFB");
        pr.setValue(this.regId);
        QueryParameters.Parameters prs =new QueryParameters.Parameters();
        prs.getParameter().add(0,pr);
        qp.getParameters().add(0,prs);
        body.setQueryForm2History(qp);
        qr.setDocument(body);

        return qr;
    }

    Documents GetQueryFormF2() {
        Documents qr;
        qr=new Documents();
        SenderInfo sender;
        sender=new SenderInfo();
        sender.setFSRARID(Console.SELF_REGID);
        qr.setOwner(sender);
        DocBody body =new DocBody();
        QueryFormF1F2 qff2=new QueryFormF1F2();
        qff2.setFormRegId(oldRegId);
        body.setQueryFormF2(qff2);
        qr.setDocument(body);
        return qr;
    }

    Document GetQueryRestBCode() throws ParserConfigurationException, SAXException, IOException {
        Document queryPartner;
        //        java.util.Date date=new java.util.Date();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        queryPartner = db.parse(new File(Console.XML_TEMPLATE));
        Element parameter = queryPartner.createElement("qp:Parameter");
        Element content = queryPartner.createElement("qp:Name");
        content.setTextContent("ФОРМА2");
        parameter.appendChild(content);
        content = queryPartner.createElement("qp:Value");
        content.setTextContent(regId);
        parameter.appendChild(content);
        content = queryPartner.createElement("qp:Parameters");
        content.appendChild(parameter);
        parameter = queryPartner.createElement("ns:QueryRestBCode");
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
}
