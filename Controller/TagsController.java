package Controller;

import Model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * This class bridges the View (TagsView) and the Model (GeneralManager).
 * It takes method calls which are triggered by user actions such as clicks,
 * then calls GeneralManager methods to update the Model.
 * GeneralManager and TagManager then update the View using Observer/Observable pattern.
 */
public class TagsController implements Observer {
    /** All variables from 207GUI.fxml file initialized here */
    @FXML private Label currDirectory;
    @FXML private ComboBox<Directory> chooseDirectory;
    @FXML private Label currImageText;
    @FXML private Label currImagePath;
    @FXML private ImageView currImageVisual;
    @FXML private TextField tagsInput;
    @FXML private ComboBox<String> imageNameHistory;
    @FXML private TableView<Tag> tagTable;
    @FXML private TableColumn<Tag, String> tagColumn;
    @FXML private Stage primaryStage;
    @FXML private Pane sidePane;
    @FXML private CheckBox isAllImages;
    @FXML private TableView<Image> imageTable;
    @FXML private TableColumn<Image, String> imageColumn;

    /**
     * The GeneralManager that the controller will collaborate with to update the Model.
     */
    private GeneralManager generalManager = new GeneralManager();


    /**
     * Constructor which prepares Observer/Observable by adding the instance of TagsController to the list of observers
     * in GeneralManager and TagManager classes
     *
     * @param primaryStage  FXML Stage for actions
     */
    public TagsController(Stage primaryStage) {
        this.primaryStage = primaryStage;
        generalManager.addObserver(this);
        generalManager.addObserverToTagManager(this);
    }

    /**
     * Constructor which operates similarly to the previous one, only does not take a Stage.
     */
    public TagsController() {
        generalManager.addObserver(this);
        generalManager.addObserverToTagManager(this);
    }

