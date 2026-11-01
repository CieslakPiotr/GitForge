package dev.gitforge;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

class InfrastructureTest extends IntegrationTest {

    @Autowired JdbcTemplate jdbc;
    @Autowired StringRedisTemplate redis;

    @Test
    void connectsToPostgresAndAppliesMigrations() {
        Integer applied =
                jdbc.queryForObject(
                        "SELECT count(*) FROM flyway_schema_history WHERE success", Integer.class);

        assertThat(applied).isPositive();
    }

    @Test
    void connectsToRedis() {
        redis.opsForValue().set("integration-test", "ok");

        assertThat(redis.opsForValue().get("integration-test")).isEqualTo("ok");
    }
}
