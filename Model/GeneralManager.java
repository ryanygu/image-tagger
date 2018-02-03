package Model;

import Controller.TagsController;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * This class manages interactions between the controller and other managers.
 * Takes method calls from TagsController and calls methods from TagManager, DirectoryManager, and ImageManager
 */
public class GeneralManager extends Observable {

    /**
     * Observer for currentImage and currentDirectory
     */
    private ArrayList<Observer> observers = new ArrayList<>();

    /**
     * The object that will be taking requests on behalf of Image objects
     */
    private ImageManager imageManager;

    /**
     * The object that will be taking requests on behalf of Directory objects
     */
    private DirectoryManager directoryManager;

    /**
     * The object that will be taking requests on behalf of Tag objects
     */
    private TagManager tagManager;

    /**
     * Keeps track of the current selected directory.
     */
    private Directory currentDirectory;

    /**
     * Keeps track of the current selected image.
     */
    private Image currentImage;

    /**
     * Keeps track of whether or not to show all images under the current directory.
     */
    private boolean showAllImages = false;

    /**
     * The array of all image extensions supported by this program.
     */
    private static final String[] EXTENSIONS = new String[]{"gif", "png", "bmp", "jpeg", "tif", "raw", "jpg"};

    /**
     * Initializes an instance of GeneralManager.
     */
    public GeneralManager() {
        this.imageManager = new ImageManager();
        this.tagManager = new TagManager();
        this.directoryManager = new DirectoryManager();
    }

    /**
     * Adds an observer to this general manager's list of observers for currentImage and currentDirectory
     *
     * @param o: the observer to be added
     */
    @Override
    public void addObserver(Observer o){
        this.observers.add(o);
    }

    /**
     * Passes the TagsController instance to TagManager so it may be an observer of allTags
     *
     * @param o: the observer to be added
     */
    public void addObserverToTagManager(Observer o){
        this.tagManager.addObserver(o);
    }

    /**
     * Notifies observers of changes in currentImage
     *
     * @param currentImage  the image most recently selected by the user
     */
    private void notifyObservers(Image currentImage){
        for (Observer o : observers){
            o.update(this, currentImage);
        }
    }

    /**
     * Removes the selected tags from existence.
     * Called when the user wants to delete a Tag completely.
     *
     * Must complete the following:
     * i) Remove the deleted tags from all the images
     * ii) Delete the tags from existence
     *
     * i) Remove the tag from allTags
     *
     * @param tags      the list of tags to delete
     */
    public void deleteTags(ArrayList<Tag> tags) {
        //imageManager.removeDeletedTags(tags, getAllImages());
        tagManager.deleteTags(tags);
        if (currentImage != null) {
            notifyObservers(currentImage);
        }
    }

    /**
     * Updates the current image with the given set of tags.
     * Called when the user wants to update an image's tags.
     *
     * Must complete the following:
     * i) remove image from its previous tags' list of images
     * ii) add image to its new tags' list of images
     * iii) update image's tags to new set of tags
     *
     * @param tags      the list of tags to update the current image with
     */
    private void updateImageTags(ArrayList<Tag> tags) {
        tagManager.removeImageFromTags(currentImage, currentImage.getTags());
        tagManager.addImageToTags(currentImage, tags);
        imageManager.updateImageTags(currentImage, tags);
        notifyObservers(currentImage);
    }

    /**
     * Adds the given list of tags to the current image's list of tags.
     *
     * @param tags      the list of tags to add to the current image
     */
    public void addTagsToImage(ArrayList<Tag> tags) {
        if (!getNewTags(tags).isEmpty()) {
            tagManager.addImageToTags(currentImage, getNewTags(tags));
            imageManager.addTagsToImage(currentImage, getNewTags(tags));
            notifyObservers(currentImage);
        }
    }

    /**
     * Returns only the tags in the given tags that are not already in the current image.
     *
     * @param tags      the list of tags to check
     * @return          a list of tags from the given tags that are not in the current image's tags
     */
    public ArrayList<Tag> getNewTags(ArrayList<Tag> tags) {
        ArrayList<Tag> newTags = new ArrayList<>();
        for (Tag tag: tags) {
            if (!imageManager.tagInTags(tag, currentImage.getTags())) {
                newTags.add(tag);
            }
        }
        return newTags;
    }

