package util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class SimpleDataSource implements DataSource {
    
    @Override
    public Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public java.io.PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLogWriter(java.io.PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}