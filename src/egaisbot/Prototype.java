package egaisbot;

import java.sql.Connection;

import org.firebirdsql.pool.FBWrappingDataSource;

import org.w3c.dom.Node;

public class Prototype implements InterfaceRoot {
    
    static String SQL_SET="";
    static String SQL_GET="";
    static String URL_STR="";
    Connection conn;
    
    public Prototype() {
        super();
    }

    @Override
    public void Init(int p_id, FBWrappingDataSource dataSource) {
    }

    @Override
    public int Init(Node p_source, String strUrl) {
        return 0;
    }

    @Override
    public void Parce() {
    }

    @Override
    public Node GetXML() {
        return null;
    }

    @Override
    public void SetGUID(String p_guid) {
    }
}
