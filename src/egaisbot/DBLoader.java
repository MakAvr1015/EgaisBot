package egaisbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBLoader {
    /*    String strDatabasePath = "/var/firebird/firebird_db1";
    String strURL="jdbc:firebirdsql:local:"+strDatabasePath;
    String strUser="SYSDBA";
    String strPassword="123";*/
    public DBLoader(String strDatabasePath, String strUser, String strPassword) {
        super();
        String strURL = "jdbc:firebirdsql:local:" + strDatabasePath;
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(strURL, strUser, strPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
