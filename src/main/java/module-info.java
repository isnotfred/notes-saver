module com.notesvault {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens fred.was.here.notessaver to javafx.fxml;
    opens fred.was.here.notessaver.controller to javafx.fxml;
    opens fred.was.here.notessaver.model to javafx.base;

    exports fred.was.here.notessaver;
    exports fred.was.here.notessaver.controller;
    exports fred.was.here.notessaver.model;
    exports fred.was.here.notessaver.dao;
    exports fred.was.here.notessaver.util;
}