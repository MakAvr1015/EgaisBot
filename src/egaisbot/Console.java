package egaisbot;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.OutputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.swing.UIManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import org.w3c.dom.Node;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class Console {
    public static String UTM_HOST = "http://localhost:8080/opt/out";
    private static String BASE_URL = "hyper-2:3050/EGAIS";
    private static String BASE_USER = "SYSDBA";
    private static String BASE_PASSWD = "masterkey";
    public static String XML_TEMPLATE = "template.xml";
    public static String SELF_REGID = "010000002477";
    public static int SELF = 2280;
    public static Document xmlTemplate;

    public Console(String flName) {
        super();
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(flName)));

            Console.UTM_HOST = props.getProperty("UTM_HOST", "http://localhost:8080/opt");
            Console.BASE_URL = props.getProperty("BASE_URL", "localhost:3050/EGAIS");
            Console.BASE_USER = props.getProperty("BASE_USER", "SYSDBA");
            Console.BASE_PASSWD = props.getProperty("BASE_PASSWD", "masterkey");
            Console.XML_TEMPLATE = props.getProperty("XML_TEMPLATE", "template.xml");
//            System.setProperty("file.encoding","UTF8");
            xmlTemplate = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(XML_TEMPLATE));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            xmlTemplate = dBuilder.parse(new File(XML_TEMPLATE));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        EGAISparser egp = /*new EGAISparser("jdbc:firebirdsql:"+BASE_URL,BASE_USER,BASE_PASSWD);*/
            new EGAISparser(BASE_URL, BASE_USER, BASE_PASSWD);
