package util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcUtil {
    private static JdbcTemplate template;

    public static JdbcTemplate getTemplate() {
        if (template == null) {
            // 创建Spring对象
            String config = "applicationContext.xml";
            ApplicationContext app = new ClassPathXmlApplicationContext(config);
            // 创建template
            template = app.getBean("jdbcTemplate", JdbcTemplate.class);
        }
        return template;
    }

    public static NamedParameterJdbcTemplate getNamedTemplate() {
        template = getTemplate();
        return new NamedParameterJdbcTemplate(template.getDataSource());
    }
}
