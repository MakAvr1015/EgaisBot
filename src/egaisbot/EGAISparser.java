package egaisbot;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.Charset;

import java.util.Date;

import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Types;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.List;

import java.util.Map;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.transform.stream.StreamSource;

import mak.fbBase.DataModule;

import org.firebirdsql.pool.FBWrappingDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import ru.fsrar.wegais.wb_doc_single_01.Documents;


public class EGAISparser {
    private XMLStreamReader resXML;
    private XMLInputFactory factory;
    private static final String LINE_FEED = "\r\n";
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    Document document;
    DocumentBuilderFactory builderFactory;
    DocumentBuilder builder = null;
    Transformer transformer;
    public FBWrappingDataSource dataSource;
    public java.sql.Connection conn;
    private String SET_SQL = "select f_id from PR_EGAIS_UTM_SOURCE_I('%1$s','%2$s','%3$s',?)";

    public EGAISparser(String strURL, String strUser, String strPassword) {
        super();
        factory = XMLInputFactory.newInstance();
        dataSource = new FBWrappingDataSource();
        builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //        Source xslt = new StreamSource(new File("RemovePrefix.xsl"));

        try {
            /*            String driverName = "org.firebirdsql.jdbc.FBDriver";
            java.sql.Driver d = null;
            Class.forName ("org.firebirdsql.jdbc.FBDriver");*/
            transformer = transformerFactory.newTransformer();
            //(xslt);
            dataSource.setDatabase(strURL);
            dataSource.setEncoding("WIN1251");
            //            d = java.sql.DriverManager.getDriver (strURL);
            //            conn = DriverManager.getConnection(strURL,strUser, strPassword);
            conn = dataSource.getConnection(strUser, strPassword);
        } catch (SQLException e) {

            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } /*catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    public void GetURLXML(UTMHost.HostElement hostUrl) throws IOException {

        String replId = hostUrl.replyID;
        BufferedReader bf;
        int v_id = 0;
        HTTPQueue my_queue = new HTTPQueue(conn);
        HttpURLConnection lconn;
        URL url = null;
        url = new URL(hostUrl.url);
        lconn = (HttpURLConnection)url.openConnection();
        lconn.setRequestMethod("GET");
        
        try {

            builder = builderFactory.newDocumentBuilder();
            //            doc.saveToBase(conn);
            document = builder.parse(lconn.getInputStream());
            //            DOMSource source = new DOMSource(document);

            //lconn.getInputStream());

            //transformer.transform(source, new StreamResult(new File("D:\\out.xml")));
/*        } catch (IOException e) {
            e.printStackTrace();*/
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        /*catch (TransformerException e) {
            e.printStackTrace();
        }*/

        ResultSet rs;
        PreparedStatement stmt;

        String sqlQuery;
        sqlQuery = String.format(SET_SQL, hostUrl.url, hostUrl.replyID, hostUrl.recno);

        DOMSource ds = new DOMSource(document);
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
        InputStream ts;

  /*              try {
            ByteArrayOutputStream zoutt = new ByteArrayOutputStream();
            ZipOutputStream zout = new ZipOutputStream(zoutt);
//                ZipOutputStream(new FileOutputStream("d:\\output.zip"));
            ZipEntry entry1=new ZipEntry(String.valueOf(hostUrl.recno)+".xml");
            zout.putNextEntry(entry1);
            zout.write(outt.toByteArray());
            zout.closeEntry();
            zout.finish();
            zout.close();
            
            ts = new ByteArrayInputStream(zoutt.toByteArray());
/*        } catch (IOException e) {
            e.printStackTrace();}*/

        ts = new ByteArrayInputStream(outt.toByteArray());

        //        }

        try {
//           ts = new ByteArrayInputStream(outt.toString("UTF-8").getBytes());

            stmt = conn.prepareStatement(sqlQuery);
            
            stmt.setBlob(1,ts);
            rs = stmt.executeQuery();
            while (rs.next()) {
                v_id = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }
        /*        catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }        */
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);
            Unmarshaller un = jaxbContext.createUnmarshaller();
            DataModule dataModule = DataModule.getInstance();
            dataModule.setConnection(conn);
            Documents doc = (Documents)un.unmarshal(url);
            if (dataModule.saveDocuments(doc, hostUrl.recno, hostUrl.replyID)){
                RemoveXMLDoc(url);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        if (hostUrl.recno != 6835 /* & hostUrl.recno!=64738 & hostUrl.recno!=65067 & hostUrl.recno!=65498 */
            //            & v_id==0
            ) {
            if (document.hasChildNodes()) {
                NodeList nl;
                WayBill wbb;

                nl = document.getElementsByTagName("ns:WayBill");
                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                }
                //                nl = document.getElementsByTagName("ns:WayBill_v2");
                nl = document.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01", "WayBill_v2");
                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }

                }

                /*try {
                    wbb = new WayBill(445,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(453,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(454,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(455,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(456,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(545,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(551,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(552,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(557,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(560,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");
                    wbb = new WayBill(561,conn);
                    wbb.SaveSourceToFile("d:\\NullWB");

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                nl = document.getElementsByTagName("ns:TTNInformBReg");
                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    //                wb.confirmWB();
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }

/*                nl = document.getElementsByTagName("ns:TTNInformF2Reg");
                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    //                wb.confirmWB();
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }*/


                nl = document.getElementsByTagName("ns:WayBillAct");
                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //nl = document.getElementsByTagName("ns:WayBillAct_v2");
                nl = document.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/WB_DOC_SINGLE_01", "WayBillAct_v2");


                for (int i = 0; i < nl.getLength(); i++) {
                    WayBill wb = new WayBill(nl.item(i), conn, hostUrl.recno);
                    if (wb.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                nl = document.getElementsByTagName("ns:ConfirmTicket");
                for (int i = 0; i < nl.getLength(); i++) {

                }
                /*nl = document.getElementsByTagName("ns:Ticket");
                for (int i = 0; i < nl.getLength(); i++) {
                    Ticket tk = new Ticket(nl.item(i), conn);
                    if (tk.remove_url) {
                        try {
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }

                }*/
                //-----------------ReplyProduct------------------------------
/*                if (hostUrl.url.contains("ReplyAP_v2")) {
                    nl = document.getElementsByTagName("rap:Product");
                    for (int i = 0; i < nl.getLength(); i++) {
                        Product prd = new Product(nl.item(i), conn, new Date(), 2);
                        //                    pr=null;
                    }
                    try {
                        RemoveXMLDoc(new URL(hostUrl.url));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }*/
                //------------------- ReplyClients----------------------------
                if (hostUrl.url.contains("ReplyPartner")) {
                    nl = document.getElementsByTagName("rc:Client");
                    for (int i = 0; i < nl.getLength(); i++) {
                        Producer pr = new Producer(nl.item(i), conn, null);
                    }
                    try {
                        RemoveXMLDoc(new URL(hostUrl.url));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                }
                /*                if (hostUrl.url.contains("ReplyClient_v2")) {
                    nl = document.getElementsByTagName("rc:Client");
                    for (int i = 0; i < nl.getLength(); i++) {
                        Producer pr = new Producer(nl.item(i), conn,null,0 );
                    }
                    try {
                        RemoveXMLDoc(new URL(hostUrl.url));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                }*/
                //--------------------ReplyRests------------------------------
                if (hostUrl.url.contains("ReplyRests")) {

                    nl = document.getElementsByTagName("ns:ReplyRests");
                    if (my_queue.CheckGUID(hostUrl.replyID)) {
                        for (int i = 0; i < nl.getLength(); i++) {
                            Rest rst = new Rest(nl.item(i), conn, replId);
                            try {
                                RemoveXMLDoc(new URL(hostUrl.url));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                /*                if (hostUrl.url.contains("ReplyRests_v2")) {

                    nl = document.getElementsByTagName("ns:ReplyRests_v2");
                    if (my_queue.CheckGUID(hostUrl.replyID)) {
                        for (int i = 0; i < nl.getLength(); i++) {
                            Rest rst = new Rest(nl.item(i), conn, replId);
                            try {
                                RemoveXMLDoc(new URL(hostUrl.url));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }*/

                if (hostUrl.url.contains("ReplyFormA")) {
                    nl = document.getElementsByTagName("ns:ReplyFormA");
                    for (int i = 0; i < nl.getLength(); i++) {
                        InformA infA = new InformA();
                        try {
                            infA.saveQuery(nl.item(i), conn);
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                /*                if (hostUrl.url.contains("ReplyForm1")) {
                    nl = document.getElementsByTagName("ns:ReplyForm1");
                    for (int i = 0; i < nl.getLength(); i++) {
                        InformA infA = new InformA();
                        try {
                            infA.saveQuery(nl.item(i), conn);
                            RemoveXMLDoc(new URL(hostUrl.url));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }*/

            }
            //-------------------------------------------------------------

        } /*else {
            try {
                RemoveXMLDoc(new URL(hostUrl.url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }*/
    }

    public void RemoveXMLDoc(URL url) {
        HttpURLConnection lconn;
        try {

            lconn = (HttpURLConnection)url.openConnection();
            //            lconn.setDoOutput(true);
            //            lconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            lconn.setRequestMethod("DELETE");
            int resp = lconn.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void GetFileXML(String fileXML) {
        /*        try {
            resXML = factory.createXMLStreamReader(new FileReader(fileXML));
        } catch (XMLStreamException e) {
        } catch (FileNotFoundException e) {
        }
        try {
            while(resXML.hasNext()){
            resXML.next();
            if(resXML.getEventType() == XMLStreamReader.START_ELEMENT){
//                System.out.println(resXML.getLocalName());
            }
        }
        } catch (XMLStreamException e) {
        }*/


        try {
            builder = builderFactory.newDocumentBuilder();
            document = builder.parse(new File(fileXML));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Documents.class);
            Unmarshaller un = jaxbContext.createUnmarshaller();
            DataModule dataModule = DataModule.getInstance();
            dataModule.setConnection(conn);
            Documents doc = (Documents)un.unmarshal(new File(fileXML));
            if (dataModule.saveDocuments(doc,1,"" )){
//                RemoveXMLDoc(url);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        
        if (document.hasChildNodes()) {
            NodeList nl;
            //            NodeList nl=document.getChildNodes();
            nl = document.getElementsByTagName("rap:Product");
            for (int i = 0; i < nl.getLength(); i++) {
                //                Product pr = new Product(nl.item(i), conn);

            }

            nl = document.getElementsByTagName("ns:WayBill");
            for (int i = 0; i < nl.getLength(); i++) {
                //                nl.item(i).getNodeName();
                //                nl.item(i).
                //                System.out.println(nl.item(i).getNodeName());
                //                Product pr = new Product(nl.item(i),conn);
                //WayBill wb= new WayBill(nl.item(i),conn);
                //                                System.out.println(wb.identity);



            }
            nl = document.getElementsByTagName("ns:WayBillAct");
            for (int i = 0; i < nl.getLength(); i++) {
                WayBill wb = new WayBill(nl.item(i), conn, 1);
            }
            nl = document.getElementsByTagName("ns:WayBillAct_v2");
            for (int i = 0; i < nl.getLength(); i++) {
                WayBill wb = new WayBill(nl.item(i), conn, 1);
            }
            nl = document.getElementsByTagName("ns:TTNInformBReg");
            for (int i = 0; i < nl.getLength(); i++) {
                WayBill wb = new WayBill(nl.item(i), conn, 55303);
                //                wb.confirmWB();
            }

        }

    }

    public Document PostURLXML(URL url, Node xmlSend) {
        HttpURLConnection conn;

        int responseCode;
        OutputStream outt;
        String boundary = Long.toHexString(System.currentTimeMillis());
        document = null;
        //        String boundary = "===" + System.currentTimeMillis() + "===";
        ;
        try {

            //            FileInputStream fl;
            //            fl = new FileInputStream(new File("d:\\egais\\AMD64\\production.xml"));
            //            int size = fl.available();
            //(xmlSend,outt, true);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            DOMSource source = new DOMSource(xmlSend);
            transformer.transform(source, result);
            byte b[] = bos.toByteArray();


            //            byte b[] = xmlSend.toString().getBytes();//new byte[size];
            //            fl.read(b);
            conn = (HttpURLConnection)url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "BrowserForDummies/4.67b");
            conn.setRequestProperty("Content-Type", "multipart/form-data boundary=" + boundary);
            //            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(bos.size()));
            //            conn.setRequestProperty("Connection","keep-alive");
            //            conn.setRequestProperty("Keep-Alive","300");
            conn.setRequestMethod("POST");
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(LINE_FEED);
            dos.writeBytes(LINE_FEED);
            dos.writeBytes("--" + boundary + LINE_FEED);
            dos.writeBytes("Content-Disposition: form-data; name=\"xml_file\"" + LINE_FEED);
            dos.writeBytes("Content-Type: text/xml; charset=UTF-8" + LINE_FEED);
            dos.writeBytes(LINE_FEED);
            dos.write(b);
            dos.writeBytes(LINE_FEED);
            //            dos.writeBytes("--" + boundary + LINE_FEED);
            dos.writeBytes("--" + boundary + "--" + LINE_FEED);
            dos.writeBytes(LINE_FEED);
            dos.flush();
            dos.close();

            //            outt = new FileOutputStream("d:\\stream_out");

            builder = builderFactory.newDocumentBuilder();
            document = builder.parse(conn.getInputStream());
            responseCode = conn.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);
        } catch (IOException e) {
            //            this.showDialog(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } //catch (ParserConfigurationException e) {}
        //catch (SAXException e) {}
        return document;
    }

    public Node PostURLFile(URL url, String fileName) {
        HttpURLConnection conn;

        int responseCode;
        String boundary = "===" + System.currentTimeMillis() + "===";
        ;
        try {

            FileInputStream fl;
            fl = new FileInputStream(new File(fileName));
            int size = fl.available();
            byte b[] = new byte[size];
            fl.read(b);
            conn = (HttpURLConnection)url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("User-Agent", "CodeJava Agent");
            conn.setRequestMethod("POST");

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes("--" + boundary + LINE_FEED);
            dos.writeBytes("Content-Disposition: form-data; name=\"xml_file\"");
            dos.writeBytes(LINE_FEED);
            dos.writeBytes("Content-Type: text/xml; charset=UTF-8");
            dos.writeBytes(LINE_FEED);
            dos.writeBytes(LINE_FEED);
            dos.write(b);
            dos.writeBytes(LINE_FEED);
            dos.flush();
            dos.close();
            builder = builderFactory.newDocumentBuilder();
            document = builder.parse(conn.getInputStream());

            responseCode = conn.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);


        } catch (IOException e) {
            //            this.showDialog(getApplicationContext(), e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } //catch (ParserConfigurationException e) {}
        //catch (SAXException e) {}
        return document;
    }

}