    /**
     * This method is called by TagsView at startup.
     * First, retrieves serialized files.
     * Then, prompts the user to choose a directory
     * Finally, updates the GUI for the selected directory and lists of images and tags.
     */
    @FXML
    private void initialize() {
        // Opening window for user to select initial "currentDirectory"
        sidePane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        boolean stop = true;
        File selectedDirectory;
        allOrSomeImages();

        if (new File("tags.ser").exists() && new File("directories.ser").exists() && new File("images.ser").exists()) {
            generalManager.deserialize();
            selectedDirectory = generalManager.getRootDirectory();
        } else {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            displayWarning("Note: this will permanently be your root directory. See help.txt for more details.");
            directoryChooser.setTitle("CHOOSE YOUR STARTING DIRECTORY!");
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            generalManager.initialize(selectedDirectory.getAbsolutePath());
        }

        while (stop) {

            if (selectedDirectory != null){
                stop = false;

                // Updating the Model
                generalManager.setCurrentDirectory(selectedDirectory.getAbsolutePath());

                // Updating the View
                generalManager.getCurrentDirectory().getName();
                currDirectory.setText(generalManager.getCurrentDirectory().getName());
                updateDirectoryBox(generalManager.getSubDirectories());
                updateNameHistoryBox(generalManager.getImageRenameLogs());

                tagTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                tagTable.getItems().addAll(generalManager.getAllTags());
                tagColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                tagsInput.clear();

                imageTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
                imageTable.getItems().addAll(generalManager.getSomeImages());
                imageColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

                //Code adapted from:
                //http://blog.ngopal.com.np/2014/02/14/tableview-data-selection-mouse/
                imageTable.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent event) {
                        updateImage();
                    }
                });
                saveCurrentState();
            }
        }


    }

    /**
     * This method is called when the checkbox next to "view images from sub-directories" is clicked
     * It toggles between showing every image underneath the current directory, or only the ones
     * directly beneath it.
     */
    private void allOrSomeImages(){
        isAllImages.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (isAllImages.isSelected()){
                    updateImageBox(generalManager.getAllImages());
                }
                else {
                    updateImageBox(generalManager.getSomeImages());
                }
                generalManager.toggleShowAllImages();
            }
        });
    }

    /**
     * This method is called by GeneralManager and TagManager when the directory, image, or list of tags has changed.
     * This method makes the necessary updates to elements of the GUI
     *
     * @param tempManager   the Manager which called this method (either GeneralManager or TagManager)
     * @param obj           the object which is being observed (image, directory, or list of tags)
     */
    public void update(Observable tempManager, Object obj) {
        if (obj instanceof Image){
            GeneralManager manager = (GeneralManager) tempManager;
            Image image = (Image) obj;

            currImageText.setText(image.getName());
            currImagePath.setText(image.getPath());
            File file = new File(image.getPath());
            currImageVisual.setImage(new javafx.scene.image.Image(file.toURI().toString()));
            updateNameHistoryBox(manager.getImageRenameLogs());

            ArrayList<Image> images;
            if (isAllImages.isSelected()){
                images = generalManager.getAllImages();
            } else {
                images = generalManager.getSomeImages();
            }
            updateImageBox(images);
        } else if (obj instanceof Directory){
            GeneralManager manager = (GeneralManager) tempManager;

            currDirectory.setText(manager.getCurrentDirectory().getName());
            updateDirectoryBox(manager.getSubDirectories());
            updateImageBox(manager.getSomeImages());
        } else if (obj instanceof ArrayList){
            tagTable.getItems().clear();
            tagTable.getItems().addAll(generalManager.getAllTags());
            tagColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            tagsInput.clear();
        }
    }

    /**
     * Takes an ArrayList of Directories which denote contents of current Directory
     * Converts this into an ObservableList and updates combo box
     *
     * @param arr the list of directories to be added to chooseDirectory
     */
    @FXML
    private void updateDirectoryBox(ArrayList<Directory> arr){
        ObservableList<Directory> updateList = FXCollections.observableList(arr);
        chooseDirectory.setItems(updateList);

        chooseDirectory.setConverter(new StringConverter<Directory>() {

            @Override
            public String toString(Directory d) {
                return d.getName();
            }

            @Override
            public Directory fromString(String string) {
                return chooseDirectory.getItems().stream().filter(ap ->
                        ap.getName().equals(string)).findFirst().orElse(null);
            }
        });
    }

    /**
     * Takes an ArrayList of Images which denote contents of current Directory
     * Converts this into an ObservableList and updates combo box
     *
     *  @param arr the list of Images to be added to chooseImage
     */
    @FXML
    private void updateImageBox(ArrayList<Image> arr){
        ObservableList<Image> updateList = FXCollections.observableList(arr);
        imageTable.setItems(updateList);
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    /**
     * Takes an ArrayList of strings which denote names that current image has held
     * Converts this into an ObservableList and updates combo box
     *
     *  @param arr the list of old names to be added to imageNameHistory
     */
    @FXML
    private void updateNameHistoryBox(ArrayList<String> arr){
        ObservableList<String> updateList = FXCollections.observableList(arr);
        imageNameHistory.setItems(updateList);
    }


    /**
     * Called when the user clicks "Navigate to Parent Directory"
     * Takes the user up to the current directory's parent
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void goToParentDirectory(ActionEvent event){
        // Updating the Model
        if (!generalManager.goToParentDirectory()) {
            displayError("You are already in your root directory!");
        }
        generalManager.goToParentDirectory();
        isAllImages.setSelected(false);
    }

    /**
     * Called when the user requests to open current directory in their OS's file viewer
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void openDirectoryInFileViewer(ActionEvent event){
        generalManager.openDirectory();
    }

    /**
     * Extra bonus feature, triggered by user clicking "View Images with Tag"
     * Creates and opens directory with all images associated with selected tag
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void makeImageDirectory(ActionEvent event) throws IOException{
        Tag selectedTag = tagTable.getSelectionModel().getSelectedItem();
        generalManager.openRelatedImages(selectedTag);
    }

    /**
     * When the user clicks "Navigate to sub-Directory", selects a directory, then clicks 'confirm'
     * Updates the current directory
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void updateDirectory(ActionEvent event){

        // Updating the Model
        if (chooseDirectory.getSelectionModel().getSelectedItem() != null) {
            generalManager.setCurrentDirectory(chooseDirectory.getValue());

        }
    }

    /**
     * When the user selects an image from the current directory to be the new current image
     */
    @FXML
    private void updateImage(){
        if (imageTable.getSelectionModel().getSelectedItem() != null) {
            // Updating the Model
            Image currImage = imageTable.getSelectionModel().getSelectedItem();
            generalManager.setCurrentImage(currImage);
            generalManager.setCurrentDirectory(currImage.getParentDirectory());
            isAllImages.setSelected(false);
        }
    }

    /**
     * When the user wishes to move current image to a new directory; trigger by mouse click on "Move Image"
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void moveImage(ActionEvent event){
        // Opening window for user to select target Directory for selected Image
        if (generalManager.getCurrentImage() != null) {

            final DirectoryChooser directoryChooser =
                    new DirectoryChooser();
            final File selectedDirectory =
                    directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                // Updating the Model
                generalManager.moveImage(selectedDirectory.getAbsolutePath());
            }
            allOrSomeImages();
        } else {
            displayError("You must select an image first!");
        }
        saveCurrentState();
    }

    /**
     * When the user wishes to revert the image name to a previous version; trigger by mouse click on "Revert Name"
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void revertName(ActionEvent event){

        if (generalManager.getCurrentImage() != null) {
            // Updating the Model
            if (imageNameHistory.getSelectionModel().getSelectedItem() != null) {
                generalManager.revertImageName(imageNameHistory.getSelectionModel().getSelectedItem());
                allOrSomeImages();
            }
        }
        saveCurrentState();
    }

    /**
     * When the user has chosen tags, and wishes to add them to the selected image
     * Triggered by mouse click on "Add Tags to Image"
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void addTagsToImage(ActionEvent event){
        if (generalManager.getCurrentImage() != null){
            // Updating the model
            ArrayList<Tag> newTags = new ArrayList<>();
            tagTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList<Tag> selectedTags = tagTable.getSelectionModel().getSelectedItems();
            newTags.addAll(selectedTags);

            generalManager.addTagsToImage(newTags);
            allOrSomeImages();

        }
        saveCurrentState();
    }

    /**
     * When the user has chosen tags, and wishes to remove them from the selected image
     * Triggered by mouse click on Remove Tags From Image
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void removeTagsFromImage(ActionEvent event){
        if (generalManager.getCurrentImage() != null){
            // Updating the model
            ArrayList<Tag> newTags = new ArrayList<>();
            tagTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            ObservableList<Tag> selectedTags = tagTable.getSelectionModel().getSelectedItems();
            newTags.addAll(selectedTags);

            generalManager.removeTagsFromImage(newTags);
            allOrSomeImages();

        }
        saveCurrentState();
    }

    /**
     * When the user has selected tags and wishes to delete them from existence
     * Trigger by mouse click on "Delete Tags"
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void deleteTags(ActionEvent event) throws IOException {
        // Calculations
        ArrayList<Tag> tagsToDelete = new ArrayList<>();
        tagTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ObservableList<Tag> selectedTags, allTags;
        allTags = tagTable.getItems();

        selectedTags = tagTable.getSelectionModel().getSelectedItems();
        tagsToDelete.addAll(selectedTags);
        allTags.removeAll(selectedTags);

        // Updating the Model
        generalManager.deleteTags(tagsToDelete);

        saveCurrentState();

    }

    /**
     * When the user has entered some text and wishes to create a new tag from it
     * Triggered by mouse click on "Add Tags"
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void createTags(ActionEvent event) throws IOException {
        // Updating the Model
        Tag newTag = generalManager.createTag(tagsInput.getText());
        if (newTag == null) {
            displayError("That tag already exists!");
        }

        saveCurrentState();
    }

    /**
     * When the user wishes to see all renaming every done, with mouse click on "See All Renaming Logs"
     * Opens text file in new window
     *
     * @param event Mouse click which calls the method
     */
    @FXML
    public void getRenameHistory(ActionEvent event){
        generalManager.getAllRenameLogs();
    }

    /**
     * Serializes tags, and tree file structure created by GeneralManager
     */
    private void saveCurrentState() {
        generalManager.serialize();
    }

    /**Displays given message when an error occurs
     *
     * @param msg Message to be displayed
     */
    private void displayError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("You can't do that!");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    /**Displays given message when a warning is shown
     *
     * @param msg Message to be displayed
     */
    private void displayWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning!");
        alert.setHeaderText("There was a small problem.");
        alert.setContentText(msg);

        alert.showAndWait();
    }


}
