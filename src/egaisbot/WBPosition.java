package egaisbot;

import java.io.IOException;

//import java.security.Identity;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.sql.Statement;

import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class WBPosition {
    public int f_id;
    public String identity;
    public float quantity;
    public float price;
    public Product product;
    public InformA infA;
    public InformB infB;
    public Date wbDate;
    //WBMarkInfo[] markInfo;

    private Connection conn;

    private static String SET_SQL =
        "SELECT * FROM PR_T_EGAIS_TTN_POSITION_SET(%1$s, '%2$s', %3$s, %4$s, %5$s, %6$s, null)";
    public String get_sql;

    public Node getXml(Node parent, int version) throws ParserConfigurationException, SQLException, SAXException,
                                                        IOException {
        Document productXml;
        productXml = parent.getOwnerDocument();
        Element content = productXml.createElement("wb:Identity");
        content.setTextContent(identity);
        parent.appendChild(content);
        parent.appendChild(product.GetXML(parent, version));
        content = productXml.createElement("wb:Quantity");
        content.setTextContent(String.valueOf(quantity));
        parent.appendChild(content);
        content = productXml.createElement("wb:Price");
        content.setTextContent(String.valueOf(price));
        parent.appendChild(content);
        if (version == 2) {
            content = productXml.createElement("wb:Party");
            content.setTextContent(String.valueOf(identity));
            parent.appendChild(content);
        }
        parent.appendChild(infA.getXML(productXml,version));
        parent.appendChild(infB.getXML(productXml,version));
        return parent;
    }

    WBPosition(String lSql, Connection lConn) {
        conn = lConn;
        get_sql = lSql;

    }

    public void getFromDB() {
        ResultSet rs;

        Statement stmt;
        try {
            //            stmt = conn.prepareStatement(get_sql);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(get_sql);
            int v_form_a = 0, v_form_b = 0, v_product = 0;
            while (rs.next()) {
                f_id = rs.getInt("F_ID");
                v_product = rs.getInt("F_PRODUCT");

                v_form_b = rs.getInt("F_FORM_B");

                v_form_a = rs.getInt("F_FORM_A");

                identity = String.valueOf(rs.getInt("f_ident"));
                //String.valueOf(f_id);
                quantity = rs.getFloat("F_QUAN");
                price = rs.getFloat("F_PRICE");
            }
            product = new Product(v_product, conn);
            infB = new InformB(v_form_b, conn);
            infA = new InformA(v_form_a, conn);
        } catch (SQLException e) {
            System.out.println(get_sql);
            e.printStackTrace();
        }

    }

    WBPosition(Node node, Connection lConn, Date p_wb_date) {
        wbDate = p_wb_date;
        conn = lConn;
        if (node instanceof Element) {
            NodeList nl = node.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                String nodeName=nl.item(i).getLocalName();
                if (nodeName!=null){
                    switch (nodeName.toLowerCase()){
                    case "identity":
                        identity = nl.item(i).getTextContent();
                        break;
                    case "quantity":
                        quantity = Float.parseFloat(nl.item(i).getTextContent());
                        break;
                    case "price":
                        price = Float.parseFloat(nl.item(i).getTextContent());
                        break;
                    case "product":
                        product = new Product(nl.item(i), conn, wbDate, 2);
                        break;
                    case "informa":
                        if (infA == null) {
                            infA = new InformA();
                        }
                        infA.ParseNode(nl.item(i));
                        break;
                    case "informf1":
                        if (infA == null) {
                            infA = new InformA();
                        }
                        infA.ParseNode(nl.item(i));
                        break;
                    case "informb":
                        if (infB == null) {
                            infB = new InformB();
                        }
                        infB.ParseNode(nl.item(i));
                        break;
                    case "informf2":
                        if (infB == null) {
                            infB = new InformB();
                        }
                        infB.ParseNode(nl.item(i));
                        break;                        
                    }
                }
                /*if (nl.item(i).getNodeName() == "wb:Identity") {
                    identity = nl.item(i).getTextContent();
                }
                if (nl.item(i).getNodeName() == "wb:Quantity") {
                    quantity = Float.parseFloat(nl.item(i).getTextContent());
                }
                if (nl.item(i).getNodeName() == "wb:Price") {
                    price = Float.parseFloat(nl.item(i).getTextContent());
                }
                if (nl.item(i).getNodeName() == "wb:Product") {
                    product = new Product(nl.item(i), conn, wbDate, 2);
                }
                if (nl.item(i).getNodeName() == "wb:InformA") {
                    if (infA == null) {
                        infA = new InformA();
                    }
                    infA.ParseNode(nl.item(i));
                }
                if (nl.item(i).getNodeName() == "wb:InformF1") {
                    if (infA == null) {
                        infA = new InformA();
                    }
                    infA.ParseNode(nl.item(i));


                }
                
                if (nl.item(i).getNodeName() == "wb:InformB") {
                    if (infB == null) {
                        infB = new InformB();
                    }
                    infB.ParseNode(nl.item(i));

                }

                if (nl.item(i).getNodeName() == "wb:InformF2") {
                    if (infB == null) {
                        infB = new InformB();
                    }
                    infB.ParseNode(nl.item(i));

                }*/

            }
            if (infA != null) {
                infA.SaveToBase(product.f_id, lConn);
            }
            /*            if (infB != null){
                infB.SaveToBase(product.f_id, lConn);}*/


        }
    }

    public void SaveToBase(int p_ttn) {
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery = String.format(SET_SQL, p_ttn, identity, quantity, price, product.f_id, infA.f_id);
        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_id = rs.getInt(1);
            }
            if (f_id > 0) {
                infB.SaveToBase(this.product.f_id, this.f_id, conn);
            }
        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();
        }

    }
}