    /**
     * Removes the given list of tags from the current image's list of tags.
     * Assumes that the current image is tagged with all of the tags in the given list of tags.
     *
     * @param tags      the list of tags to remove from the current image
     */
    public void removeTagsFromImage(ArrayList<Tag> tags) {
        if (!getOldTags(tags).isEmpty()) {
            tagManager.removeImageFromTags(currentImage, getOldTags(tags));
            imageManager.removeTagsFromImage(currentImage, getOldTags(tags));
            notifyObservers(currentImage);
        }
    }

    /**
     * Returns only the tags in the given tags that are already in the current image.
     *
     * @param tags      the list of tags to check
     * @return          a list of tags from the given tags that are in the current image's tags
     */
    public ArrayList<Tag> getOldTags(ArrayList<Tag> tags) {
        ArrayList<Tag> oldTags = new ArrayList<>();
        for (Tag tag: tags) {
            if (imageManager.tagInTags(tag, currentImage.getTags())) {
                oldTags.add(tag);
            }
        }
        return oldTags;
    }

    /**
     * Creates a new Tag with the input name.
     * Called when the user wants to create a new Tag.
     *
     * @param name      the name of the tag the user wishes to create
     */
    public Tag createTag(String name) {
        return tagManager.createTag(name);
    }

    /**
     * Returns list of all tags in the program
     *
     * @return      a list of all tags in the program
     */
    public ArrayList<Tag> getAllTags(){
        return tagManager.getAllTags();
    }

    /**
     * Moves the current Image to the target directory.
     *
     * Must complete the following:
     * i) Add the image to the new directory's contents
     * ii) Remove the image from the old directory's contents
     * iii) Move the image to the new directory and update it's parentDirectory
     * iv) Update currentDirectory to new directory
     * v) Call controller.update(currentImage) to make sure the displayed image path is updated
     *
     * @param path      the path of the directory the user wishes to move the current image to
     */
    public void moveImage(String path) {
        Directory target = directoryManager.getDirectoryFromPath(path);

        //If file does not exist in the target directory
        if (!new File(target.getPath() + File.separator + currentImage.getName()).exists()) {
            //i)
            directoryManager.addContents(currentImage, target);
            //ii)
            directoryManager.removeContents(currentImage, currentDirectory);
            //iii)
            imageManager.moveOrRenameImage(currentImage, target, currentImage.getName());
            //iv)
            setCurrentDirectory(target);
            //v)
            notifyObservers(currentImage);
        }
    }

    /**
     * Reverts the current Image's name back to the specified name.
     *
     * Must complete the following:
     * i) Update the image's tags
     * ii) Rename the image and update nameHistory
     * iii) Update tag instances that are affected
     *
     * @param previousName      the previous name of the current image the user wishes to revert to
     */
    public void revertImageName(String previousName){
        ArrayList<Tag> newTags = imageManager.getRevertTags(currentImage, previousName);
        if (newTags != null) {
            updateImageTags(newTags);
        }
        for (Tag tag: newTags) {
            if (!imageManager.tagInTags(tag, tagManager.getAllTags())) {
                tagManager.addToAllTags(tag);
            }
        }
        notifyObservers(currentImage);
    }

    /**
     * Sets the currentImage instance variable to the image passed into this method
     *
     * @param image        the image to set as the current image
     */
    public void setCurrentImage(Image image){
        this.currentImage = image;
        notifyObservers(currentImage);
    }

    /**
     * Called when the user wants to display all of the images under the current directory.
     *
     * @return      a list of images under the current directory, including its sub-directories
     */
    public ArrayList<Image> getAllImages() {
        return directoryManager.getAllImages(currentDirectory);
    }

    /**
     * Called when the user wants to display only the images under the current directory.
     *
     * @return      a list of the images under the current directory, not including its sub-directories.
     */
    public ArrayList<Image> getSomeImages() {
        return directoryManager.getImages(currentDirectory);
    }

