//module drinkshop {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.base;
//
//    requires org.controlsfx.controls;
//
//    opens drinkshop.ui to javafx.fxml;
//    exports drinkshop.ui;
////
////    opens drinkshop.repository;
////    opens drinkshop.service.validator;
////    opens drinkshop.service;
////    opens drinkshop.domain to  javafx.base;
////    exports drinkshop.domain;
//    exports drinkshop.domain;
//    opens drinkshop.domain to javafx.base;
//
//    // 🔥 IMPORTANT pentru Mockito
//    opens drinkshop.repository to org.mockito;
//    opens drinkshop.service.validator to org.mockito;
//    opens drinkshop.service to org.mockito;
//}

module drinkshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;

    // UI
    opens drinkshop.ui to javafx.fxml;
    exports drinkshop.ui;

    // domain
    opens drinkshop.domain to javafx.base;
    exports drinkshop.domain;

    // 🔥 IMPORTANT pentru Mockito + test runtime
    opens drinkshop.repository to org.mockito, junit;
    opens drinkshop.service to org.mockito, junit;
    opens drinkshop.service.validator to org.mockito, junit;

    // dacă mai ai probleme:
    exports drinkshop.repository;
    exports drinkshop.service;
    exports drinkshop.service.validator;
}