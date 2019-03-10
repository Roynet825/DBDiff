package dto;

import java.io.Serializable;

public class Sql implements Serializable {
    private String tblName;
    private String sqlStr;
    private String pks;

    public String getTblName() {
        return tblName;
    }

    public void setTblName(String tblName) {
        this.tblName = tblName;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }

    public String getPks() {
        return pks;
    }

    public void setPks(String pks) {
        this.pks = pks;
    }

    public String[] getPksArr() {
        return pks.split(",");
    }
}
