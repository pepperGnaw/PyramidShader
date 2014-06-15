package edu.oregonstate.cartography.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

/**
 * http://www.javalobby.org/java/forums/t90550.html
 * Dispatch menu keyboard shortcuts from dialog to main window.
 */
public class DialogUtil {

    private static int counter = 0;

    private static ArrayList dialogs = new ArrayList();

    private static JMenuBar menuBar = null;

    public static synchronized void registerJDialog(JDialog dialog) {
        if (menuBar != null) {
            setupActions(dialog, menuBar);
        } else {
            dialogs.add(dialog);
        }
    }

    public static synchronized void setupDialogActions(JMenuBar _menuBar) {
        menuBar = _menuBar;
        if (dialogs == null) {
            return;
        }
        for (Iterator iter = dialogs.iterator(); iter.hasNext();) {
            JDialog dialog = (JDialog) iter.next();

            setupActions(dialog, menuBar);
        }
        dialogs.clear();
        dialogs = null;
    }

    private static void setupActions(JDialog dialog, JMenuBar menuBar) {
        counter = 0;

        for (int i = 0; i < menuBar.getComponentCount(); i++) {
            Component c = menuBar.getComponent(i);

            if (c instanceof MenuElement) {
                setActionsMenu(dialog, (MenuElement) c);
            }
        }
    }

    private static void setActionsMenu(JDialog dialog, MenuElement menu) {

        MenuElement[] subItems = menu.getSubElements();

        for (int i = 0; i < subItems.length; i++) {
            MenuElement c = subItems[i];

            if (c instanceof JMenuItem) {
                final JMenuItem menuItem = (JMenuItem) c;

                if (menuItem.getAccelerator() != null) {

                    String key = "hackAction" + counter++;

                    dialog.getRootPane().getInputMap(
                            JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                                    menuItem.getAccelerator(), key);

                    if (menuItem.getAction() == null) {

                        dialog.getRootPane().getActionMap().put(key,
                                new AbstractAction() {

                                    public void actionPerformed(ActionEvent e) {
                                        menuItem.doClick();

                                    }

                                });

                    } else {

                        dialog.getRootPane().getActionMap().put(key,
                                menuItem.getAction());
                    }

                    // System.out.println(key + " : "
                    // + menuItem.getActionCommand() + " : "
                    // + menuItem.getAccelerator() + " : "
                    // + menuItem.getAction());
                }

            } else if (c.getSubElements().length > 0) {
                setActionsMenu(dialog, c);
            }
        }
    }

}
