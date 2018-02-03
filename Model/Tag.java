package Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class representation of the user created Tags
 */

public class Tag implements Serializable {

    /**
     * The name of this Tag which has the format "tagName".
     */
    private String name;

    private ArrayList<Image> images;

    /**
     * Creates the Tag object using the given name and initial image. Also instantiated the list containing all the
     * images that the tag is associated with.
     *
     * @param name      the name of the tag
     */
    public Tag(String name) {
        this.name = name;
        this.setImages(new ArrayList<>());
    }

    /**
     * @return the name of this tag
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add an image to the list of associated images
     *
     * @param newImage the Image to be associated with this Tag
     */
    void addImage(Image newImage) {
        this.getImages().add(newImage);
    }

    /**
     * Delete the given image from the list of images of this tag
     *
     * @param deleteImage the image to delete from the images list of this tag
     */
    void removeImage(Image deleteImage) {
        this.getImages().remove(deleteImage);
    }


    /**
     * The list containing all Images associated with this Tag.
     *
     * @return list of all images associated with this Tag
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    /**
     * Sets the images associated with the tag to the argument passed
     *
     * @param images the list of images to be associated with this tag
     */
    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }
}
