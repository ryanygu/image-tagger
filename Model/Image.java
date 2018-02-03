package Model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.io.File;

/**
 * Represents an image in the user's computer
 * Each image has current name, past names, associated tags, directory which it belongs to
 */
public class Image implements Serializable {

    /**
     * Stores the name history of this Image with format [oldName, newName, timestamp].
     */
    private ArrayList<String[]> nameHistory = new ArrayList<>();

    /**
     * Stores the tag history of this Image.
     */
    private ArrayList<Tag[]> tagHistory = new ArrayList<>();

    /**
     * Directory in which this image is found
     */
    private Directory parentDirectory;

    /**
     * Stores this Image's file representation.
     */
    private File file;

    /**
     * Name of this image in the user's computer
     */
    private String name;

    /**
     * Creates an instance of Image.
     *
     * @param path              the path of this image
     * @param parentDirectory   the parent directory of this image
     */
    public Image(String path, Directory parentDirectory) {
        this.file = new File(path);
        this.name = file.getName();
        this.setParentDirectory(parentDirectory);
    }

    /**
     * Used for testing purposes.
     */
    public Image(){}

    /**
     * Creates an instance of Image.
     *
     * @param path              the path of this image
     * @param parentDirectory   the parent directory of this image
     */
    public Image(String path, Directory parentDirectory, ArrayList<Tag> tags) {
        this.file = new File(path);
        this.setParentDirectory(parentDirectory);
        Tag[] tagHistoryEntry = new Tag[tags.size()];
        for (int i = 0; i < tags.size(); i++) {
            tagHistoryEntry[i] = tags.get(i);
        }
        this.tagHistory.add(tagHistoryEntry);
    }

    /**
     * Returns the name of the image
     *
     * @return      the current name of this Image file
     */
    public String getName() {
        return this.file.getName();
    }

    /**
     * Returns the original name of this image
     *
     * @return      the original name of this Image
     */
    private String getOriginalName() {
        if (this.nameHistory.size() == 0) {
            return this.file.getName();
        } else {
            return this.nameHistory.get(0)[0];
        }
    }

    /**
     * Adds this image to its parent directory's contents.
     */
    void addToParentDirectory(){
        getParentDirectory().addContents(this);
    }

    /**
     * Stores the directory that this Image is located under.
     *
     * @return       this image's parent directory
     */
    public Directory getParentDirectory() {
        return this.parentDirectory;
    }

    /**
     * Sets this Image's parent directory.
     *
     * @param parentDirectory       this image's parent directory
     */
    void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    /**
     * Returns list of tags currently associated with the image
     *
     * @return       the current list of tags associated with this image
     */
    public ArrayList<Tag> getTags() {

        if (this.tagHistory.size() == 0) {
            return new ArrayList<>();
        } else {
            Tag[] tags = this.tagHistory.get(this.tagHistory.size() - 1);
            return new ArrayList<>(Arrays.asList(tags));
        }
    }

    /**
     * Updates this Image's tags with the given tags.
     *
     * @param tags      the updated list of tags of this image
     */
    void updateTags(ArrayList<Tag> tags) {
        this.tagHistory.add(tags.toArray(new Tag[tags.size()]));
    }

    /**
     * Adds the given tags to this image's current list of tags.
     *
     * @param tags      the list of tags to add to this image
     */
    void addTags(ArrayList<Tag> tags) {
        ArrayList<Tag> tagHistoryEntry = new ArrayList<>();
        tagHistoryEntry.addAll(this.getTags());
        tagHistoryEntry.addAll(tags);
        this.tagHistory.add(tagHistoryEntry.toArray(new Tag[tagHistoryEntry.size()]));
    }

    /**
     * Removes the given tags from this image's current list of tags.
     *
     * @param tags      the list of tags to remove from this image
     */
    void removeTags(ArrayList<Tag> tags) {
        ArrayList<Tag> tagHistoryEntry = new ArrayList<>();
        tagHistoryEntry.addAll(this.getTags());
        tagHistoryEntry.removeAll(tags);
        this.tagHistory.add(tagHistoryEntry.toArray(new Tag[tagHistoryEntry.size()]));
    }

    /**
     * Returns a log of all names ever associated with this Image.
     * Example as follows: [startingName, name1, name2, ...].
     *
     * @return      a list of all names ever associated with this image
     */
    ArrayList<String> getNameHistory() {
        ArrayList<String> nameHistory = new ArrayList<>();
        if (this.nameHistory.size() == 0) {
            nameHistory.add(getName());
        } else {
            nameHistory.add(getOriginalName());
            for (String[] nameHistoryEntry: this.nameHistory) {
                nameHistory.add(nameHistoryEntry[1]);
            }
        }
        return nameHistory;
    }

