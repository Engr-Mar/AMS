package com.gasc.ams.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // PostgreSQL JDBC URL
    private static final String URL =
            // "jdbc:postgresql://192.168.1.106:5432/AMS";
            "jdbc:postgresql://192.168.1.114:5432/AMS";
    // change host, port, and database name to match your PostgreSQL configuration
    // PostgreSQLの構成に合わせてホスト、ポート、データベース名を変更してください

    // username and password
    private static final String USER = "postgres";
    private static final String PASSWORD = "ams";

    private DatabaseConfig() {}

    public static Connection getConnection() throws SQLException, ClassNotFoundException {

        // PostgreSQL JDBC Driver
        Class.forName("org.postgresql.Driver");

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ORACLE 
    // private static final String URL_TNS_DESCRIPTOR =
    //         "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.106)(PORT=1521))"
    //         //  change the host and port to match your Oracle database configuration
    //          // Oracle データベースの構成に合わせてホストとポートを変更します

    //         //  use this commented code below if using pc name. note that it may not work all the time if the user pc due to firewall or privacy
    //         // PC名を使用する場合は、以下のコメント付きコードを使用してください。ファイアウォールやプライバシーのためにユーザーのPCが常に機能するとは限りません。
            
    //         //"jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=GASPC-103)(PORT=1521))"

    //         // change the service name to match your Oracle database configuration
    //          // Oracle データベースの構成に合わせてサービス名を変更します
    //         + "(CONNECT_DATA=(SERVICE_NAME=AMS_DB)))";
    // // Example TNS alias (replace with the alias name present in tnsnames.ora)

    // // ステップ3で使用したTNSニックネームに基づいて@を変更します
    // // change the @ based on the TNS nickname you used on step 3
    // private static final String URL_TNS_ALIAS_EXAMPLE = "jdbc:oracle:thin:@AMS";

    // // 手順 5 で使用したユーザー名とパスワードに基づいて以下の値を変更します
    // // change the values below based on the user and password used in step 5
    // private static final String USER = "GAS";
    // private static final String PASSWORD = "GAS";

    // private DatabaseConfig() {}

    // public static Connection getConnection() throws SQLException, ClassNotFoundException {
    //     Class.forName("oracle.jdbc.driver.OracleDriver");
    //     return DriverManager.getConnection(URL_TNS_DESCRIPTOR, USER, PASSWORD);
    // }

    // public static String exampleTnsAliasUrl() {
    //     return URL_TNS_ALIAS_EXAMPLE;
    // }

    // MS SQL Server を使用する場合は、このコメントを解除してください
    // 上記のコードをコメントアウトしてください
    // 以下の詳細は、お使いの MS DB に合わせて変更してください
    // uncomment this when using MS SQL Server
    // Comment the codes above
    // change the details below base on your MS DB
    
    // private static final String URL =
    //         "jdbc:sqlserver://192.168.1.106:1433;databaseName=AMS_DB;encrypt=false;";
    // private static final String USER = "GAS";
    // private static final String PASSWORD = "GAS";

    // private DatabaseConfig() {}

    // public static Connection getConnection() throws SQLException, ClassNotFoundException {
    //     Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    //     return DriverManager.getConnection(URL, USER, PASSWORD);
    // }
}