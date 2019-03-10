package dao;

import dto.Sql;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import util.JdbcUtil;

import java.util.List;

public class SqlDao {

    public List<Sql> getAll() {
        JdbcTemplate template = JdbcUtil.getTemplate();
        // 创建SQL语句
        String sql = "select tbl_name, sql_str, pks from diff_test_sql";
        // 创建Mapper，BeanPropertyRowMapper传入对象类，将会自动映射结果
        RowMapper<Sql> mapper = new BeanPropertyRowMapper<>(Sql.class);
        return template.query(sql, mapper);

    }
}
