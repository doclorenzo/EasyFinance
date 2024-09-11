module com.manno.easyfinance {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.naming;
    requires java.desktop;

    opens com.manno.easyfinance to javafx.fxml;
    exports com.manno.easyfinance;
    exports com.manno.easyfinance.controller;
    opens com.manno.easyfinance.controller to javafx.fxml;
    exports com.manno.easyfinance.persistence.model;
    opens com.manno.easyfinance.persistence.model to javafx.fxml;
    exports com.manno.easyfinance.controller.DeatiledTableManager;
    opens com.manno.easyfinance.controller.DeatiledTableManager to javafx.fxml;
}