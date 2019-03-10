package dao;

import dto.Snapshot;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import util.JdbcUtil;

public class SnapshotDao {

    public int insert(Snapshot snapshot) {
        NamedParameterJdbcTemplate template = JdbcUtil.getNamedTemplate();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("name", snapshot.getName());

        KeyHolder keyholder = new GeneratedKeyHolder();
        template.update("insert into diff_test_snapshot (id, name, prc_date) values(" +
                        "diff_test_snapshot_seq.nextval, :name, sysdate)", mapSqlParameterSource, keyholder
                , new String[]{"id"});

        return keyholder.getKey().intValue();
    }

}
