package ru.yandex.clickhouse;

import org.apache.http.impl.client.CloseableHttpClient;
import ru.yandex.clickhouse.util.CHHttpClientBuilder;
import ru.yandex.clickhouse.util.LogProxy;
import ru.yandex.clickhouse.settings.CHProperties;
import ru.yandex.clickhouse.except.CHUnknownException;
import ru.yandex.clickhouse.util.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author jkee
 * @since 14.03.15
 */
public class CHConnectionImpl implements CHConnection {
    private static final Logger log = Logger.of(CHStatementImpl.class);


    private final CloseableHttpClient httpclient;

    private final CHProperties properties;

    private CHDataSource dataSource;

    private boolean closed = false;

    public CHConnectionImpl(String url){
        this(url, new CHProperties());
    }

    public CHConnectionImpl(String url, Properties info){
        this(url, new CHProperties(info));
    }

    public CHConnectionImpl(String url, CHProperties properties) {
        this.properties = properties;
        this.dataSource = new CHDataSource(url);
        CHHttpClientBuilder clientBuilder = new CHHttpClientBuilder(properties);
        log.debug("new connection");
        httpclient = clientBuilder.buildClient();
    }

    @Override
    public Statement createStatement() throws SQLException {
        return LogProxy.wrap(CHStatement.class, new CHStatementImpl(httpclient, dataSource, properties));
    }

    public CHStatement createCHStatement() throws SQLException {
        return LogProxy.wrap(CHStatement.class, new CHStatementImpl(httpclient, dataSource, properties));
    }

    public PreparedStatement createPreparedStatement(String sql) throws SQLException {
        return LogProxy.wrap(PreparedStatement.class, new CHPreparedStatementImpl(httpclient, dataSource, properties, sql));
    }

    public CHPreparedStatement createCHPreparedStatement(String sql) throws SQLException {
        return LogProxy.wrap(CHPreparedStatement.class, new CHPreparedStatementImpl(httpclient, dataSource, properties, sql));
    }


    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        if (resultSetType != ResultSet.TYPE_FORWARD_ONLY && resultSetConcurrency != ResultSet.CONCUR_READ_ONLY
                && resultSetHoldability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {
            throw new SQLFeatureNotSupportedException();
        }
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return createPreparedStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {
        try {
            httpclient.close();
            closed = true;
        } catch (IOException e) {
            throw new CHUnknownException("HTTP client close exception", e, dataSource.getHost(), dataSource.getPort());
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return LogProxy.wrap(DatabaseMetaData.class, new CHDatabaseMetadata(dataSource.getUrl(), this));
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }


    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createPreparedStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        // todo timeout
        Statement statement = createStatement();
        statement.execute("SELECT 1");
        statement.close();
        // no exception - fine
        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public void setSchema(String schema) throws SQLException {

    }

    public String getSchema() throws SQLException {
        return null;
    }

    public void abort(Executor executor) throws SQLException {
        this.close();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    void cleanConnections(){
        httpclient.getConnectionManager().closeExpiredConnections();
        httpclient.getConnectionManager().closeIdleConnections(2*properties.getSocketTimeout(), TimeUnit.MILLISECONDS);
    }
}