    /**
     * Returns the user's most recently selected image
     *
     * @return       the user's most recently selected image
     */
    public Image getCurrentImage(){
        return currentImage;
    }

    /**
     * Moves up a directory.
     * Used when the user clicks the "Navigate to Parent Directory" button.
     */
    public boolean goToParentDirectory() {
        if (currentDirectory.getParentDirectory() != null) {
            setCurrentDirectory(currentDirectory.getParentDirectory());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Used when the user selects a new directory.
     *
     * @param directory     the user's most recently selected image
     */
    public void setCurrentDirectory(Directory directory){
        this.currentDirectory = directory;
        this.addMissingTags();
        notifyObservers(currentDirectory);
    }

    /**
     * Helper method.
     * Finds tags in images that are not in the list of all tags and re-adds them.
     */
    private void addMissingTags() {
        if (showAllImages) {
            for (Image image: getAllImages()) {
                for (Tag tag: image.getTags()) {
                    if (!imageManager.tagInTags(tag, tagManager.getAllTags())) {
                        tagManager.addToAllTags(tag);
                    }
                }
            }
        } else {
            for (Image image: getSomeImages()) {
                for (Tag tag: image.getTags()) {
                    if (!imageManager.tagInTags(tag, tagManager.getAllTags())) {
                        tagManager.addToAllTags(tag);
                    }
                }
            }
        }
    }

    /**
     * Toggles whether or not to display all the images under the current directory (including its sub-directories),
     * or just the images under the current directory.
     */
    public void toggleShowAllImages() {
        this.showAllImages = !this.showAllImages;
    }

    /**
     * Notifies observers of changes in currentImage
     *
     * @param currentDirectory the most recently selected directory
     */
    private void notifyObservers(Directory currentDirectory){
        for (Observer o : observers){
            o.update(this, currentDirectory);
        }
    }

    /**
     * Used when the user chooses a directory and wants to display its sub-directories.
     *
     * @return      a list of sub-directories under the current directory
     */
    public ArrayList<Directory> getSubDirectories () {
        return directoryManager.getDirectories(currentDirectory);
    }

    /**
     * Opens the most recently selected Directory in user's OS's file viewer.
     */
    public void openDirectory(){
        directoryManager.openDirectory(currentDirectory.getFile());
    }

    /**
     * Returns the most recently selected directory
     *
     * @return       the user's most recently chosen directory
     */
    public Directory getCurrentDirectory() {
        return this.currentDirectory;
    }

    /**
     * Opens a file containing a log of all renaming ever done for all images.
     **/
    public void getAllRenameLogs(){
        ArrayList<String[]> logs = new ArrayList<>();

        for (Model.Image image : getAllImages()){
            logs.addAll(image.getFullNameHistory());
        }

        //Save to file
        saveLogs(logs);

        //Then open file
        //Code used from:
        //https://stackoverflow.com/questions/23176624/javafx-freeze-on-desktop-openfile-desktop-browseuri
        if (Desktop.isDesktopSupported()){ new Thread (() -> {
            try {
                Desktop.getDesktop().open(new File("renameLogs.txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        }
    }

    /**
     * Saves the logs to renameLogs.txt
     *
     * @param logs list of all logs to save
     */
    private void saveLogs(ArrayList<String[]> logs) {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("renameLogs.txt", false));
            for (String[] log: logs) {
                String line = "Image \"" + log[0]
                        + "\" has been renamed to \"" + log[1]
                        + "\" with timestamp " +log[2]
                        + "."
                        + System.lineSeparator();
                bw.write(line);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns list of all renaming done for current image
     *
     * @return   a list of all the renaming ever done for the current image
     */
    public ArrayList<String> getImageRenameLogs(){
        return imageManager.getPastNames(currentImage);
    }

    /**
     * Creates the entire data structure of Images and Directories.
     * Called when program opens.
     *
     * @param pathname      the path of the user selected root directory
     */
    public void initialize(String pathname) {
        createRootDirectory(pathname);
        setCurrentDirectory(pathname);
    }

    /**
     * Sets the current directory to the user's most recently selected directory.
     *
     * @param pathname      the path to the user's most recently selected directory
     */
    public void setCurrentDirectory(String pathname) {
        currentDirectory = directoryManager.getDirectoryFromPath(pathname);
    }

    /**
     * Creates the directory data structure from the given root folder path.
     * Only called if this is the user's first time running the program i.e. can't find/load serialized files.
     *
     * @param rootPath      the path of the user selected root directory
     */
    private void createRootDirectory(String rootPath) {
        createTreeStructure(rootPath, null);
    }

    /**
     * Helper function.
     * Actually creates the tree.
     *
     * @param currentFilePath       the path of the current file
     * @param parent                the parent directory of the current file
     */
    private void createTreeStructure(String currentFilePath, Directory parent) {
        File currentFile = new File(currentFilePath);

        // Base cases: image, or empty directory
        if (!currentFile.isDirectory() && isImage(currentFile)) {
            if (nameHasTags(currentFile.getName())) {
                ArrayList<Tag> tags = tagManager.stringToNewTags(currentFile.getName());
                Image image = imageManager.createImageWithTags(currentFilePath, parent, tags);
                tagManager.addImageToTags(image, tags);
            } else {
                imageManager.createImage(currentFilePath, parent);
            }
        } else if (currentFile.isDirectory() && currentFile.list() == null) {
            directoryManager.createDirectory(currentFilePath, parent);
        }

        //current file(dir) contains more directories; next else if rules out e.g. .app files which are
        //for some reason considered directories by java File (WHY?)
        else if (currentFile.isDirectory() &&
                !currentFile.getAbsolutePath().substring(currentFilePath.length()-4,
                        currentFilePath.length()-3).equals(".")) {
            Directory currentDirectory = directoryManager.createDirectory(currentFilePath, parent);//create current dir
            for (String childFileName: currentFile.list()) {
                String childFilePath = currentFilePath + File.separator + childFileName;
                createTreeStructure(childFilePath, currentDirectory);
            }
        }
    }

    /**
     * Helper function.
     *
     * @param name      the name of the image
     * @return          true iff name contains at least one tag
     */
    private boolean nameHasTags(String name) {
        return name.contains(" @");
    }

    /**
     * @param file      file to check
     * @return          true iff the given file is an image
     */
    private boolean isImage(File file){
        for (String ext : EXTENSIONS){
            if (file.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save the current state of the program.
     */
    public void serialize() {
        imageManager.serialize();
        directoryManager.serialize();
        tagManager.serialize();
    }

    /**
     * Load the previously saved state of the program.
     */
    public void deserialize() {
        imageManager.deserialize();
        directoryManager.deserialize();
        tagManager.deserialize();
    }

    /**
     * Returns file associated with the root directory
     *
     * @return      the file associated with the root directory
     */
    public File getRootDirectory() {
        return directoryManager.getRootDirectory().getFile();
    }

    /**
     * Create and open a directory containing all images associated with the given tag
     *
     * @param selectedTag the tag selected by a user
     * @throws IOException when attempt to open image fails
     */
    public void openRelatedImages(Tag selectedTag) throws IOException{
        String dirPath = System.getProperty("user.home")+File.separator+selectedTag.getName();
        //create the new directory
        Path dir = Paths.get(dirPath);
        if (Files.notExists(dir)) {
            Directory tagDirectory = directoryManager.createDirectory(dirPath, null);

            tagDirectory.getFile().mkdir();

            for (Image image : selectedTag.getImages()) {
                //copy image into new directory
                Path newDirPath = Paths.get(tagDirectory.getPath() + File.separator + image.getName());
                Path imgPath = Paths.get(image.getPath());
                Files.copy(imgPath, newDirPath);

            }

            directoryManager.openDirectory(tagDirectory.getFile());
        }
        File tagDir = new File(dirPath);
        for (Image image: selectedTag.getImages()){
            Path newDirPath = Paths.get(tagDir.getPath() + File.separator + image.getName());
            Path imgPath = Paths.get(image.getPath());
            if (Files.notExists(newDirPath)) {
                Files.copy(imgPath, newDirPath);
            }
        }
        directoryManager.openDirectory(tagDir);

    }


}
