package dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import util.JdbcUtil;

import java.util.List;
import java.util.Map;

public class BaseDao {

    public static List<Map<String, Object>> queryForRowSet(String sql, List<Integer> ids, Object... otherParams) {
        NamedParameterJdbcTemplate namedTemplate = JdbcUtil.getNamedTemplate();

        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("ids", ids);
        //consider add otherParams
        return namedTemplate.queryForList(sql, mapSqlParameterSource);

    }
}
