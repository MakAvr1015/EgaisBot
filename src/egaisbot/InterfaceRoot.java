package egaisbot;

import java.sql.Connection;

import org.firebirdsql.pool.FBWrappingDataSource;

import org.w3c.dom.Node;

public interface InterfaceRoot {
    void Init(int p_id,FBWrappingDataSource dataSource);
    int Init(Node p_source,String strUrl);
    void Parce();
    Node GetXML();
    void SetGUID(String p_guid);
    
}
