package edu.oregonstate.cartography.gui;

import com.bric.swing.MultiThumbSlider;
import edu.oregonstate.cartography.app.ImageUtils;
import edu.oregonstate.cartography.grid.Model;
import edu.oregonstate.cartography.grid.Model.ColorRamp;
import edu.oregonstate.cartography.grid.operators.ColorizerOperator;
import static edu.oregonstate.cartography.gui.SettingsPanel.RenderSpeed.FAST;
import static edu.oregonstate.cartography.gui.SettingsPanel.RenderSpeed.REGULAR;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Bernhard Jenny, Cartography and Geovisualization Group, Oregon State
 * University
 */
public class SettingsPanel extends javax.swing.JPanel {

    protected enum RenderSpeed {

        FAST, REGULAR
    }

    // A SwingWorker for rendering the image when no progress dialog is required.
    class BackgroundRenderer extends SwingWorker<Void, Object> {

        private final BufferedImage backgroundImage;
        private final BufferedImage foregroundImage;

        protected BackgroundRenderer(BufferedImage backgroundImage,
                BufferedImage foregroundImage) {
            this.backgroundImage = backgroundImage;
            this.foregroundImage = foregroundImage;
        }

        @Override
        public Void doInBackground() {
            if (model.getGeneralizedGrid() != null) {
                model.renderBackgroundImage(backgroundImage);

                // directly render to backround image if foreground and background
                // images are the same size. This avoids a copy operation to
                // combine the two images.
                BufferedImage img = backgroundImage != foregroundImage ? foregroundImage : backgroundImage;
                model.renderForegroundImage(img);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                MainWindow mainWindow = getOwnerWindow();
                if (!isCancelled() && mainWindow != null) {
                    get();

                    BufferedImage displayImage = mainWindow.getImage();
                    Graphics g = displayImage.getGraphics();
                    g.drawImage(backgroundImage, 0, 0, null);

                    // copy foreground image into the display image if required.
                    if (backgroundImage != foregroundImage) {
                        BufferedImage img = ImageUtils.getScaledInstance(foregroundImage,
                                displayImage.getWidth(), displayImage.getHeight(),
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
                        g.drawImage(img, 0, 0, null);
                    }
                    g.dispose();
                    mainWindow.setImage(displayImage);
                }
            } catch (Exception ignore) {
            }
        }
    }

    private Model model;
    private BackgroundRenderer renderer;

    public SettingsPanel() {
        initComponents();
    }

