package com.lockedin.lockedin.controller.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Loads nested FXML pages into the main layout {@code #pageContainer}.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public final class PageNavigator {

    private PageNavigator() {}

    /**
     * Loads an FXML page and swaps it into {@code #pageContainer} on the anchor's scene.
     *
     * @param anchor node used to resolve the scene and classpath resource owner
     * @param fxmlPath classpath path to the FXML file
     */
    public static void loadPage(Node anchor, String fxmlPath) {
        StackPane container = findPageContainer(anchor);
        if (container != null) {
            loadInto(container, PageNavigator.class, fxmlPath);
        }
    }

    /**
     * Loads an FXML page directly into the supplied container.
     *
     * @param container target stack pane (e.g. layout {@code pageContainer})
     * @param resourceOwner class used to resolve the FXML resource
     * @param fxmlPath classpath path to the FXML file
     */
    public static void loadInto(StackPane container, Class<?> resourceOwner, String fxmlPath) {
        try {
            Pane page = FXMLLoader.load(Objects.requireNonNull(resourceOwner.getResource(fxmlPath)));
            container.getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load page: " + fxmlPath, e);
        }
    }

    /**
     * Locates the main layout {@code #pageContainer} stack pane from any node in the scene.
     *
     * @param anchor node on the active scene graph
     * @return page container, or {@code null} when not found
     */
    private static StackPane findPageContainer(Node anchor) {
        return (StackPane) anchor.getScene().lookup("#pageContainer");
    }
}
