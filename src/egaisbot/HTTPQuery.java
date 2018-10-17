package egaisbot;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTTPQuery {

    public enum QueryType {
        QueryA,
        QueryB,
        QueryRests,
        QueryProduct,
        QueryClients
    };
    private Document queryDoc;

    public HTTPQuery(QueryType p_type, Properties p_params) {
        super();
        queryDoc = Console.xmlTemplate;
        switch (p_type) {
        case QueryClients:
            Element query = queryDoc.createElement("ns:QueryClients");
            Element params = queryDoc.createElement("qp:Parameters");
            Element param = queryDoc.createElement("qp:Parameter");
            /*            p_params.
            for (int i=0;i<)
            Element name;
            Element value;*/

            break;
        case QueryA:
            break;
        case QueryB:
            break;
        case QueryRests:
            break;
        case QueryProduct:
            break;
        }
    }
}
