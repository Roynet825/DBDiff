package dao;

import dto.TableParam;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import util.JdbcUtil;

public class TableParamDao {

    public int insert(TableParam tableParam) {
        NamedParameterJdbcTemplate template = JdbcUtil.getNamedTemplate();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("tblName", tableParam.getTblName())
                .addValue("snapId", tableParam.getSnapId());


        KeyHolder keyholder = new GeneratedKeyHolder();
        template.update("insert into diff_test_table (id,tbl_name,snap_id ) values(" +
                        "diff_test_table_seq.nextval, :tblName, :snapId )", mapSqlParameterSource, keyholder
                , new String[]{"id"});

        return keyholder.getKey().intValue();
    }

}
