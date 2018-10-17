package egaisbot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.xml.transform.Result;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Ticket {
    private static String SET_SQL =
        "select * from PR_T_EGAIS_TICKET_SET('%1$s'," + "'%2$s',%3$s,%4$s,%5$s,%6$s,%7$s,%8$s,%9$s,%10$s,?,?" + ")";
    private Node xmlNode;
    private Node resNode;
    private Node operNode;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private Connection conn;
    private String transportID;
    private String regID;
    private String docType;
    private String resConcl;
    private String resComment;
    private String operName;
    private String operResult;
    private String operComment;
    private Date tickDate;
    private Date resDate;
    private Date operDate;
    public boolean remove_url;

    public Ticket(Node xml, Connection lConn) {
        super();
        remove_url=false;
        xmlNode = xml;
        conn = lConn;
        String content;
        if (xmlNode instanceof Element) {
            Element el = (Element) xmlNode;
            NodeList nl = xmlNode.getChildNodes();
            NodeList op;
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName() == "tc:TicketDate") {
                    content = nl.item(i).getTextContent();
                    try {
                        tickDate = format.parse(content);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (nl.item(i).getNodeName() == "tc:TransportId") {
                    transportID = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "tc:RegID") {
                    regID = "'" + nl.item(i).getTextContent() + "'";
                }
                if (nl.item(i).getNodeName() == "tc:DocType") {
                    docType = "'" + nl.item(i).getTextContent() + "'";
                }
                if (nl.item(i).getNodeName() == "tc:Result") {
                    op = nl.item(i).getChildNodes();
                    operNode = xmlNode;
                    for (int j = 0; j < op.getLength(); j++) {
                        if (op.item(j).getNodeName() == "tc:Conclusion") {
                            resConcl = "'" + op.item(j).getTextContent() + "'";
                        }
                        if (op.item(j).getNodeName() == "tc:ConclusionDate") {
                            content = op.item(j).getTextContent();
                            try {
                                resDate = format.parse(content);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        if (op.item(j).getNodeName() == "tc:Comments") {
                            resComment = "'" + op.item(j).getTextContent() + "'";
                        }
                    }
                }
                if (nl.item(i).getNodeName() == "tc:OperationResult") {
                    op = nl.item(i).getChildNodes();
                    resNode = xmlNode;
                    for (int j = 0; j < op.getLength(); j++) {
                        if (op.item(j).getNodeName() == "tc:OperationName") {
                            operName = "'" + op.item(j).getTextContent() + "'";
                        }
                        if (op.item(j).getNodeName() == "tc:OperationResult") {
                            operResult = "'" + op.item(j).getTextContent() + "'";
                        }
                        if (op.item(j).getNodeName() == "tc:OperationDate") {
                            content = op.item(j).getTextContent();
                            try {
                                operDate = format.parse(content);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                        if (op.item(j).getNodeName() == "tc:OperationComment") {
                            operComment = "'" + op.item(j).getTextContent() + "'";
                        }
                    }

                }


            }
        }
        SaveTicket();
    }

    private void SaveTicket() {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        String lstrOperDate;
        DateFormat lformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (operDate != null) {
                lstrOperDate = "'" + lformat.format(operDate) + "'";
            } else
                lstrOperDate = "null";
            sqlQuery =
                String.format(SET_SQL, transportID, lformat.format(tickDate), regID, docType, resConcl, resComment.replace("'","''"),
                              operName, lstrOperDate, operResult.replace("'","''"), operComment.replace("'","''"));
            DOMSource ds = new DOMSource(resNode);
            ByteArrayOutputStream outt = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult();
            streamResult.setOutputStream(outt);

            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
            InputStream ts = new ByteArrayInputStream(outt.toByteArray());
            stmt = conn.prepareStatement(sqlQuery);
            if (resNode != null) {
                stmt.setBlob(1, ts);
            } else
                stmt.setNull(1, java.sql.Types.BLOB);
            //.setBlob(1,)
            ds = new DOMSource(operNode);
            TransformerFactory.newInstance().newTransformer().transform(ds, streamResult);
            ts = new ByteArrayInputStream(outt.toByteArray());
            if (operNode != null) {
                stmt.setBlob(2, ts);
            } else
                stmt.setNull(2, java.sql.Types.BLOB);
            rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("f_remove_url")==1){
                    this.remove_url=true;
                }
            }


        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            //            System.out.println(sqlQuery);
            e.printStackTrace();
        }


    }
}