/*        egp.GetFileXML("D:\\mark.xml");*/
        UTMHost uhost;
        ArrayList<UTMHost.HostElement> docList;        
        uhost = new UTMHost(UTM_HOST + "/in");
        
        docList = uhost.GetListDocs();
        for (int i = 0; i < docList.size(); i++) {
            //            uhost.ParseUrl((String)docList.get(i).url);
            System.out.println((String)docList.get(i).url);
            if (docList.get(i).recno==107149){
                try {
                    egp.RemoveXMLDoc(new URL(docList.get(i).url));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
                
        }
        /*WayBill wbill=new WayBill(304,egp.conn);
        try {
            wbill.UpdateRegisterInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        uhost = new UTMHost(UTM_HOST + "/out");

        /*        try{
        egp.GetURLXML(new URL("http://localhost:8080/opt/out/WayBill/2167"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        /*         try{
        egp.RemoveXMLDoc(new URL("http://localhost:8080/opt/out/ReplyFormA/2163"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }             */
        //------------------ClientsQuery-------------------------
        /*        String line;
        try {
            BufferedReader input = new BufferedReader(new FileReader("D:\\clients.inn"));

            while ((line = input.readLine()) != null) {
                try {
                    Node query = Producer.GetQuery(line);
                    egp.PostURLXML(new URL("http://localhost:8080/opt/in/QueryPartner"), query);
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //------------------WayBill------------------------------
        /*        WayBill wb;


        try {
//            wb.confirmWB("d:\\ConfirmWB_295.xml");
        BufferedReader input = new BufferedReader( new FileReader("D:\\WB_list4.id") );
        String line;
         while (( line = input.readLine()) != null){
             wb=new WayBill(Integer.parseInt(line),egp.conn);


            Node confirm;
            confirm=wb.confirmWB();

//            wb.confirmWB("d:\\ConfirmWB_12.xml");
            Document node;
            try {
                node = //egp.PostURLFile(new URL("http://localhost:8080/opt/in/WayBillAct"), "d:\\ConfirmWB_6.xml") ;
                    egp.PostURLXML(new URL("http://localhost:8080/opt/in/WayBillAct"),confirm);
                NodeList nl=node.getElementsByTagName("url");
                for (int i=0;i<nl.getLength();i++){
                    if (nl.item(i).getNodeName()=="url"){
                        wb.SetWB_GUID(nl.item(i).getTextContent());
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            wb=null;
          }
          input.close();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //------------------ActWriteOf-------------------------------
        /*                ActWriteOff act;
        act=new ActWriteOff(6,egp.conn);
        try {
            act.FormXMLFile("d:\\actW_6.xml");
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        //--------------------GET WB--------------------------------
//        egp.GetFileXML("d:\\8888.xml");
        docList = uhost.GetListDocs();
        for (int i = 0; i < docList.size(); i++) {
            //            uhost.ParseUrl((String)docList.get(i).url);
            System.out.println((String)docList.get(i).url);
            //            try {
            try {
                egp.GetURLXML(docList.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //new URL(docList.get(i).url),docList.get(i).replyID);
            //            } catch (MalformedURLException e) {
            //                e.printStackTrace();
            //            }

        }
        //---------------------------------------------------------------

        //--------------------------------WayBillOut
        WayBillOut wb; /*=new WayBillOut(61,egp.conn);
        try {
            Document outWb = wb.GetXML();
            DOMSource ds = new DOMSource(outWb);
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
            }
        } catch (SQLException | IOException | SAXException | ParserConfigurationException e) {
        }*/
        //        egp.GetFileXML("D:\\EGAIS\\AMD64\\product.xml");
        //        egp.GetFileXML("D:\\EGAIS\\AMD64\\wb_236.xml");
        /*        try {
            egp.GetURLXML(new URL("http://localhost:8080/opt/out/WAYBILL/244"));
//            egp.PostURLXML(new URL("http://localhost:8080/opt/in/QueryAP"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        /*        Node node;
        try {
            node = egp.PostURLFile(new URL("http://localhost:8080/opt/in/QueryRests"), "D:\\EGAIS\\AMD64\\ost.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        //--------------------HTTPQueue out------------------
        HTTPQueue queue = new HTTPQueue(egp.conn);

        try {
            queue.GetQueue();
            Node httpPost;
            for (int i = 0; i < queue.queueList.size(); i++) {

                httpPost = queue.queueList.get(i).GetXML();
                System.out.println("Queue"+(String)queue.queueList.get(i).f_type);
//                SaveNodeToFile(httpPost,"d:\\file_w.xml");
                Document node;
                queue.queueList.get(i).SetXML(httpPost);

                node = egp.PostURLXML(new URL(queue.queueList.get(i).f_url), httpPost);
                NodeList nl = node.getElementsByTagName("url");
                for (int j = 0; j < nl.getLength(); j++) {
                    if (nl.item(j).getNodeName() == "url") {
                        queue.queueList.get(i).SetGuid(nl.item(j).getTextContent());
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        /*
        ArrayList<WayBillOut> wbOutList = queue.GetOutWB();
        for (int i = 0; i < wbOutList.size(); i++) {
            try {
                wb = wbOutList.get(i);
                Document outWb = wb.GetXML();
                Document node;
                node = egp.PostURLXML(new URL(WayBillOut.URL_TARGET), outWb);
                NodeList nl = node.getElementsByTagName("url");
                for (int j = 0; j < nl.getLength(); j++) {
                    if (nl.item(j).getNodeName() == "url") {
                        wb.SetState(nl.item(j).getTextContent(), 2);
                    }
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ArrayList<Producer> prodQueue = queue.GetClientQ();
        for (int i = 0; i < prodQueue.size(); i++) {
            try {
                egp.PostURLXML(new URL(Producer.URL_TARGET), prodQueue.get(i).GetQuery());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }



    public static void SaveNodeToFile(Node p_node, String flName){
         DOMSource ds = new DOMSource(p_node);
        OutputStream outt;
        StreamResult streamResult = new StreamResult();

        try {
            outt = new FileOutputStream(flName);
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


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        new Console("d:\\EGAIS\\AMD64\\wb_246.xml");
        //args[0]
        //new Console("E:\\EGAIS\\Base\\JAVA\\getEgais\\EgaisBot\\classes\\EgaisUTM.ini");
        System.out.println(System.getProperty("user.dir")+"\\EgaisUTM.ini");
        new Console(System.getProperty("user.dir")+"\\EgaisUTM.ini");
    }
}
