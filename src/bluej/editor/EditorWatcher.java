/*
 This file is part of the BlueJ program. 
 Copyright (C) 1999-2009,2012,2013,2014,2016,2017,2018  Michael Kolling and John Rosenberg
 
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
package bluej.editor;

import java.util.Collection;
import java.util.List;

import bluej.collect.DiagnosticWithShown;
import bluej.collect.StrideEditReason;
import bluej.compiler.CompileReason;
import bluej.compiler.CompileType;
import bluej.editor.stride.FrameCatalogue;
import bluej.stride.generic.Frame;

/**
 * Interface between the editor and the rest of BlueJ
 * The editor uses this class
 * @author Michael Kolling
 */
public interface EditorWatcher
{
    //key for storing the value of the expand/collapse of the naviview
    public final static String NAVIVIEW_EXPANDED_PROPERTY="naviviewExpandedProperty";
    /**
     * Called by Editor when a file is changed
     */
    void modificationEvent(Editor editor);

    /**
     * Called by Editor when a file is saved
     */
    void saveEvent(Editor editor);

    /**
     * Called by Editor when it is closed
     */
    void closeEvent(Editor editor);

    /**
     * Called by Editor to set/clear a breakpoint
     * @param lineNo the line number of the breakpoint
     * @param set    whether the breakpoint is set (true) or cleared
     * @return             An error message or null if okay.
     */
    String breakpointToggleEvent(int lineNo, boolean set);

    /**
     * Called by Editor when documentation is to be compiled
     */
    void generateDoc();  
    
    /**
     * Sets a property
     */
    void setProperty(String key, String value);
    
    /**
     * Gets a property
     */
    String getProperty(String key);

    /**
     * Schedule compilation due to reload or modification
     * @param immediate  True if compilation should be performed immediately; false if compilation should be
     *                   postponed until the user VM is idle
     * @param reason    Reason for compilation, used for data recording purposes
     * @param type      The type of compilation, used to decide whether to keep or discard the generated .class files.
     */
    public void scheduleCompilation(boolean immediate, CompileReason reason, CompileType type);

    void clearAllBreakpoints();

    /**
     * Record that the editor was opened
     */
    void recordOpen();

    /**
     * Record that the editor was closed
     */
    void recordClose();

    /**
     * Record that a given quick fix was selected
     * @param errorIdentifier Integer id of the error
     * @param fixIndex The index in the quick fix list, corresponding to the list passed earlier to recordShowErrorMessage for this error id
     */
    void recordFix(int errorIdentifier, int fixIndex);

    /**
     * Notifies watcher whether we are showing the interface (docs) or not
     */
    void showingInterface(boolean showingInterface);

    /**
     * Shows the preferences pane, and makes the given pane index (i.e. given tab index
     * in the preferences) the active showing tab.  0 is general, 1 is key bindings, and so on.
     * If in doubt, pass 0.
     */
    void showPreferences(int paneIndex);
}
