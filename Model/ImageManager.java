package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Takes method calls from GeneralManager and manipulates the image objects accordingly
 */
public class ImageManager implements Serializable {

    /**
     * List of all images in the root directory.
     */
    private ArrayList<Model.Image> allImages = new ArrayList<>();

    /**
     * Initializes an instance of ImageManager
     */
    public ImageManager(){}

    /**
     * Updates and renames the image with the given tags.
     *
     * @param currentImage the current image
     * @param tags list of tags to update
     */
    public void updateImageTags(Model.Image currentImage, ArrayList<Tag> tags) {
        currentImage.updateTags(tags);
        StringBuilder newName = new StringBuilder();

        newName.append(currentImage.getExtensionlessOriginalName());
        if (tags.size() != 0) {
            newName.append(" ");
            newName.append(currentImage.tagToString());
        }
        newName.append(currentImage.getExtension());

        moveOrRenameImage(currentImage, currentImage.getParentDirectory(), newName.toString());
    }

    /**
     * Adds the given list of tags to the given image's list of tags.
     * Assumes that the given image is not tagged with any of the tags in the given list of tags.
     *
     * @param image     the image to add the given tags to
     * @param tags      the list of tags to add to the given image
     */
    public void addTagsToImage(Image image, ArrayList<Tag> tags) {
        image.addTags(tags);
        image.renameImage();
    }

    /**
     * Removes the given list of tags from the given image's list of tags.
     * Assumes that the given image is tagged with all of the tags in the given list of tags.
     *
     * @param image     the image to remove the given tags from
     * @param tags      the list of tags to remove from the given image
     */
    public void removeTagsFromImage(Image image, ArrayList<Tag> tags) {
        image.removeTags(tags);
        image.renameImage();
    }

    /**
     * Helper function.
     * Returns true iff thisTag is in tags.
     *
     * @param thisTag the tag to check
     * @param tags the list of tags to see if thisTag is in it
     * @return true iff thisTag in tags
     */
    boolean tagInTags(Tag thisTag, ArrayList<Tag> tags) {
        for (Tag tag: tags) {
            if (thisTag.getName().equals(tag.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all renaming ever done for the current image.
     * Formatted in an array list: [startingName, name1, name2, ...].
     *
     * @param currentImage the current image
     * @return list of names for the given image
     */
    public ArrayList<String> getPastNames(Model.Image currentImage) {
        if (currentImage != null) {
            return currentImage.getNameHistory();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Moves the current image to the target directory.
     * For move, target directory is a different directory than the one current image is located in.
     * For rename, target directory is the same directory that the current image is located it.
     *
     * @param currentImage the current image
     * @param target the directory to be moved to
     * @param newName new name for the image
     */
    void moveOrRenameImage(Model.Image currentImage, Directory target, String newName){

        String oldParent = currentImage.getParentDirectory().getPath();
        String oldName = currentImage.getName();

        Path src = Paths.get(currentImage.getPath());
        Path dest = Paths.get(target.getPath() + File.separator + newName);

        try {
            Files.move(src, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Change internal references for file name
        if (target.getPath().equals(oldParent)) {
            //rename
            String[] nameHistoryEntry = {oldName, newName, currentImage.getCurrentDate()};
            currentImage.addNameHistoryEntry(nameHistoryEntry);
            currentImage.setParentDirectory(target);
            currentImage.setFile(target.getPath() + File.separator + newName);
        } else {
            //move
            currentImage.setParentDirectory(target);
            currentImage.setFile(target.getPath() + File.separator + newName);
        }

    }

    /**
     * Creates an instance of Image under the current directory.
     * Only used in initializing program.
     * Since system ensures no duplicate names in same directory, no need to check.
     *
     * @param path the path of the image
     * @param parentDirectory the directory that the image is from
     */
    public Image createImage(String path, Directory parentDirectory) {
        Model.Image image = new Model.Image(path, parentDirectory);
        allImages.add(image);
        image.addToParentDirectory();
        return image;
    }

    /**
     * Creates an instance of Image under the current directory with the pre-existing tags.
     * Only used in initializing program.
     * Since system ensures no duplicate names in same directory, no need to check.
     *
     * @param path the path of the image
     * @param parentDirectory the directory that the image is from
     */
    public Image createImageWithTags(String path, Directory parentDirectory, ArrayList<Tag> tags) {
        Model.Image image = new Model.Image(path, parentDirectory, tags);
        allImages.add(image);
        image.addToParentDirectory();
        return image;
    }

    /**
     * Returns the new name of the image, based on most recently added tags
     *
     * @param previousName the previous name of this image
     * @return the new name of this image
     */
    ArrayList<Tag> getRevertTags(Image image, String previousName) {
        return image.getRevertTags(previousName);
    }

    /**
     * Saves the current state of all images.
     */
    void serialize() {
        try {
            FileOutputStream fos = new FileOutputStream("images.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allImages);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the past state of all images.
     */
    void deserialize() {
        try {
            FileInputStream fis = new FileInputStream("images.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            allImages = (ArrayList<Image>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
