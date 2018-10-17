package egaisbot;

import java.sql.Connection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WBMarkInfo {
    public int f_id;
    public InformB form2;
    public WBBoxes[] boxes;
    public WBCePos[] cePos;

    private Connection conn;
        
    WBMarkInfo(Node node) {
        if (node instanceof Element){
            Element el = (Element)node;
            NodeList nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/CommonV3", "boxpos");
            boxes = new WBBoxes[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                boxes[i] = new WBBoxes(nl.item(i));
            }
            nl = el.getElementsByTagNameNS("http://fsrar.ru/WEGAIS/CommonV3", "amc");
            cePos = new WBCePos[nl.getLength()];
            for (int i = 0; i < nl.getLength(); i++) {
                cePos[i] = new WBCePos(nl.item(i));
            }
            
        }
    }

    void SaveToBase(int i, Connection connection) {
    }
}
