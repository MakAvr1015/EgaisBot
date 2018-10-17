package egaisbot;

import egaisbot.ActWriteOff.AWPosition;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ru.fsrar.wegais.actunfixbarcode.ActUnFixBarCode;
import ru.fsrar.wegais.actunfixbarcode.ActUnFixBarCodePositionType;
import ru.fsrar.wegais.actwriteoff_v3.ActWriteOffTypeV3;
import ru.fsrar.wegais.commonv3.AMCforDocType;

public class ActMarkOff {
    
    private static String GET_SQL = "select * from PR_T_EGAIS_AMC_OFF_GET(%1$s)";
    private static String GET_SQL_STR = "select * from PR_T_EGAIS_AMC_OFF_STR_S(%1$s) order by F_FORM_B_REGID";
    
    public class AMPosition{
        int identity;
        String informBRegId;
        List<String> amcList;
    }
    
    int f_id;
    String f_number;
    Date f_date;
    String f_descr;
    private Connection conn;
    ArrayList<AMPosition> positions;
    
    public ActMarkOff(int p_id, Connection lConn) {
        super();
        ResultSet rs;
        PreparedStatement stmt;
        String sqlQuery;
        if (p_id > 0) {
            f_id = p_id;
        }
        conn = lConn;
        sqlQuery = String.format(GET_SQL, f_id);

        try {
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            while (rs.next()) {
                f_number = rs.getString("F_NUMBER");
                f_date = rs.getDate("F_DATE");
                f_descr = rs.getString("F_DESCR");
            }
            sqlQuery = String.format(GET_SQL_STR, f_id);
            stmt = conn.prepareStatement(sqlQuery);
            rs = stmt.executeQuery();
            positions = new ArrayList<AMPosition>();
            int i=0;
            while (rs.next()) {

                String v_informBRegId = rs.getString("F_FORM_B_REGID");
                if (positions.isEmpty()){
                    positions.add(i,new AMPosition());
                    positions.get(i).identity=i+1;
                    positions.get(i).informBRegId=v_informBRegId;
                    positions.get(i).amcList=new ArrayList<String>();
                    positions.get(i).amcList.add(rs.getString("F_AMC_VAL"));
                }
                else if(positions.get(i).informBRegId.compareTo(v_informBRegId)!=0){
                    i++;
                    positions.add(i,new AMPosition());
                    positions.get(i).identity=i+1;
                    positions.get(i).informBRegId=v_informBRegId;
                    positions.get(i).amcList=new ArrayList<String>();
                    positions.get(i).amcList.add(rs.getString("F_AMC_VAL"));
                } else {
                    positions.get(i).amcList.add(rs.getString("F_AMC_VAL"));
                }

            }


        } catch (SQLException e) {
            System.out.println(sqlQuery);
            e.printStackTrace();

        }

        
    }


    public ActUnFixBarCode GetActMarkOff() {
        ActUnFixBarCode result = new ActUnFixBarCode();
        result.setIdentity(Integer.toString(f_id));
        result.setHeader(new ActUnFixBarCode.Header());
        result.getHeader().setNumber(f_number);
        result.getHeader().setNote(f_descr);
        GregorianCalendar gregory = new GregorianCalendar();
        XMLGregorianCalendar calendar = null;
        gregory.setTime(new Date());
        try {
            calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregory);
            result.getHeader().setActDate(calendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        result.setContent(new ActUnFixBarCode.Content());
        for (int i=0;i<positions.size();i++){
            ActUnFixBarCodePositionType pos=new ActUnFixBarCodePositionType();
            pos.setIdentity(Integer.toString(positions.get(i).identity));
            pos.setInform2RegId(positions.get(i).informBRegId);
            pos.getMarkInfo().add(0,new AMCforDocType());
            pos.getMarkInfo().get(0).getAmc().addAll(positions.get(i).amcList);
            result.getContent().getPosition().add(pos);
        }

        return result;
    }
}