    public void updateImage(RenderSpeed renderSpeed) {
        MainWindow mainWindow = getOwnerWindow();
        if (mainWindow == null || model == null) {
            return;
        }

        // if we are currently rendering an image, first cancel the current rendering
        if (renderer != null && !renderer.isDone()) {
            // FIXME
            //renderer.cancel(false);
        }

        // block the event dispatching thread until the BackgroundRenderer worker thread
        // is done. This is to avoid that two BackgroundRenderer threads write to 
        // the same image.
        try {
            if (renderer != null) {
                renderer.get();
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // create destination image
        // note: it is not possible to reuse this image. Flickering artifacts
        // will appear otherwise.
        BufferedImage backgroundImage = model.createDestinationImage(1);
        if (backgroundImage == null) {
            return;
        }
        int foregroundScale = (renderSpeed == FAST ? 1 : 2);
        BufferedImage foregroundImage = model.createDestinationImage(foregroundScale);

        // create a new renderer and run it
        renderer = new BackgroundRenderer(backgroundImage, foregroundImage);
        renderer.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        colorPopupMenu = new javax.swing.JPopupMenu();
        colorButtonGroup = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        javax.swing.JPanel visualizationContainer = new TransparentMacPanel();
        visualizationPanel = new TransparentMacPanel();
        grayRadioButton = new javax.swing.JRadioButton();
        hypsometricShadingRadioButton = new javax.swing.JRadioButton();
        expositionRadioButton = new javax.swing.JRadioButton();
        solidColorRadioButton = new javax.swing.JRadioButton();
        verticalExaggerationLabel = new javax.swing.JLabel();
        verticalExaggerationSlider = new javax.swing.JSlider();
        colorsCardsPanel = new TransparentMacPanel();
        emptyPanel = new TransparentMacPanel();
        colorGradientCardPanel = new TransparentMacPanel();
        javax.swing.JLabel colorInfoLabel = new javax.swing.JLabel();
        colorGradientSlider = new com.bric.swing.GradientSlider();
        colorPresetsButton = new edu.oregonstate.cartography.gui.MenuToggleButton();
        solidColorCardPanel = new TransparentMacPanel();
        solidColorButton = new edu.oregonstate.cartography.gui.ColorButton();
        hypsometricRadioButton = new javax.swing.JRadioButton();
        javax.swing.JPanel generalizationContainer = new TransparentMacPanel();
        generalizationPanel = new TransparentMacPanel();
        javax.swing.JLabel generalizationDetailLabel = new javax.swing.JLabel();
        generalizationDetailSlider = new javax.swing.JSlider();
        javax.swing.JLabel generalizationMaxLabel = new javax.swing.JLabel();
        generalizationMaxLevelsSpinner = new javax.swing.JSpinner();
        generalizationDetaiIndicator = new javax.swing.JLabel();
        generalizationInfoLabel = new javax.swing.JLabel();
        javax.swing.JPanel illuminationContainer = new TransparentMacPanel();
        illuminationPanel = new TransparentMacPanel();
        javax.swing.JLabel zeLabel = new javax.swing.JLabel();
        azimuthSlider = new javax.swing.JSlider();
        zenithSlider = new javax.swing.JSlider();
        javax.swing.JLabel azLabel = new javax.swing.JLabel();
        contoursPanel = new TransparentMacPanel();
        illuminatedContoursPanel = new TransparentMacPanel();
        contoursComboBox = new javax.swing.JComboBox();
        contoursCardPanel = new TransparentMacPanel();
        contoursEmptyPanel = new TransparentMacPanel();
        contoursSettingsPanel = new TransparentMacPanel();
        contoursShadowLineWidthSlider = new javax.swing.JSlider();
        javax.swing.JLabel contoursMinLineWidthLabel = new javax.swing.JLabel();
        contoursIntervalTextBox = new javax.swing.JFormattedTextField();
        contoursGradientSlider = new javax.swing.JSlider();
        javax.swing.JLabel contoursShadwoLineWidthSlider = new javax.swing.JLabel();
        contoursIlluminatedLineWidthSlider = new javax.swing.JSlider();
        javax.swing.JLabel contoursIntervalLabel = new javax.swing.JLabel();
        javax.swing.JLabel contoursIlluminatedLineWidthLabel = new javax.swing.JLabel();
        contoursMinLineWidthSlider = new javax.swing.JSlider();
        contoursGradientLabel = new javax.swing.JLabel();

        colorPopupMenu.setLightWeightPopupEnabled(false);

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 2, 2));

        visualizationContainer.setLayout(new java.awt.GridBagLayout());

        visualizationPanel.setLayout(new java.awt.GridBagLayout());

        colorButtonGroup.add(grayRadioButton);
        grayRadioButton.setSelected(true);
        grayRadioButton.setText("Gray Shading");
        grayRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grayRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        visualizationPanel.add(grayRadioButton, gridBagConstraints);

        colorButtonGroup.add(hypsometricShadingRadioButton);
        hypsometricShadingRadioButton.setText("Hypsometric Color with Shading");
        hypsometricShadingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypsometricShadingRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        visualizationPanel.add(hypsometricShadingRadioButton, gridBagConstraints);

        colorButtonGroup.add(expositionRadioButton);
        expositionRadioButton.setText("Exposition Color");
        expositionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expositionRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        visualizationPanel.add(expositionRadioButton, gridBagConstraints);

        colorButtonGroup.add(solidColorRadioButton);
        solidColorRadioButton.setText("Continuous Tone (For Illuminated Contours)");
        solidColorRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solidColorRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        visualizationPanel.add(solidColorRadioButton, gridBagConstraints);

        verticalExaggerationLabel.setText("Vertical Exaggeration");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        visualizationPanel.add(verticalExaggerationLabel, gridBagConstraints);

        verticalExaggerationSlider.setMajorTickSpacing(5);
        verticalExaggerationSlider.setMaximum(50);
        verticalExaggerationSlider.setMinorTickSpacing(1);
        verticalExaggerationSlider.setPaintLabels(true);
        verticalExaggerationSlider.setPaintTicks(true);
        verticalExaggerationSlider.setSnapToTicks(true);
        verticalExaggerationSlider.setToolTipText("Vertical exaggeration to the grid applied for shading calculation.");
        verticalExaggerationSlider.setValue(10);
        {
            java.util.Hashtable labels = verticalExaggerationSlider.createStandardLabels(10);
            java.util.Enumeration e = labels.elements();
            DecimalFormat df = new DecimalFormat("#.#");
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    String v = df.format(Integer.parseInt(label.getText()) / 10f);
                    label.setText(v + "\u00d7");
                }
            }
            verticalExaggerationSlider.setLabelTable(labels);
        }
        verticalExaggerationSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                verticalExaggerationSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        visualizationPanel.add(verticalExaggerationSlider, gridBagConstraints);

