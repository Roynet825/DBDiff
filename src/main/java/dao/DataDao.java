package dao;

import dto.Data;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import util.JdbcUtil;

import java.util.List;

public class DataDao {
    public int insert(Data data) {
        NamedParameterJdbcTemplate template = JdbcUtil.getNamedTemplate();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("snap_id", data.getSnapId())
                .addValue("tbl_name", data.getTblName())
                .addValue("dk", data.getDk())
                .addValue("pk", data.getPk())
                .addValue("data_map", data.getDataMap());

        KeyHolder keyholder = new GeneratedKeyHolder();
        template.update("insert into diff_test_data (id, snap_id, tbl_name, dk, pk, data_map) values(" +
                        "diff_test_data_seq.nextval, :snap_id, :tbl_name, :dk, :pk, :data_map)", mapSqlParameterSource, keyholder
                , new String[]{"id"});

        return keyholder.getKey().intValue();
    }

    public List<Data> getBySnapId(int snap_id) {
        JdbcTemplate template = JdbcUtil.getTemplate();
        // 创建SQL语句
        String sql = "select id, snap_id, pk, dk, tbl_name, data_map from diff_test_data where snap_id=? order by tbl_name";
        // 创建Mapper，BeanPropertyRowMapper传入对象类，将会自动映射结果
        RowMapper<Data> mapper = new BeanPropertyRowMapper<>(Data.class);
        return template.query(sql, new Object[]{snap_id}, mapper);
    }
}
