import com.google.common.collect.Lists;
import dao.*;
import dto.Data;
import dto.Snapshot;
import dto.Sql;
import dto.TableParam;
import org.springframework.util.DigestUtils;
import util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SnapshotCreator {

    private static String ENCODING = "UTF-8";

    private SqlDao sqlDao = new SqlDao();
    private SnapshotDao snapshotDao = new SnapshotDao();
    private TableParamDao tableParamDao = new TableParamDao();
    private DataDao dataDao = new DataDao();

    public static void main(String[] args) throws UnsupportedEncodingException {
        SnapshotCreator creator = new SnapshotCreator();

        //input table object to snapshot

        //input params for sql
        List<Integer> synIds = Lists.newArrayList(1, 2, 3);
        //create snapshot
        creator.createSnapshot(synIds, "beforeApply");
    }

    private void createSnapshot(List<Integer> synIds, String snapshotName) throws UnsupportedEncodingException {
        int snapId = snapshotDao.insert(new Snapshot(snapshotName));
        List<Sql> sqls = sqlDao.getAll();
        for (Sql sql : sqls) {
            tableParamDao.insert(new TableParam(snapId, sql.getTblName()));

            String pkArr[] = sql.getPksArr();
            List<Map<String, Object>> rsts = BaseDao.queryForRowSet(sql.getSqlStr(), synIds, null);
            for (Map<String, Object> rst : rsts) {
                TreeMap<String, Object> sortMap = new TreeMap<>(rst);
                Data data = new Data();
                data.setSnapId(snapId);
                data.setTblName(sql.getTblName());
                data.setDk(DigestUtils.md5DigestAsHex(sortMap.toString().getBytes(ENCODING)));
                data.setPk(getPKHash(pkArr, sortMap));
                data.setDataMap(JsonUtil.toJson(sortMap));

                dataDao.insert(data);
            }
        }
    }

    private String getPKHash(String[] pkArr, TreeMap<String, Object> sortMap) {
        StringBuffer sb = new StringBuffer("#");
        for (String pk : pkArr) {
            Object value = sortMap.get(pk.toUpperCase());
            sb.append(value.toString() + "#");
        }
        return sb.toString();
    }
}