    /**
     * Example as follows: [[startingName, name1, time], [name1, name2, time], ...].
     *
     * @return      a log of all names ever associated with this image
     */
    ArrayList<String[]> getFullNameHistory() {
        return this.nameHistory;
    }

    /**
     * Returns string representation of file path to this image
     *
     * @return       the path of this image
     */
    public String getPath() {
        return this.file.getPath();
    }

    /**
     * Returns the current system time in yyyy/MM/dd HH:mm:ss format
     *
     * @return   the current system time in a yyyy/MM/dd HH:mm:ss format
     */
    String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * Example as follows: [tag1, tag2], turns into "@tag1 @tag2".
     *
     * @return      a string representation of the current tags associated with this image
     */
    String tagToString() {
        if (this.getTags().size() == 0) {
            return "";
        }
        ArrayList<Tag> tags = this.getTags();
        StringBuilder tagString = new StringBuilder();
        for (Tag tag: tags) {
            tagString.append("@");
            tagString.append(tag.getName());
            tagString.append(" ");
        }
        tagString.deleteCharAt(tagString.length() - 1);
        return tagString.toString();
    }

    /**
     * Example as follows: ".extension".
     *
     * @return      the file extension of this image
     */
    String getExtension() {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * Returns file object which the image is associated with
     *
     * @return       the file this image is associated with
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Sets a file object to associate the image with
     *
     * @param path       the file this image is associated with
     */
    void setFile(String path) {
        this.file = new File(path);
    }

    /**
     * Returns the name of the image, without the extension
     *
     * @return       the name of the image without the extension
     */
    String getExtensionlessOriginalName() {
        String name = this.getOriginalName();
        int extension = name.lastIndexOf(".");
        int tag = name.indexOf(" @");
        if (tag == -1) {
            return name.substring(0, extension);
        } else {
            return name.substring(0, tag);
        }
    }

    /**
     * Adds the new entry to this image's name history.
     *
     * @param entry     the entry to be added
     */
    void addNameHistoryEntry(String[] entry) {
        this.nameHistory.add(entry);
    }

    /**
     * Renames the image based on most recent set of tags
     */
    void renameImage() {

        String oldName = this.getName();
        String newName = this.getNewName();

        Path src = Paths.get(this.getPath());
        Path dest = Paths.get(this.getParentDirectory().getPath() + File.separator + newName);

        try {
            Files.move(src, dest);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String[] nameHistoryEntry = {oldName, newName, this.getCurrentDate()};
        this.addNameHistoryEntry(nameHistoryEntry);
        this.setFile(this.getParentDirectory().getPath() + File.separator + newName);
    }

    /**
     * Returns the new name of the image, based on most recently added tags
     *
     * @return new name of image, based on most recently added tags
     */
    //correct entry if: #tags in previousname == #tags in taghistory entry, and all the tags are the same.
    ArrayList<Tag> getRevertTags(String previousName) {
        for (Tag[] tagHistoryEntry: this.tagHistory) {//[t1],[t1t2],[t1t2t3]

            if (tagHistoryEntry.length != stringToTagNames(previousName).size()) {
                continue;
            }

            boolean valid = true;
            for (Tag tag: tagHistoryEntry) {

                if (!stringToTagNames(previousName).contains(tag.getName())) {
                    valid = false;
                    break;
                }

            }
            if (!valid) {
                continue;
            }
            return new ArrayList<>(Arrays.asList(tagHistoryEntry));

        }
        return new ArrayList<>();
    }

    /**
     * Takes a string, and returns list of tag names derived from splitting the string by @ character
     *
     * @param name the string to be split
     * @return the result of splitting the string
     */
    private ArrayList<String> stringToTagNames(String name) {
        ArrayList<String> tagNames = new ArrayList<>();
        String[] tags = name.split("[@.]"); // "name @tag1 @tag2.png" -> [name /tag1 /tag2/png]

        for (int i = 1; i < tags.length - 1; i++) {
            tagNames.add(tags[i].trim());
        }

        return tagNames;
    }

    /**
     * Returns newest name of image, based on set of tags
     *
     * @return new name of image, based on set of tags
     */
    private String getNewName() {
        StringBuilder newName = new StringBuilder();

        newName.append(this.getExtensionlessOriginalName());
        if (this.getTags().size() != 0) {
            newName.append(" ");
            newName.append(this.tagToString());
        }
        newName.append(this.getExtension());
        return newName.toString();
    }

}