        colorsCardsPanel.setLayout(new java.awt.CardLayout());
        colorsCardsPanel.add(emptyPanel, "emptyCard");

        colorGradientCardPanel.setLayout(new java.awt.GridBagLayout());

        colorInfoLabel.setFont(colorInfoLabel.getFont().deriveFont(colorInfoLabel.getFont().getSize()-2f));
        colorInfoLabel.setText("Click on slider to add color; double-click on triangles to change.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        colorGradientCardPanel.add(colorInfoLabel, gridBagConstraints);

        colorGradientSlider.setPreferredSize(new java.awt.Dimension(360, 30));
        colorGradientSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                colorGradientSliderPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        colorGradientCardPanel.add(colorGradientSlider, gridBagConstraints);

        colorPresetsButton.setText("Color Presets");
        colorPresetsButton.setPopupMenu(colorPopupMenu);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        colorGradientCardPanel.add(colorPresetsButton, gridBagConstraints);

        colorsCardsPanel.add(colorGradientCardPanel, "colorGradientCard");

        solidColorCardPanel.setLayout(new java.awt.GridBagLayout());

        solidColorButton.setText("Background Color");
        solidColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solidColorButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        solidColorCardPanel.add(solidColorButton, gridBagConstraints);

        colorsCardsPanel.add(solidColorCardPanel, "solidColorCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        visualizationPanel.add(colorsCardsPanel, gridBagConstraints);

        colorButtonGroup.add(hypsometricRadioButton);
        hypsometricRadioButton.setText("Hypsometric Color");
        hypsometricRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hypsometricRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        visualizationPanel.add(hypsometricRadioButton, gridBagConstraints);

        visualizationContainer.add(visualizationPanel, new java.awt.GridBagConstraints());

        tabbedPane.addTab("Visualization", visualizationContainer);

        generalizationPanel.setLayout(new java.awt.GridBagLayout());

        generalizationDetailLabel.setText("Details Removal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        generalizationPanel.add(generalizationDetailLabel, gridBagConstraints);

        generalizationDetailSlider.setMajorTickSpacing(10);
        generalizationDetailSlider.setMaximum(10);
        generalizationDetailSlider.setMinimum(-10);
        generalizationDetailSlider.setMinorTickSpacing(1);
        generalizationDetailSlider.setPaintTicks(true);
        generalizationDetailSlider.setSnapToTicks(true);
        generalizationDetailSlider.setValue(-5);
        generalizationDetailSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                generalizationDetailSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        generalizationPanel.add(generalizationDetailSlider, gridBagConstraints);

        generalizationMaxLabel.setText("Landforms Removal");
        generalizationMaxLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        generalizationPanel.add(generalizationMaxLabel, gridBagConstraints);

        generalizationMaxLevelsSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 0, 10, 1));
        generalizationMaxLevelsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                generalizationMaxLevelsSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        generalizationPanel.add(generalizationMaxLevelsSpinner, gridBagConstraints);

        generalizationDetaiIndicator.setText("100%");
        generalizationDetaiIndicator.setPreferredSize(new java.awt.Dimension(40, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        generalizationPanel.add(generalizationDetaiIndicator, gridBagConstraints);

        generalizationInfoLabel.setText("No Generalization");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 0, 0);
        generalizationPanel.add(generalizationInfoLabel, gridBagConstraints);

        generalizationContainer.add(generalizationPanel);

        tabbedPane.addTab("Generalization", generalizationContainer);

        illuminationPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        illuminationPanel.setLayout(new java.awt.GridBagLayout());

        zeLabel.setText("Zenith (Vertical Direction)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        illuminationPanel.add(zeLabel, gridBagConstraints);

        azimuthSlider.setMajorTickSpacing(45);
        azimuthSlider.setMaximum(360);
        azimuthSlider.setMinorTickSpacing(15);
        azimuthSlider.setPaintLabels(true);
        azimuthSlider.setPaintTicks(true);
        azimuthSlider.setValue(45);
        azimuthSlider.setPreferredSize(new java.awt.Dimension(380, 52));
        {
            java.util.Hashtable labels = azimuthSlider.createStandardLabels(45);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    int angle = Integer.parseInt(label.getText());
                    switch (angle) {
                        case 0:
                        case 360:
                        label.setText("N");
                        break;
                        case 45:
                        label.setText("NE");
                        break;
                        case 90:
                        label.setText("E");
                        break;
                        case 135:
                        label.setText("SE");
                        break;
                        case 180:
                        label.setText("S");
                        break;
                        case 225:
                        label.setText("SW");
                        break;
                        case 270:
                        label.setText("W");
                        break;
                        case 315:
                        label.setText("NW");
                        break;
                    }
                }
            }
            azimuthSlider.setLabelTable(labels);
        }
        azimuthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                azimuthSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        illuminationPanel.add(azimuthSlider, gridBagConstraints);

        zenithSlider.setMajorTickSpacing(15);
        zenithSlider.setMaximum(90);
        zenithSlider.setMinorTickSpacing(5);
        zenithSlider.setPaintLabels(true);
        zenithSlider.setPaintTicks(true);
        zenithSlider.setValue(45);
        {
            java.util.Hashtable labels = zenithSlider.createStandardLabels(15);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    label.setText(label.getText() + "\u00b0");
                }
            }
            zenithSlider.setLabelTable(labels);
        }
        zenithSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zenithSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        illuminationPanel.add(zenithSlider, gridBagConstraints);

        azLabel.setText("Azimuth (Horizontal Direction)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        illuminationPanel.add(azLabel, gridBagConstraints);

        illuminationContainer.add(illuminationPanel);

        tabbedPane.addTab("Illumination", illuminationContainer);

        illuminatedContoursPanel.setLayout(new java.awt.GridBagLayout());

        contoursComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No Contours", "Illuminated & Shadowed Contours", "Shadowed Contours" }));
        contoursComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                contoursComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        illuminatedContoursPanel.add(contoursComboBox, gridBagConstraints);

        contoursCardPanel.setLayout(new java.awt.CardLayout());
        contoursCardPanel.add(contoursEmptyPanel, "emptyCard");

        contoursSettingsPanel.setLayout(new java.awt.GridBagLayout());

        contoursShadowLineWidthSlider.setMajorTickSpacing(10);
        contoursShadowLineWidthSlider.setMaximum(50);
        contoursShadowLineWidthSlider.setMinorTickSpacing(5);
        contoursShadowLineWidthSlider.setPaintLabels(true);
        contoursShadowLineWidthSlider.setPaintTicks(true);
        contoursShadowLineWidthSlider.setToolTipText("Line widths are relative to grid cell size.");
        contoursShadowLineWidthSlider.setValue(0);
        {
            java.util.Hashtable labels = contoursShadowLineWidthSlider.createStandardLabels(10);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    int sliderValue = Integer.parseInt(label.getText());
                    label.setText(Integer.toString(sliderValue / 10));
                }
            }
            contoursShadowLineWidthSlider.setLabelTable(labels);
        }
        contoursShadowLineWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contoursShadowLineWidthSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        contoursSettingsPanel.add(contoursShadowLineWidthSlider, gridBagConstraints);

        contoursMinLineWidthLabel.setText("Minimum Line Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        contoursSettingsPanel.add(contoursMinLineWidthLabel, gridBagConstraints);

        contoursIntervalTextBox.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter()));
        contoursIntervalTextBox.setToolTipText("Contour interval");
        contoursIntervalTextBox.setPreferredSize(new java.awt.Dimension(120, 28));
        contoursIntervalTextBox.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                contoursIntervalTextBoxPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contoursSettingsPanel.add(contoursIntervalTextBox, gridBagConstraints);

        contoursGradientSlider.setMajorTickSpacing(10);
        contoursGradientSlider.setMaximum(50);
        contoursGradientSlider.setMinorTickSpacing(5);
        contoursGradientSlider.setPaintLabels(true);
        contoursGradientSlider.setPaintTicks(true);
        contoursGradientSlider.setToolTipText("A gradient between black and white is created within this angle.");
        contoursGradientSlider.setValue(0);
        {
            java.util.Hashtable labels = contoursGradientSlider.createStandardLabels(10);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    label.setText(label.getText() + "\u00b0");
                }
            }
            contoursGradientSlider.setLabelTable(labels);
        }
        contoursGradientSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contoursGradientSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        contoursSettingsPanel.add(contoursGradientSlider, gridBagConstraints);

        contoursShadwoLineWidthSlider.setText("Shadowed Line Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        contoursSettingsPanel.add(contoursShadwoLineWidthSlider, gridBagConstraints);

        contoursIlluminatedLineWidthSlider.setMajorTickSpacing(10);
        contoursIlluminatedLineWidthSlider.setMaximum(50);
        contoursIlluminatedLineWidthSlider.setMinorTickSpacing(5);
        contoursIlluminatedLineWidthSlider.setPaintLabels(true);
        contoursIlluminatedLineWidthSlider.setPaintTicks(true);
        contoursIlluminatedLineWidthSlider.setToolTipText("Line widths are relative to grid cell size.");
        contoursIlluminatedLineWidthSlider.setValue(0);
        {
            java.util.Hashtable labels = contoursIlluminatedLineWidthSlider.createStandardLabels(10);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    int sliderValue = Integer.parseInt(label.getText());
                    label.setText(Integer.toString(sliderValue / 10));
                }
            }
            contoursIlluminatedLineWidthSlider.setLabelTable(labels);
        }
        contoursIlluminatedLineWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contoursIlluminatedLineWidthSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        contoursSettingsPanel.add(contoursIlluminatedLineWidthSlider, gridBagConstraints);

        contoursIntervalLabel.setText("Interval");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        contoursSettingsPanel.add(contoursIntervalLabel, gridBagConstraints);

        contoursIlluminatedLineWidthLabel.setText("Illuminated Line Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        contoursSettingsPanel.add(contoursIlluminatedLineWidthLabel, gridBagConstraints);

        contoursMinLineWidthSlider.setMajorTickSpacing(10);
        contoursMinLineWidthSlider.setMaximum(50);
        contoursMinLineWidthSlider.setMinorTickSpacing(5);
        contoursMinLineWidthSlider.setPaintLabels(true);
        contoursMinLineWidthSlider.setPaintTicks(true);
        contoursMinLineWidthSlider.setToolTipText("Line widths are relative to grid cell size.");
        contoursMinLineWidthSlider.setValue(0);
        {
            java.util.Hashtable labels = contoursMinLineWidthSlider.createStandardLabels(10);
            java.util.Enumeration e = labels.elements();
            while(e.hasMoreElements()) {
                javax.swing.JComponent comp = (javax.swing.JComponent)e.nextElement();
                if (comp instanceof javax.swing.JLabel) {
                    javax.swing.JLabel label = (javax.swing.JLabel)(comp);
                    int sliderValue = Integer.parseInt(label.getText());
                    label.setText(Integer.toString(sliderValue / 10));
                }
            }
            contoursMinLineWidthSlider.setLabelTable(labels);
        }
        contoursMinLineWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                contoursMinLineWidthSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        contoursSettingsPanel.add(contoursMinLineWidthSlider, gridBagConstraints);

        contoursGradientLabel.setText("Gradient Angle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        contoursSettingsPanel.add(contoursGradientLabel, gridBagConstraints);

        contoursCardPanel.add(contoursSettingsPanel, "contoursSettingsCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        illuminatedContoursPanel.add(contoursCardPanel, gridBagConstraints);

        contoursPanel.add(illuminatedContoursPanel);

        tabbedPane.addTab("Contours", contoursPanel);

        add(tabbedPane);
    }// </editor-fold>//GEN-END:initComponents

    public void setModel(Model m) {
        this.model = m;

        generalizationMaxLevelsSpinner.setValue(m.generalizationMaxLevels);
        generalizationDetailSlider.setValue((int) Math.round(m.getGeneralizationDetails() * 10));

        azimuthSlider.setValue(m.azimuth);
        zenithSlider.setValue(m.zenith);

        contoursIlluminatedLineWidthSlider.setValue((int) Math.round(m.contoursIlluminatedWidth * 10));
        contoursShadowLineWidthSlider.setValue((int) Math.round(m.contoursShadowWidth * 10));
        contoursMinLineWidthSlider.setValue((int) Math.round(m.contoursMinWidth * 10));
        contoursGradientSlider.setValue(m.contoursGradientAngle);
        contoursIntervalTextBox.setValue(m.contoursInterval);

        verticalExaggerationSlider.setValue(Math.round(m.shadingVerticalExaggeration * 10f));
        colorGradientSlider.setValues(m.colorRamp.colorPositions, m.colorRamp.colors);
        solidColorButton.setColor(m.solidColor);
        updateGeneralizationInfoLabelVisiblity();

        for (ColorRamp cr : model.predefinedColorRamps) {
            JMenuItem colorMenuItem = new JMenuItem(cr.name);
            colorMenuItem.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    JMenuItem menuItem = (JMenuItem) evt.getSource();
                    model.selectColorRamp(menuItem.getText());
                    colorGradientSlider.setValues(model.colorRamp.colorPositions, model.colorRamp.colors);
                    updateImage(REGULAR);
                }
            });
            colorPopupMenu.add(colorMenuItem);
        }
        updateImage(REGULAR);
    }

    private MainWindow getOwnerWindow() {
        JRootPane rootPane = getRootPane();
        if (rootPane != null) {
            JDialog dialog = (JDialog) (rootPane.getParent());
            return (MainWindow) (dialog.getOwner());
        }
        return null;
    }

    private void azimuthSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_azimuthSliderStateChanged
        model.azimuth = azimuthSlider.getValue();
        updateImage(azimuthSlider.getValueIsAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_azimuthSliderStateChanged

    private void updateGeneralizationInfoLabelVisiblity() {
        int maxLevel = (Integer) (generalizationMaxLevelsSpinner.getValue());
        double details = generalizationDetailSlider.getValue() / 10d;
        boolean showLabel = maxLevel <= 0 || details <= -1d;
        generalizationInfoLabel.setVisible(showLabel);
    }
    private void generalizationDetailSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_generalizationDetailSliderStateChanged
        //don't take action until user has finished adjusting
        if (generalizationDetailSlider.getValueIsAdjusting() == false) {
            model.setGeneralizationDetails(generalizationDetailSlider.getValue() / 10d);
            //compute the summed pyramids using the original grid
            model.updateGeneralizedGrid();
            //shade, color, and redraw
            updateImage(generalizationDetailSlider.getValueIsAdjusting() ? FAST : REGULAR);
        }

        // write value to GUI
        double detailVal = generalizationDetailSlider.getValue() / 10d;
        detailVal = (detailVal + 1) / 2;
        String valString = new DecimalFormat("#.#%").format(detailVal);
        generalizationDetaiIndicator.setText(valString);

        updateGeneralizationInfoLabelVisiblity();
    }//GEN-LAST:event_generalizationDetailSliderStateChanged

    private void zenithSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zenithSliderStateChanged
        model.zenith = zenithSlider.getValue();
        updateImage(zenithSlider.getValueIsAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_zenithSliderStateChanged

    private void generalizationMaxLevelsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_generalizationMaxLevelsSpinnerStateChanged
        model.generalizationMaxLevels = (Integer) (generalizationMaxLevelsSpinner.getValue());
        //compute the summed pyramids using the original grid
        model.updateGeneralizedGrid();
        //shade, color, and redraw
        updateImage(REGULAR);
        updateGeneralizationInfoLabelVisiblity();
    }//GEN-LAST:event_generalizationMaxLevelsSpinnerStateChanged

    private void verticalExaggerationSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_verticalExaggerationSliderStateChanged
        model.shadingVerticalExaggeration = verticalExaggerationSlider.getValue() / 10f;
        updateImage(verticalExaggerationSlider.getValueIsAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_verticalExaggerationSliderStateChanged

    private void colorGradientSliderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_colorGradientSliderPropertyChange
        String propName = evt.getPropertyName();
        //Check if the changed property is either value or color for one of the thumbs
        if (MultiThumbSlider.VALUES_PROPERTY.equals(propName) || MultiThumbSlider.ADJUST_PROPERTY.equals(propName)) {
            model.colorRamp.colors = colorGradientSlider.getColors();
            model.colorRamp.colorPositions = colorGradientSlider.getThumbPositions();
            updateImage(colorGradientSlider.isValueAdjusting() ? FAST : REGULAR);
        }
    }//GEN-LAST:event_colorGradientSliderPropertyChange

    private void expositionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expositionRadioButtonActionPerformed
        model.backgroundVisualization = ColorizerOperator.ColorVisualization.EXPOSITION;
        updateImage(REGULAR);
        ((CardLayout) (colorsCardsPanel.getLayout())).show(colorsCardsPanel, "colorGradientCard");
        verticalExaggerationLabel.setEnabled(true);
        verticalExaggerationSlider.setEnabled(true);
    }//GEN-LAST:event_expositionRadioButtonActionPerformed

    private void grayRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grayRadioButtonActionPerformed
        model.backgroundVisualization = ColorizerOperator.ColorVisualization.GRAY_SHADING;
        updateImage(REGULAR);
        ((CardLayout) (colorsCardsPanel.getLayout())).show(colorsCardsPanel, "emptyCard");
        verticalExaggerationLabel.setEnabled(true);
        verticalExaggerationSlider.setEnabled(true);
    }//GEN-LAST:event_grayRadioButtonActionPerformed

    private void hypsometricShadingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypsometricShadingRadioButtonActionPerformed
        model.backgroundVisualization = ColorizerOperator.ColorVisualization.HYPSOMETRIC_SHADING;
        updateImage(REGULAR);
        ((CardLayout) (colorsCardsPanel.getLayout())).show(colorsCardsPanel, "colorGradientCard");
        verticalExaggerationLabel.setEnabled(true);
        verticalExaggerationSlider.setEnabled(true);
    }//GEN-LAST:event_hypsometricShadingRadioButtonActionPerformed

    private void contoursIntervalTextBoxPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_contoursIntervalTextBoxPropertyChange
        Object contourTextVal = contoursIntervalTextBox.getValue();
        if (contourTextVal != null) {
            double d = ((Number) contourTextVal).doubleValue();
            model.contoursInterval = d;
            updateImage(REGULAR);
        }
    }//GEN-LAST:event_contoursIntervalTextBoxPropertyChange

    /**
     * Adjust the value of the slider with the minimum contours line width. This
     * value is always smaller than the other two width values.
     *
     * @param movingSlider The slider that is currently moved.
     */
    private void adjustContoursMinLineWidthSlider(JSlider movingSlider) {
        // temporarily remove event listener to avoid triggering a render event
        ChangeListener listener = contoursMinLineWidthSlider.getChangeListeners()[0];
        contoursMinLineWidthSlider.removeChangeListener(listener);
        int width = Math.min(movingSlider.getValue(), (int)Math.round(model.contoursMinWidth * 10));
        contoursMinLineWidthSlider.setValue(width);
        // add the event lister back to the slider
        contoursMinLineWidthSlider.addChangeListener(listener);

        // also update the model
        model.contoursMinWidth = width / 10d;
    }

    /**
     * Returns true if any of the sliders for adjusting contour line widths is
     * currently moving.
     *
     * @return
     */
    private boolean isContoursLineWidthSliderAdjusting() {
        return contoursIlluminatedLineWidthSlider.getValueIsAdjusting()
                | contoursShadowLineWidthSlider.getValueIsAdjusting()
                | contoursMinLineWidthSlider.getValueIsAdjusting();
    }
    private void contoursIlluminatedLineWidthSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contoursIlluminatedLineWidthSliderStateChanged
        model.contoursIlluminatedWidth = contoursIlluminatedLineWidthSlider.getValue() / 10.f;
        adjustContoursMinLineWidthSlider(contoursIlluminatedLineWidthSlider);
        updateImage(isContoursLineWidthSliderAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_contoursIlluminatedLineWidthSliderStateChanged

    private void contoursShadowLineWidthSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contoursShadowLineWidthSliderStateChanged
        model.contoursShadowWidth = contoursShadowLineWidthSlider.getValue() / 10.f;
        adjustContoursMinLineWidthSlider(contoursShadowLineWidthSlider);
        updateImage(isContoursLineWidthSliderAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_contoursShadowLineWidthSliderStateChanged

    private void contoursMinLineWidthSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contoursMinLineWidthSliderStateChanged
        model.contoursMinWidth = contoursMinLineWidthSlider.getValue() / 10.;

        // remove event listener to avoid triggering a rendering event
        ChangeListener listener = contoursIlluminatedLineWidthSlider.getChangeListeners()[0];
        contoursIlluminatedLineWidthSlider.removeChangeListener(listener);
        int illuminatedWidth = Math.max(contoursIlluminatedLineWidthSlider.getValue(), contoursMinLineWidthSlider.getValue());
        contoursIlluminatedLineWidthSlider.setValue(illuminatedWidth);
        contoursIlluminatedLineWidthSlider.addChangeListener(listener);

        // remove event listener to avoid triggering a rendering event
        listener = contoursShadowLineWidthSlider.getChangeListeners()[0];
        contoursShadowLineWidthSlider.removeChangeListener(listener);
        int shadowdWidth = Math.max(contoursShadowLineWidthSlider.getValue(), contoursMinLineWidthSlider.getValue());
        contoursShadowLineWidthSlider.setValue(shadowdWidth);
        contoursShadowLineWidthSlider.addChangeListener(listener);

        // update model
        model.contoursIlluminatedWidth = illuminatedWidth / 10d;
        model.contoursShadowWidth = shadowdWidth / 10d;

        updateImage(isContoursLineWidthSliderAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_contoursMinLineWidthSliderStateChanged

    private void contoursGradientSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_contoursGradientSliderStateChanged
        model.contoursGradientAngle = contoursGradientSlider.getValue();
        updateImage(contoursGradientSlider.getValueIsAdjusting() ? FAST : REGULAR);
    }//GEN-LAST:event_contoursGradientSliderStateChanged

    private void solidColorRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solidColorRadioButtonActionPerformed
        model.backgroundVisualization = ColorizerOperator.ColorVisualization.CONTINUOUS;
        updateImage(REGULAR);
        ((CardLayout) (colorsCardsPanel.getLayout())).show(colorsCardsPanel, "solidColorCard");
        verticalExaggerationLabel.setEnabled(false);
        verticalExaggerationSlider.setEnabled(false);
    }//GEN-LAST:event_solidColorRadioButtonActionPerformed

    private void solidColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solidColorButtonActionPerformed
        model.solidColor = solidColorButton.getColor();
        updateImage(REGULAR);
    }//GEN-LAST:event_solidColorButtonActionPerformed

    private void contoursComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_contoursComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            switch (contoursComboBox.getSelectedIndex()) {
                case 0:
                    model.foregroundVisualization = Model.ForegroundVisualization.NONE;
                    ((CardLayout) (contoursCardPanel.getLayout())).show(contoursCardPanel, "emptyCard");
                    break;
                case 1:
                    model.foregroundVisualization = Model.ForegroundVisualization.ILLUMINATED_CONTOURS;
                    ((CardLayout) (contoursCardPanel.getLayout())).show(contoursCardPanel, "contoursSettingsCard");
                    break;
                case 2:
                    model.foregroundVisualization = Model.ForegroundVisualization.SHADED_CONTOURS;
                    ((CardLayout) (contoursCardPanel.getLayout())).show(contoursCardPanel, "contoursSettingsCard");
                    break;
            }
            updateImage(REGULAR);
        }

    }//GEN-LAST:event_contoursComboBoxItemStateChanged

    private void hypsometricRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hypsometricRadioButtonActionPerformed
        model.backgroundVisualization = ColorizerOperator.ColorVisualization.HYPSOMETRIC;
        updateImage(REGULAR);
        ((CardLayout) (colorsCardsPanel.getLayout())).show(colorsCardsPanel, "colorGradientCard");
        verticalExaggerationLabel.setEnabled(false);
        verticalExaggerationSlider.setEnabled(false);
    }//GEN-LAST:event_hypsometricRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider azimuthSlider;
    private javax.swing.ButtonGroup colorButtonGroup;
    private javax.swing.JPanel colorGradientCardPanel;
    private com.bric.swing.GradientSlider colorGradientSlider;
    private javax.swing.JPopupMenu colorPopupMenu;
    private edu.oregonstate.cartography.gui.MenuToggleButton colorPresetsButton;
    private javax.swing.JPanel colorsCardsPanel;
    private javax.swing.JPanel contoursCardPanel;
    private javax.swing.JComboBox contoursComboBox;
    private javax.swing.JPanel contoursEmptyPanel;
    private javax.swing.JLabel contoursGradientLabel;
    private javax.swing.JSlider contoursGradientSlider;
    private javax.swing.JSlider contoursIlluminatedLineWidthSlider;
    private javax.swing.JFormattedTextField contoursIntervalTextBox;
    private javax.swing.JSlider contoursMinLineWidthSlider;
    private javax.swing.JPanel contoursPanel;
    private javax.swing.JPanel contoursSettingsPanel;
    private javax.swing.JSlider contoursShadowLineWidthSlider;
    private javax.swing.JPanel emptyPanel;
    private javax.swing.JRadioButton expositionRadioButton;
    private javax.swing.JLabel generalizationDetaiIndicator;
    private javax.swing.JSlider generalizationDetailSlider;
    private javax.swing.JLabel generalizationInfoLabel;
    private javax.swing.JSpinner generalizationMaxLevelsSpinner;
    private javax.swing.JPanel generalizationPanel;
    private javax.swing.JRadioButton grayRadioButton;
    private javax.swing.JRadioButton hypsometricRadioButton;
    private javax.swing.JRadioButton hypsometricShadingRadioButton;
    private javax.swing.JPanel illuminatedContoursPanel;
    private javax.swing.JPanel illuminationPanel;
    private edu.oregonstate.cartography.gui.ColorButton solidColorButton;
    private javax.swing.JPanel solidColorCardPanel;
    private javax.swing.JRadioButton solidColorRadioButton;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JLabel verticalExaggerationLabel;
    private javax.swing.JSlider verticalExaggerationSlider;
    private javax.swing.JPanel visualizationPanel;
    private javax.swing.JSlider zenithSlider;
    // End of variables declaration//GEN-END:variables

}
