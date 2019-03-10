import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import dao.DataDao;
import dao.SqlDao;
import dto.Data;
import dto.Sql;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DiffChecker {

    private SqlDao sqlDao = new SqlDao();
    private DataDao dataDao = new DataDao();

    public static void main(String[] args) {
        DiffChecker checker = new DiffChecker();

        checker.check(Sets.newHashSet(), 23, 24); //Empty tableNames means all table
    }

    public void check(final Set<String> tableNames, int snap_id_old, int snap_id_new) {

        // tableName -> Data List
        Map<String, Collection<Data>> oldDataPerTbl = groupByTblName(dataDao.getBySnapId(snap_id_old));
        Map<String, Collection<Data>> newDataPerTbl = groupByTblName(dataDao.getBySnapId(snap_id_new));

        List<Sql> realSqls = getRealSqls(tableNames);

        for (Sql sql : realSqls) {
            String tableName = sql.getTblName();

            //PK -> DK List (In case of multiple DK exist for one PK)
            Map<String, Collection<Data>> oldTblDataMap = generateMap(oldDataPerTbl.get(tableName));
            Map<String, Collection<Data>> newTblDataMap = generateMap(newDataPerTbl.get(tableName));

            Set<String> oldPks = oldTblDataMap.keySet();
            Set<String> newPks = newTblDataMap.keySet();

            Set<String> delPks = Sets.difference(oldPks, newPks);
            Set<String> addPks = Sets.difference(newPks, oldPks);

            Set<String> possibleModPks = Sets.intersection(oldPks, newPks);
            Set<String> modPksMultiDk = Sets.newHashSet();
            Set<String> modPks = Sets.newHashSet();

            for (String possibleModPk : possibleModPks) {
                Collection<Data> oldData = oldTblDataMap.get(possibleModPk);
                Collection<Data> newData = newTblDataMap.get(possibleModPk);
                if (oldData.size() != newData.size()) { //multiple DK exist for one PK
                    modPksMultiDk.add(possibleModPk);
                } else if (oldData.size() == 1) { //Only one DK
                    String oldDk = Lists.newArrayList(oldData).get(0).getDk();
                    String newDK = Lists.newArrayList(newData).get(0).getDk();
                    if (!StringUtils.equals(oldDk, newDK)) {
                        modPks.add(possibleModPk);
                    }
                }
                //Other case , treat same
            }

            System.out.println("****************************************************");
            System.out.println("Table: [" + tableName + "] diff check result:");
            System.out.println("----------------------------------------------------");
            System.out.println("  * Deleted :");
            for (String delKey : delPks) {
                for (Data data :
                        oldTblDataMap.get(delKey)) {
                    System.out.println("     " + delKey + ":" + data.getDataMap());
                }
            }
            System.out.println("----------------------------------------------------");
            System.out.println("  * Added :");
            for (String addKey : addPks) {
                for (Data data :
                        newTblDataMap.get(addKey)) {
                    System.out.println("     " + addKey + ":" + data.getDataMap());
                }
            }
            System.out.println("----------------------------------------------------");
            System.out.println("  * Modified due to multiple DK :");
            printMods(modPksMultiDk, oldTblDataMap, newTblDataMap);
            System.out.println("----------------------------------------------------");
            System.out.println("  * Modified:");
            printMods(modPks, oldTblDataMap, newTblDataMap); //Actually should check modified column by decode JSON
            System.out.println("----------------------------------------------------");

            System.out.println("****************************************************");
        }

    }

    private void printMods(Set<String> modPksMultiDk,
                           Map<String, Collection<Data>> oldTblDataMap,
                           Map<String, Collection<Data>> newTblDataMap) {
        for (String modKey : modPksMultiDk) {
            printMod("     [OLD]", oldTblDataMap, modKey);
            printMod("     [NEW]", newTblDataMap, modKey);
        }
    }

    private void printMod(String s, Map<String, Collection<Data>> oldTblDataMap, String modKey) {
        for (Data data :
                oldTblDataMap.get(modKey)) {
            System.out.println(s + modKey + ":" + data.getDataMap());
        }
    }

    private Map<String, Collection<Data>> generateMap(Collection<Data> data) {
        Multimap<String, Data> pkData = ArrayListMultimap.create();
        if (data != null) {
            for (Data dat : data) {
                pkData.put(dat.getPk(), dat);
            }
        }

        return pkData.asMap();
    }

    private Map<String, Collection<Data>> groupByTblName(List<Data> data) {
        Multimap<String, Data> tableData = ArrayListMultimap.create();
        for (Data dat : data) {
            tableData.put(dat.getTblName(), dat);
        }
        return tableData.asMap();
    }

    private List<Sql> getRealSqls(Set<String> tableNames) {
        List<Sql> realSqls = Lists.newArrayList();
        if (tableNames == null || tableNames.size() <= 0) {
            realSqls.addAll(sqlDao.getAll());
        } else {
            for (Sql sql : sqlDao.getAll()) {
                if (tableNames.contains(sql.getTblName())) {
                    realSqls.add(sql);
                }
            }
        }
        return realSqls;
    }
}
