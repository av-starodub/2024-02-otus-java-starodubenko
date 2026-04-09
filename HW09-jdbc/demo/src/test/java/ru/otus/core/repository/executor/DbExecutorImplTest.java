package ru.otus.core.repository.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DbExecutorImplTest {

    private final DbExecutorImpl dbExecutor = new DbExecutorImpl();

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet generatedKeys;

    @Test
    @DisplayName("returns generated id for insert statements")
    void executeStatementReturnsGeneratedKey() throws Exception {
        when(connection.prepareStatement("insert into test(name) values (?)", Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getLong(1)).thenReturn(42L);

        var result = dbExecutor.executeStatement(connection, "insert into test(name) values (?)", List.of("Alice"));

        assertThat(result).isEqualTo(42L);
        verify(preparedStatement).setObject(1, "Alice");
    }

    @Test
    @DisplayName("returns affected rows when generated keys are absent")
    void executeStatementReturnsUpdatedRowsCount() throws Exception {
        when(connection.prepareStatement("update test set name = ? where id = ?", Statement.RETURN_GENERATED_KEYS))
                .thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(false);

        var result =
                dbExecutor.executeStatement(connection, "update test set name = ? where id = ?", List.of("Bob", 7L));

        assertThat(result).isEqualTo(1L);
        verify(preparedStatement).setObject(1, "Bob");
        verify(preparedStatement).setObject(2, 7L);
    }
}
