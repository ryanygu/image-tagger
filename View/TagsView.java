package View;

import Controller.TagsController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class is the View of the MVC, which is the main class and launches the GUI
 * It interacts with TagsController and no other classes
 */
public class TagsView extends Application {

    /**
     * The controller which this TagsView object will interact with
     */
    private TagsController controller;

    /**
     * Constructs a TagsView object
     */
    public TagsView(){}

    /**Initializes objects for the interface and reads all objects from FXML file
     *
     * @param primaryStage the FXML stage to display the interface
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new TagsController(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("207GUI.fxml"));
        primaryStage.setTitle("Tag Manager");
        primaryStage.setScene(new Scene(root, 800, 850));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**Runs the application
     */
    public static void main(String[] args) {
        launch(args);
    }
}