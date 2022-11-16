module cowin {
  requires javafx.controls;
  requires javafx.fxml;
  requires com.google.gson;
  requires MaterialFX;
  requires static lombok;
  requires org.apache.httpcomponents.httpcore;
  requires org.apache.httpcomponents.httpclient;

  opens cowin.controllers to
      javafx.fxml;

  exports cowin;
}
