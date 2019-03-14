import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.DataDao;
import dao.SqlDao;
import dto.Data;
import dto.Sql;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class DiffChecker {

    private SqlDao sqlDao = new SqlDao();
    private DataDao dataDao = new DataDao();

    public static void main(String[] args) {
        DiffChecker checker = new DiffChecker();

        checker.check(Sets.newHashSet(), 23, 24); //Empty tableNames means all table
    }

    public void check(final Set<String> tableNames, int snap_id_old, int snap_id_new) {

        // tableName -> Data List
        Map<String, List<Data>> oldDataPerTbl = groupByTblName(dataDao.getBySnapId(snap_id_old));
        Map<String, List<Data>> newDataPerTbl = groupByTblName(dataDao.getBySnapId(snap_id_new));

        List<Sql> realSqls = getRealSqls(tableNames);

        for (Sql sql : realSqls) {
            String tableName = sql.getTblName();

            //PK -> Data List (In case of multiple DK exist for one PK)
            Map<String, List<Data>> oldTblDataMap = generateMap(oldDataPerTbl.get(tableName));
            Map<String, List<Data>> newTblDataMap = generateMap(newDataPerTbl.get(tableName));

            Set<String> oldPks = oldTblDataMap.keySet();
            Set<String> newPks = newTblDataMap.keySet();

            Set<String> delPks = Sets.difference(oldPks, newPks);
            Set<String> addPks = Sets.difference(newPks, oldPks);

            Set<String> possibleModPks = Sets.intersection(oldPks, newPks);
            Set<String> modPks = Sets.newHashSet();

            for (String possibleModPk : possibleModPks) {
                List<Data> oldData = oldTblDataMap.get(possibleModPk);
                List<Data> newData = newTblDataMap.get(possibleModPk);
                if (oldData.size() == newData.size() && oldData.size() == 1) { //Only one DK
                    String oldDk = oldData.get(0).getDk();
                    String newDK = newData.get(0).getDk();
                    if (!StringUtils.equals(oldDk, newDK)) {
                        modPks.add(possibleModPk);
                    }
                } else {
                    System.err.println("PK not correct!! " + tableName + ": " + possibleModPk);
                }
            }

            if (delPks.size() > 0 || addPks.size() > 0 || modPks.size() > 0) {
                System.out.println("****************************************************");
                System.out.println("Table: [" + tableName + "] diff check result:");
            }

            if (delPks.size() > 0) {
                System.out.println("----------------------------------------------------");
                System.out.println("  * Deleted :");
                delPks.forEach(s -> System.out.println("     " + s));
            }

            if (addPks.size() > 0) {
                System.out.println("----------------------------------------------------");
                System.out.println("  * Added :");
                addPks.forEach(s -> System.out.println("     " + s));
            }

            if (modPks.size() > 0) {
                System.out.println("----------------------------------------------------");
                System.out.println("  * Modified:");
                printMods(modPks, oldTblDataMap, newTblDataMap);
            }

            if (delPks.size() > 0 || addPks.size() > 0 || modPks.size() > 0) {
                System.out.println("****************************************************");
            }
        }

    }

    private void printMods(Set<String> modPksMultiDk,
                           Map<String, List<Data>> oldTblDataMap,
                           Map<String, List<Data>> newTblDataMap) {
        for (String modKey : modPksMultiDk) {
            Data oldData = oldTblDataMap.get(modKey).get(0);
            Data newData = newTblDataMap.get(modKey).get(0);
            MapDifference md = jsonDiff(oldData.getDataMap(), newData.getDataMap());
            System.out.println("     " + md.entriesDiffering());
        }
    }

    private Map<String, List<Data>> generateMap(Collection<Data> data) {
        if (data != null) {
            return data.stream().collect(Collectors.groupingBy(Data::getPk));
        }
        return Maps.newHashMap();
    }

    private Map<String, List<Data>> groupByTblName(List<Data> data) {
        return data.stream().collect(Collectors.groupingBy(Data::getTblName));
    }

    private List<Sql> getRealSqls(Set<String> tableNames) {
        if (tableNames == null || tableNames.size() <= 0) {
            return sqlDao.getAll();
        } else {
            return sqlDao.getAll().stream()
                    .filter(sql -> tableNames.contains(sql.getTblName()))
                    .collect(Collectors.toList());
        }
    }

    private MapDifference jsonDiff(String json1, String json2){
        Gson g = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> firstMap = g.fromJson(json1, mapType);
        Map<String, Object> secondMap = g.fromJson(json2, mapType);
        return Maps.difference(firstMap, secondMap);
    }
}
