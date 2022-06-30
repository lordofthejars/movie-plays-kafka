package org.acme;

public class MySqlJdbcParser {

    private String host;
    private String port;
    private String database;

    public static MySqlJdbcParser parse(String jdbcUrl) {

        MySqlJdbcParser mySqlJdbcParser = new MySqlJdbcParser();

        int pos, pos1, pos2;
        String connUri;

        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:")
                || (pos1 = jdbcUrl.indexOf(':', 5)) == -1)
            throw new IllegalArgumentException("Invalid JDBC url.");

        if ((pos2 = jdbcUrl.indexOf(';', pos1)) == -1) {
            connUri = jdbcUrl.substring(pos1 + 1);
        } else {
            connUri = jdbcUrl.substring(pos1 + 1, pos2);
        }

        if (connUri.startsWith("//")) {
            if ((pos = connUri.indexOf('/', 2)) != -1) {
                mySqlJdbcParser.host = connUri.substring(2, pos);
                mySqlJdbcParser.database = connUri.substring(pos + 1);

                if ((pos = mySqlJdbcParser.host.indexOf(':')) != -1) {
                    mySqlJdbcParser.port = mySqlJdbcParser.host.substring(pos + 1);
                    mySqlJdbcParser.host = mySqlJdbcParser.host.substring(0, pos);
                }
            }
        } else {
            mySqlJdbcParser.database = connUri;
        }

        return mySqlJdbcParser;
    }

    @Override
    public String toString() {
        return "MySqlJdbcParser [database=" + database + ", host=" + host + ", port=" + port + "]";
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }


}