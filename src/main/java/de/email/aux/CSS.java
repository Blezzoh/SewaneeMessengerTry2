package de.email.aux;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class CSS {

    public static void main(String[] args) {
        String newStyle = "-fx-alignment: #888888;";
        Text text = new Text();
        text.setStyle("-fx-background-color:#FF0099;-fx-fill: color;-fx-alignment: color");
        replaceStyle(text, newStyle);
    }

    public static String replaceStyle(Node node, String style) {
        String currStyle = node.getStyle();
        // get rid of all spaces
        currStyle = currStyle.replaceAll(" ", "");
        style = style.replaceAll(" ", "");

        System.out.println(currStyle);
        List<String> commonStyles = findCommonStyle(currStyle, style);
        for (String commonStyle : commonStyles) {
            currStyle = currStyle.replace(commonStyle, "");
        }
        if (!(currStyle.charAt(currStyle.length() - 1) == ';'))
            currStyle += ";";
        if (!(style.charAt(style.length() - 1) == ';'))
            style += ";";

        System.out.println(style + currStyle);
        return style + currStyle;

    }

    /**
     * Preconditions: curStyle and style must be valid javafx css strings
     *
     * @param curStyle the current style of a node
     * @param style    the style to add/replace for the node
     * @return a list of the common strings between curStyle and style
     */
    private static List<String> findCommonStyle(String curStyle, String style) {
        // check for valid css strings
        if (!curStyle.contains("-fx-") || !style.contains("-fx-"))
            throw new IllegalArgumentException("Not a javafx-valid css string: curStyle:" + curStyle + " styleToAdd:" + style);

        List<String> commonStyles = new ArrayList<>(10);
        int prevSemicolon = 0;
        for (int i = 0; i < curStyle.length(); i++) {
            // get next semicolon and colon
            int nextColon = curStyle.indexOf(':', i);
            int semicolon = curStyle.indexOf(';', nextColon);

            if (semicolon == -1) semicolon = curStyle.length() - 1; // last style rule doesn't end w/ a semicolon
            if (nextColon == -1) break; // no more style rules

//            System.out.println(curStyle.substring(prevSemicolon,nextColon+1));
            // or'd b/c the last css statement might not have a nextColon
            if (style.contains(curStyle.substring(prevSemicolon, nextColon + 1)) || style.contains(curStyle.substring(prevSemicolon, nextColon)))
                commonStyles.add(curStyle.substring(prevSemicolon, semicolon + 1));

            prevSemicolon = semicolon + 1;
            i = semicolon;
        }
        System.out.println(commonStyles);
        return commonStyles;
    }
}
