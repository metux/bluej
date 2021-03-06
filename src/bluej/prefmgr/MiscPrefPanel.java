/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2011,2012,2013,2016,2018  Michael Kolling and John Rosenberg
 
 This program is free software; you can redistribute it and/or 
 modify it under the terms of the GNU General Public License 
 as published by the Free Software Foundation; either version 2 
 of the License, or (at your option) any later version. 
 
 This program is distributed in the hope that it will be useful, 
 but WITHOUT ANY WARRANTY; without even the implied warranty of 
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 GNU General Public License for more details. 
 
 You should have received a copy of the GNU General Public License 
 along with this program; if not, write to the Free Software 
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. 
 
 This file is subject to the Classpath exception as provided in the  
 LICENSE.txt file that accompanied this code.
 */
package bluej.prefmgr;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.KeyStroke;

import bluej.pkgmgr.Project;
import bluej.debugger.RunOnThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import bluej.Boot;
import bluej.Config;
import bluej.utility.javafx.JavaFXUtil;
import threadchecker.OnThread;
import threadchecker.Tag;

/**
 * A PrefPanel subclass to allow the user to interactively edit
 * various miscellaneous settings
 *
 * @author  Andrew Patterson
 */
@OnThread(Tag.FXPlatform)
public class MiscPrefPanel extends VBox 
                           implements PrefPanelListener
{
    private static final String bluejJdkURL = "bluej.url.javaStdLib";
    private static final String greenfootJdkURL = "greenfoot.url.javaStdLib";

    private TextField jdkURLField;
    private CheckBox linkToLibBox;
    private CheckBox showUncheckedBox; // show "unchecked" compiler warning
    private String jdkURLPropertyName;
    private TextField playerNameField;
    private ComboBox<RunOnThread> runOnThread;
    private Node threadRunSetting;

    /**
     * Setup the UI for the dialog and event handlers for the buttons.
     */
    public MiscPrefPanel()
    {
        JavaFXUtil.addStyleClass(this, "prefmgr-pref-panel");
        
        if(Config.isGreenfoot()) {
            jdkURLPropertyName = greenfootJdkURL;
        }
        else {
            jdkURLPropertyName = bluejJdkURL;
        }
        
        getChildren().add(makeDocumentationPanel());
        
        if (Config.isGreenfoot()) {
            getChildren().add(makePlayerNamePanel());
        }
        else {
            getChildren().add(makeVMPanel());
        }
    }

    // Not called in Greenfoot
    private Node makeVMPanel()
    {
        showUncheckedBox = new CheckBox(Config.getString("prefmgr.misc.showUnchecked"));
        ObservableList<RunOnThread> runOnThreadPoss = FXCollections.observableArrayList(RunOnThread.DEFAULT, RunOnThread.FX, RunOnThread.SWING);
        runOnThread = new ComboBox<>(runOnThreadPoss);
        threadRunSetting = PrefMgrDialog.labelledItem("prefmgr.misc.runOnThread", runOnThread);
        return PrefMgrDialog.headedVBox("prefmgr.misc.vm.title", Arrays.asList(showUncheckedBox, threadRunSetting));
    }

    private Node makePlayerNamePanel()
    {
        List<Node> contents = new ArrayList<>();
        
        // get Accelerator text
        String shortcutText = " ";
        KeyStroke accelerator = Config.GREENFOOT_SET_PLAYER_NAME_SHORTCUT;
        if (accelerator != null) {
            int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                shortcutText += KeyEvent.getKeyModifiersText(modifiers);
                shortcutText += Config.isMacOS() ? "" : "+";
            }

            int keyCode = accelerator.getKeyCode();
            if (keyCode != 0) {
                shortcutText += KeyEvent.getKeyText(keyCode);
            } else {
                shortcutText += accelerator.getKeyChar();
            }
        }

        playerNameField = new TextField(PrefMgr.getPlayerName().get());
        playerNameField.setPrefColumnCount(20);
        contents.add(PrefMgrDialog.labelledItem("playername.dialog.help", playerNameField));
        
        contents.add(PrefMgrDialog.wrappedLabel(Config.getString("prefmgr.misc.playerNameNote") + shortcutText));
        
        return PrefMgrDialog.headedVBox("prefmgr.misc.playername.title", contents);
    }

    private Node makeDocumentationPanel()
    {
        List<Node> contents = new ArrayList<>();
        this.jdkURLField = new TextField();
        JavaFXUtil.addStyleClass(jdkURLField, "prefmgr-jdk-url");
        contents.add(PrefMgrDialog.labelledItem("prefmgr.misc.jdkurlpath", jdkURLField));

        linkToLibBox = new CheckBox(Config.getString("prefmgr.misc.linkToLib"));
        contents.add(linkToLibBox);

        contents.add(PrefMgrDialog.wrappedLabel(Config.getStringList("prefmgr.misc.linkToLibNoteLine").stream().collect(Collectors.joining(" "))));
        return PrefMgrDialog.headedVBox("prefmgr.misc.documentation.title", contents);
    }

    public void beginEditing(Project project)
    {
        linkToLibBox.setSelected(PrefMgr.getFlag(PrefMgr.LINK_LIB));
        jdkURLField.setText(Config.getPropString(jdkURLPropertyName));
        if(!Config.isGreenfoot()) {
            showUncheckedBox.setSelected(PrefMgr.getFlag(PrefMgr.SHOW_UNCHECKED));
            if (project == null)
            {
                threadRunSetting.setVisible(false);
                threadRunSetting.setManaged(false);
            }
            else
            {
                runOnThread.getSelectionModel().select(project.getRunOnThread());
                threadRunSetting.setVisible(true);
                threadRunSetting.setManaged(true);
            }
        }
        else
        {
            playerNameField.setText(PrefMgr.getPlayerName().get());
        }
    }

    public void revertEditing(Project project) { }

    public void commitEditing(Project project)
    {
        PrefMgr.setFlag(PrefMgr.LINK_LIB, linkToLibBox.isSelected());
        if(!Config.isGreenfoot()) {
            PrefMgr.setFlag(PrefMgr.SHOW_UNCHECKED, showUncheckedBox.isSelected());
            if (project != null)
            {
                // Important to use .name() because we overrode toString() for localized display:
                project.setRunOnThread(runOnThread.getSelectionModel().getSelectedItem());
            }
        }

        String jdkURL = jdkURLField.getText();
        Config.putPropString(jdkURLPropertyName, jdkURL);

        if (Config.isGreenfoot())
        {
            PrefMgr.getPlayerName().set(playerNameField.getText());
        }
    }
}
