module org.example.vocabhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.desktop;
    requires org.jsoup;
    requires java.sql;

    opens org.example.vocabhub to javafx.fxml;
    exports org.example.vocabhub;
    exports org.example.vocabhub.utils;
    exports org.example.vocabhub.trainer;
    exports org.example.vocabhub.statistics;
    exports org.example.vocabhub.persistence;
    opens org.example.vocabhub.utils to javafx.fxml;
    exports org.example.vocabhub.trainer.strategies;
    exports org.example.vocabhub.trainer.model;
    exports org.example.vocabhub.config;
    opens org.example.vocabhub.config to javafx.fxml;

}