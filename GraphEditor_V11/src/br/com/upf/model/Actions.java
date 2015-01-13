/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the classEditor.
 */
package br.com.upf.model;

import br.com.upf.beans.Attribute;
import br.com.upf.beans.ClassElement;
import br.com.upf.beans.ClassModel;
import br.com.upf.beans.DiagramOfStateModel;
import br.com.upf.util.DefaultFileFilter;
import br.com.upf.view.ClassView;
import br.com.upf.view.GraphEditor;
import br.com.upf.view.dialog.AttributeEdition;
import br.com.upf.view.internalframe.ClassEditor;
import br.com.upf.view.internalframe.StateEditor;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxSvgCanvas;
import com.mxgraph.io.mxCodec;
import com.mxgraph.io.mxGdCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.util.png.mxPngTextDecoder;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.w3c.dom.Document;

/**
 *
 * @author GiordaniAntonio
 */
public class Actions {

    /**
     *
     */
    public static final String IMAGE_PATH = "/br/com/upf/images/";

    /**
     *
     * @param e
     * @return
     */
    public static final GraphEditor getEditor(ActionEvent e) {

        if (e.getSource() instanceof Component) {
            Component component = (Component) e.getSource();

            while (component != null
                    && !(component instanceof GraphEditor)) {
                component = component.getParent();
            }

            return (GraphEditor) component;
        }
        return null;

    }

    /**
     *
     * @param e
     * @return
     */
    public static final ClassEditor getClassEditor(ActionEvent e) {

        GraphEditor editor;
        if (e.getSource() instanceof Component) {
            Component component = (Component) e.getSource();

            while (component != null
                    && !(component instanceof GraphEditor)) {
                component = component.getParent();
            }

            editor = (GraphEditor) component;
            return editor.getClassEditor();
        }
        return null;
    }

    /**
     *
     */
    @SuppressWarnings("serial")
    public static class NewClassEditorAction extends AbstractAction {

        /**
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            GraphEditor editor = getEditor(e);
            if (editor != null) {
                editor.newClassEditor("Novo Diagrama de Classes", true);
            } else {
                JOptionPane.showMessageDialog(editor, "Editor não definido");
            }
        }
    }

    /**
     *
     */
    @SuppressWarnings("serial")
    public static class OpenAction extends AbstractAction {

        protected GraphEditor graphEditor;

        protected ClassEditor classEditor;

        /**
         *
         */
        protected String lastDir;

        /**
         *
         */
        protected void resetEditor(ClassEditor classEditor) {
            classEditor.setModified(false);
            classEditor.getUndoManager().clear();
            classEditor.getGraphComponent().zoomAndCenter();
        }

        /**
         * Reads XML+PNG format.
         */
        protected void openXmlPng(ClassEditor classEditor, File file)
                throws IOException {
            Map<String, String> text = mxPngTextDecoder
                    .decodeCompressedText(new FileInputStream(file));

            if (text != null) {
                String value = text.get("mxGraphModel");

                if (value != null) {
                    Document document = mxXmlUtils.parseXml(URLDecoder.decode(
                            value, "UTF-8"));
                    mxCodec codec = new mxCodec(document);
                    codec.decode(document.getDocumentElement(), classEditor.getGraphComponent().getGraph().getModel());
                    classEditor.setCurrentFile(file);
                    resetEditor(classEditor);

                    return;
                }
            }

            JOptionPane.showMessageDialog(classEditor,
                    mxResources.get("imageContainsNoDiagramData"));
        }

        /**
         * @throws IOException
         *
         */
        protected void openGD(ClassEditor classEditor, File file,
                String gdText) {
            mxGraph graph = classEditor.getGraph();

            // Replaces file extension with .mxe
            String filename = file.getName();
            filename = filename.substring(0, filename.length() - 4) + ".mxe";

            if (new File(filename).exists()
                    && JOptionPane.showConfirmDialog(classEditor,
                            mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                return;
            }

            ((mxGraphModel) graph.getModel()).clear();
            mxGdCodec.decode(gdText, graph);
            classEditor.getGraphComponent().zoomAndCenter();
            classEditor.setCurrentFile(new File(lastDir + "/" + filename));
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            classEditor = getClassEditor(e);
            graphEditor = getEditor(e);

            if (classEditor == null) {
                graphEditor.newClassEditor("New Diagram", false);
                classEditor = graphEditor.getClassEditor();
            }

            if (classEditor != null) {
                if (!classEditor.isModified()
                        || JOptionPane.showConfirmDialog(classEditor,
                                mxResources.get("loseChanges")) == JOptionPane.YES_OPTION) {
                    mxGraph graph = classEditor.getGraph();

                    if (graph != null) {
                        String wd = (lastDir != null) ? lastDir : System
                                .getProperty("user.dir");

                        JFileChooser fc = new JFileChooser(wd);

                        // Adds file filter for supported file format
                        DefaultFileFilter defaultFilter = new DefaultFileFilter(
                                ".mxe", mxResources.get("allSupportedFormats")
                                + " (.mxe, .png, .vdx)") {

                                    public boolean accept(File file) {
                                        String lcase = file.getName().toLowerCase();

                                        return super.accept(file)
                                        || lcase.endsWith(".png")
                                        || lcase.endsWith(".vdx");
                                    }
                                };
                        fc.addChoosableFileFilter(defaultFilter);

                        fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                                "mxGraph Editor " + mxResources.get("file")
                                + " (.mxe)"));
                        fc.addChoosableFileFilter(new DefaultFileFilter(".png",
                                "PNG+XML  " + mxResources.get("file")
                                + " (.png)"));

                        // Adds file filter for VDX import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".vdx",
                                "XML Drawing  " + mxResources.get("file")
                                + " (.vdx)"));

                        // Adds file filter for GD import
                        fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
                                "Graph Drawing  " + mxResources.get("file")
                                + " (.txt)"));

                        fc.setFileFilter(defaultFilter);

                        int rc = fc.showDialog(null,
                                mxResources.get("openFile"));

                        if (rc == JFileChooser.APPROVE_OPTION) {
                            lastDir = fc.getSelectedFile().getParent();

                            try {
                                if (fc.getSelectedFile().getAbsolutePath()
                                        .toLowerCase().endsWith(".png")) {
                                    openXmlPng(classEditor, fc.getSelectedFile());
                                } else if (fc.getSelectedFile().getAbsolutePath()
                                        .toLowerCase().endsWith(".txt")) {
                                    openGD(classEditor, fc.getSelectedFile(),
                                            mxUtils.readFile(fc
                                                    .getSelectedFile()
                                                    .getAbsolutePath()));
                                } else {
                                    Document document = mxXmlUtils
                                            .parseXml(mxUtils.readFile(fc
                                                            .getSelectedFile()
                                                            .getAbsolutePath()));

                                    mxCodec codec = new mxCodec(document);
                                    codec.decode(
                                            document.getDocumentElement(),
                                            graph.getModel());
                                    classEditor.setCurrentFile(fc
                                            .getSelectedFile());

                                    resetEditor(classEditor);
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(classEditor.getGraphComponent(),
                                        ex.toString(),
                                        mxResources.get("error"),
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            graphEditor.classEditorView();
                        } else {
                            graphEditor.resetGraphEditor();
                        }
                    }
                }

            }
        }
    }

    /**
     *
     */
    @SuppressWarnings("serial")
    public static class SaveAction extends AbstractAction {

        /**
         *
         */
        protected boolean showDialog;

        /**
         *
         */
        protected String lastDir = null;

        /**
         *
         */
        public SaveAction(boolean showDialog) {
            this.showDialog = showDialog;
        }

        /**
         * Saves XML+PNG format.
         */
        protected void saveXmlPng(ClassEditor classEditor, String filename,
                Color bg) throws IOException {
            mxGraphComponent graphComponent = classEditor.getGraphComponent();
            mxGraph graph = graphComponent.getGraph();

            // Creates the image for the PNG file
            BufferedImage image = mxCellRenderer.createBufferedImage(graph,
                    null, 1, bg, graphComponent.isAntiAlias(), null,
                    graphComponent.getCanvas());

            // Creates the URL-encoded XML data
            mxCodec codec = new mxCodec();
            String xml = URLEncoder.encode(
                    mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
            mxPngEncodeParam param = mxPngEncodeParam
                    .getDefaultEncodeParam(image);
            param.setCompressedText(new String[]{"mxGraphModel", xml});

            // Saves as a PNG file
            FileOutputStream outputStream = new FileOutputStream(new File(
                    filename));
            try {
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream,
                        param);

                if (image != null) {
                    encoder.encode(image);

                    classEditor.setModified(false);
                    classEditor.setCurrentFile(new File(filename));
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            mxResources.get("noImageData"));
                }
            } finally {
                outputStream.close();
            }
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent e) {
            ClassEditor editor = getClassEditor(e);

            if (editor != null) {
                mxGraphComponent graphComponent = editor.getGraphComponent();
                mxGraph graph = graphComponent.getGraph();
                FileFilter selectedFilter = null;
                DefaultFileFilter xmlPngFilter = new DefaultFileFilter(".png",
                        "PNG+XML " + mxResources.get("file") + " (.png)");
                FileFilter vmlFileFilter = new DefaultFileFilter(".html",
                        "VML " + mxResources.get("file") + " (.html)");
                String filename = null;
                boolean dialogShown = false;

                if (showDialog || editor.getCurrentFile() == null) {
                    String wd;

                    if (lastDir != null) {
                        wd = lastDir;
                    } else if (editor.getCurrentFile() != null) {
                        wd = editor.getCurrentFile().getParent();
                    } else {
                        wd = System.getProperty("user.dir");
                    }

                    JFileChooser fc = new JFileChooser(wd);

                    // Adds the default file format
                    FileFilter defaultFilter = xmlPngFilter;
                    fc.addChoosableFileFilter(defaultFilter);

                    // Adds special vector graphics formats and HTML
                    fc.addChoosableFileFilter(new DefaultFileFilter(".mxe",
                            "mxGraph Editor " + mxResources.get("file")
                            + " (.mxe)"));
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".txt",
//                            "Graph Drawing " + mxResources.get("file")
//                            + " (.txt)"));
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".svg",
//                            "SVG " + mxResources.get("file") + " (.svg)"));
//                    fc.addChoosableFileFilter(vmlFileFilter);
//                    fc.addChoosableFileFilter(new DefaultFileFilter(".html",
//                            "HTML " + mxResources.get("file") + " (.html)"));

                    // Adds a filter for each supported image format
                    Object[] imageFormats = ImageIO.getReaderFormatNames();

                    // Finds all distinct extensions
                    HashSet<String> formats = new HashSet<String>();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString().toLowerCase();
                        formats.add(ext);
                    }

                    imageFormats = formats.toArray();

                    for (int i = 0; i < imageFormats.length; i++) {
                        String ext = imageFormats[i].toString();
                        fc.addChoosableFileFilter(new DefaultFileFilter("."
                                + ext, ext.toUpperCase() + " "
                                + mxResources.get("file") + " (." + ext + ")"));
                    }

                    // Adds filter that accepts all supported image formats
                    fc.addChoosableFileFilter(new DefaultFileFilter.ImageFileFilter(
                            mxResources.get("allImages")));
                    fc.setFileFilter(defaultFilter);
                    int rc = fc.showDialog(null, mxResources.get("save"));
                    dialogShown = true;

                    if (rc != JFileChooser.APPROVE_OPTION) {
                        return;
                    } else {
                        lastDir = fc.getSelectedFile().getParent();
                    }

                    filename = fc.getSelectedFile().getAbsolutePath();
                    selectedFilter = fc.getFileFilter();

                    if (selectedFilter instanceof DefaultFileFilter) {
                        String ext = ((DefaultFileFilter) selectedFilter)
                                .getExtension();

                        if (!filename.toLowerCase().endsWith(ext)) {
                            filename += ext;
                        }
                    }

                    if (new File(filename).exists()
                            && JOptionPane.showConfirmDialog(graphComponent,
                                    mxResources.get("overwriteExistingFile")) != JOptionPane.YES_OPTION) {
                        return;
                    }
                } else {
                    filename = editor.getCurrentFile().getAbsolutePath();
                }

                try {
                    String ext = filename
                            .substring(filename.lastIndexOf('.') + 1);

                    if (ext.equalsIgnoreCase("svg")) {
                        mxSvgCanvas canvas = (mxSvgCanvas) mxCellRenderer
                                .drawCells(graph, null, 1, null,
                                        new mxCellRenderer.CanvasFactory() {
                                            public mxICanvas createCanvas(
                                                    int width, int height) {
                                                        mxSvgCanvas canvas = new mxSvgCanvas(
                                                                mxDomUtils.createSvgDocument(
                                                                        width, height));
                                                        canvas.setEmbedded(true);

                                                        return canvas;
                                                    }

                                        });

                        mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()),
                                filename);
                    } else if (selectedFilter == vmlFileFilter) {
                        mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
                                .createVmlDocument(graph, null, 1, null, null)
                                .getDocumentElement()), filename);
                    } else if (ext.equalsIgnoreCase("html")) {
                        mxUtils.writeFile(mxXmlUtils.getXml(mxCellRenderer
                                .createHtmlDocument(graph, null, 1, null, null)
                                .getDocumentElement()), filename);
                    } else if (ext.equalsIgnoreCase("mxe")
                            || ext.equalsIgnoreCase("xml")) {
                        mxCodec codec = new mxCodec();
                        String xml = mxXmlUtils.getXml(codec.encode(graph
                                .getModel()));

                        mxUtils.writeFile(xml, filename);

                        editor.setModified(false);
                        editor.setCurrentFile(new File(filename));
                    } else if (ext.equalsIgnoreCase("txt")) {
                        String content = mxGdCodec.encode(graph);

                        mxUtils.writeFile(content, filename);
                    } else {
                        Color bg = null;

//                        if ((!ext.equalsIgnoreCase("gif") && !ext
//                                .equalsIgnoreCase("png"))
//                                || JOptionPane.showConfirmDialog(
//                                        graphComponent, mxResources
//                                        .get("transparentBackground")) != JOptionPane.YES_OPTION) {
//                            bg = graphComponent.getBackground();
//                        }  
                        if (selectedFilter == xmlPngFilter
                                || (editor.getCurrentFile() != null
                                && ext.equalsIgnoreCase("png") && !dialogShown)) {
                            saveXmlPng(editor, filename, bg);
                        } else {
                            BufferedImage image = mxCellRenderer
                                    .createBufferedImage(graph, null, 1, bg,
                                            graphComponent.isAntiAlias(), null,
                                            graphComponent.getCanvas());

                            if (image != null) {
                                ImageIO.write(image, ext, new File(filename));
                            } else {
                                JOptionPane.showMessageDialog(graphComponent,
                                        mxResources.get("noImageData"));
                            }
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(graphComponent,
                            ex.toString(), mxResources.get("error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     *
     */
    public static class PrintAction extends AbstractAction {

        private static final long serialVersionUID = 3297525706854096481L;

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e
                        .getSource();
                PrinterJob pj = PrinterJob.getPrinterJob();

                if (pj.printDialog()) {
                    PageFormat pf = graphComponent.getPageFormat();
                    Paper paper = new Paper();
                    double margin = 36;
                    paper.setImageableArea(margin, margin, paper.getWidth()
                            - margin * 2, paper.getHeight() - margin * 2);
                    pf.setPaper(paper);
                    pj.setPrintable(graphComponent, pf);

                    try {
                        pj.print();
                    } catch (PrinterException e2) {
                        System.out.println(e2);
                    }
                }
            }
        }
    }

    /**
     *
     */
    public static class PageSetupAction extends AbstractAction {

        private static final long serialVersionUID = 4707261984072016413L;

        /**
         *
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof mxGraphComponent) {
                mxGraphComponent graphComponent = (mxGraphComponent) e
                        .getSource();
                PrinterJob pj = PrinterJob.getPrinterJob();
                PageFormat format = pj.pageDialog(graphComponent
                        .getPageFormat());

                if (format != null) {
                    graphComponent.setPageFormat(format);
                    graphComponent.zoomAndCenter();
                }
            }
        }
    }

    /**
     *
     */
    public static class ExitAction extends AbstractAction {

        private static final long serialVersionUID = -6260491787527361767L;

        /**
         *
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            GraphEditor editor = getEditor(e);
            if (editor != null) {
                //editor.setVisible(false);
                System.exit(1);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Cria um Novo StateEditor - Editor de Diagrama de Estados
     */
    public static class CreateDiagramOfStatesAction extends AbstractAction {

        private static final long serialVersionUID = -5905131075768370733L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public CreateDiagramOfStatesAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (classModel.validate()) {
                        if (!diagramOfStateModel.isInitialized()) {
                            diagramOfStateModel.setInitialized(true);
                            graphEditor.newStateEditor(classElement);
                            graphEditor.stateEditorView(classElement.getStateEditor());
                        } else {
                            graphEditor.stateEditorView(classElement.getStateEditor());
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidClassModel"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    }

    /**
     * Inicia processo para criação do Código SQL - Table
     */
    public static class CreateTableCodeAction extends AbstractAction {

        private static final long serialVersionUID = -7685935918681175303L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public CreateTableCodeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (classModel.isContainsAttributes()) {
                        if (classModel.isCheckName()) {
                            graphEditor.parametersTableView(classElement);
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("informClassName"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("informClassAttributes"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Inicia processo para criação do Código SQL - Triggers
     */
    public static class CreateTriggersCodeAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        /**
         *
         */
        private String yesUrlIcon = "/br/com/upf/images/yes.png";

        /**
         *
         */
        private String noUrlIcon = "/br/com/upf/images/no.png";

        /**
         *
         */
        private Icon yesIcon = new ImageIcon(CreateTriggersCodeAction.class.getResource(yesUrlIcon));

        /**
         *
         */
        private Icon noIcon = new ImageIcon(CreateTriggersCodeAction.class.getResource(noUrlIcon));

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public CreateTriggersCodeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (diagramOfStateModel.isInitialized()) {
                        if (diagramOfStateModel.validate()) {
                            if (classModel.isInitializedDictionaryOfStates()) {
                                if (classModel.validateDictionaryOfStates()) {
                                    classModel.generateTriggerCode();
                                    graphEditor.triggersCodeView(classElement);
                                } else {
                                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDictionary"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                                    graphEditor.dictionaryOfStatesValidationView(classElement);
                                }
                            } else {
                                JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDictionaryBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            graphEditor.diagramOfStatesValidationView(classElement);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (workArea.getSelectedFrame() instanceof StateEditor) {
                stateEditor = (StateEditor) workArea.getSelectedFrame();
                classElement = stateEditor.getClassElement();
                classModel = classElement.getClassModel();
                diagramOfStateModel = classModel.getDiagramOfStateModel();
                if (diagramOfStateModel.isInitialized()) {
                    if (diagramOfStateModel.validate()) {
                        if (classModel.isInitializedDictionaryOfStates()) {
                            if (classModel.validateDictionaryOfStates()) {
                                classModel.generateTriggerCode();
                                graphEditor.triggersCodeView(classElement);
                            } else {
                                JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDictionary"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                                graphEditor.dictionaryOfStatesValidationView(classElement);
                            }
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDictionaryBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        graphEditor.diagramOfStatesValidationView(classElement);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Inicia processo para criação do Código SQL - Triggers
     */
    public static class CreateDictionaryOfStatesAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public CreateDictionaryOfStatesAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (diagramOfStateModel.isInitialized()) {
                        if (diagramOfStateModel.validate()) {
                            if (classModel.getMonitorableAttributes().isEmpty()) {
                                graphEditor.parametersTriggersView(classElement);
                            } else {
                                if (classModel.isMethodElaborationCustom()) {
                                    graphEditor.dictionaryOfStatesMethodCustomView(classElement);
                                } else {
                                    graphEditor.dictionaryOfStatesMethodStandartView(classElement);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            graphEditor.diagramOfStatesValidationView(classElement);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (workArea.getSelectedFrame() instanceof StateEditor) {
                stateEditor = (StateEditor) workArea.getSelectedFrame();
                classElement = stateEditor.getClassElement();
                classModel = classElement.getClassModel();
                diagramOfStateModel = classModel.getDiagramOfStateModel();
                if (diagramOfStateModel.isInitialized()) {
                    if (diagramOfStateModel.validate()) {
                        if (classModel.getMonitorableAttributes().isEmpty()) {
                            graphEditor.parametersTriggersView(classElement);
                        } else {
                            if (classModel.isMethodElaborationCustom()) {
                                graphEditor.dictionaryOfStatesMethodCustomView(classElement);
                            } else {
                                graphEditor.dictionaryOfStatesMethodStandartView(classElement);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                        graphEditor.diagramOfStatesValidationView(classElement);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo contendo Código SQL - Table
     */
    public static class ViewTableCodeAction extends AbstractAction {

        private static final long serialVersionUID = 6440133021195979435L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public ViewTableCodeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (classModel.isGeneratedTableCode()) {
                        if (classModel.isUpdatedTableCode()) {
                            graphEditor.tableCodeView(classElement);
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("outdatedCodeOgTable"), mxResources.get("warning"), JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("tableHasNotYetBeenGenerated"), mxResources.get("warning"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    }

    /**
     * Exibe Internal Frame - Diagrama de Estados Editor
     */
    public static class ViewDiagramOfStatesAction extends AbstractAction {

        private static final long serialVersionUID = -8931354550272562981L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public ViewDiagramOfStatesAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (diagramOfStateModel.isInitialized()) {
                        graphEditor.stateEditorView(classElement.getStateEditor());
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    }

    /**
     * Exibe Internal Frame - Diagrama de Classes Editor
     */
    public static class ViewDiagramOfClassAction extends AbstractAction {

        private static final long serialVersionUID = -7157338421818640813L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public ViewDiagramOfClassAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof StateEditor) {
                stateEditor = (StateEditor) workArea.getSelectedFrame();
                graphEditor.classEditorView();
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    }

    /**
     * Exibe Dialogo contendo Código SQL - Triggers
     */
    public static class ViewTriggersCodeAction extends AbstractAction {

        private static final long serialVersionUID = -868129787052403123L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public ViewTriggersCodeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (classModel.isGeneratedTriggersCode()) {
                        if (classModel.isUpdatedTriggersCode()) {
                            graphEditor.triggersCodeView(classElement);
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("outdatedCodeTriggers"), mxResources.get("warning"), JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("triggersHasNotYetBeenGenerated"), mxResources.get("warning"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class DiagramOfStatesValidationAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        /**
         *
         */
        private String yesUrlIcon = "/br/com/upf/images/yes.png";

        /**
         *
         */
        private String noUrlIcon = "/br/com/upf/images/no.png";

        /**
         *
         */
        private Icon yesIcon = new ImageIcon(DiagramOfStatesValidationAction.class.getResource(yesUrlIcon));

        /**
         *
         */
        private Icon noIcon = new ImageIcon(DiagramOfStatesValidationAction.class.getResource(noUrlIcon));

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public DiagramOfStatesValidationAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (diagramOfStateModel.isInitialized()) {
                        if (diagramOfStateModel.validate()) {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("validStateDiagram"), mxResources.get("validation"), JOptionPane.INFORMATION_MESSAGE, yesIcon);
                            //graphEditor.diagramOfStatesValidationView(classElement);
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                            graphEditor.diagramOfStatesValidationView(classElement);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (workArea.getSelectedFrame() instanceof StateEditor) {
                stateEditor = (StateEditor) workArea.getSelectedFrame();
                classElement = stateEditor.getClassElement();
                classModel = classElement.getClassModel();
                diagramOfStateModel = classModel.getDiagramOfStateModel();
                if (diagramOfStateModel.isInitialized()) {
                    if (diagramOfStateModel.validate()) {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("validStateDiagram"), mxResources.get("validation"), JOptionPane.INFORMATION_MESSAGE, yesIcon);
                        //graphEditor.diagramOfStatesValidationView(classElement);
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDiagram"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                        graphEditor.diagramOfStatesValidationView(classElement);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class DictionaryOfStatesValidationAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        /**
         *
         */
        private String yesUrlIcon = "/br/com/upf/images/yes.png";

        /**
         *
         */
        private String noUrlIcon = "/br/com/upf/images/no.png";

        /**
         *
         */
        private Icon yesIcon = new ImageIcon(DiagramOfStatesValidationAction.class.getResource(yesUrlIcon));

        /**
         *
         */
        private Icon noIcon = new ImageIcon(DiagramOfStatesValidationAction.class.getResource(noUrlIcon));

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public DictionaryOfStatesValidationAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    if (diagramOfStateModel.isInitialized()) {
                        if (classModel.validateDictionaryOfStates()) {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("validStateDictionary"), mxResources.get("validation"), JOptionPane.INFORMATION_MESSAGE, yesIcon);
                            //graphEditor.dictionaryOfStatesValidationView(classElement);
                        } else {
                            JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDictionary"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                            graphEditor.dictionaryOfStatesValidationView(classElement);
                        }
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (workArea.getSelectedFrame() instanceof StateEditor) {
                stateEditor = (StateEditor) workArea.getSelectedFrame();
                classElement = stateEditor.getClassElement();
                classModel = classElement.getClassModel();
                diagramOfStateModel = classModel.getDiagramOfStateModel();
                if (diagramOfStateModel.isInitialized()) {
                    if (classModel.validateDictionaryOfStates()) {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("validStateDictionary"), mxResources.get("validation"), JOptionPane.INFORMATION_MESSAGE, yesIcon);
                        //graphEditor.dictionaryOfStatesValidationView(classElement);
                    } else {
                        JOptionPane.showMessageDialog(graphEditor, mxResources.get("invalidStateDictionary"), mxResources.get("validation"), JOptionPane.ERROR_MESSAGE, noIcon);
                        graphEditor.dictionaryOfStatesValidationView(classElement);
                    }
                } else {
                    JOptionPane.showMessageDialog(graphEditor, mxResources.get("createTheStateDiagramBeforeProceeding"), mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class ClassModelRenameAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private StateEditor stateEditor;

        public ClassModelRenameAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                if (elementSelected()) {
                    String newName = JOptionPane.showInputDialog(graphEditor, "Informe o nome:", classModel.getName());
                    if ((newName != null) && (!newName.isEmpty())) {
                        classModel.setName(newName);
                        classElement.getClassView().updateContext();
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class ClassModelAddAttributeAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private ClassEditor classEditor;

        private AttributeEdition attributeEdition;

        private Attribute editingAttribute;

        public ClassModelAddAttributeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                classEditor = (ClassEditor) workArea.getSelectedFrame();
                if (elementSelected()) {
                    attributeEditionView(editingAttribute, false);
                    classElement.getClassView().updateContext();
                }
            }
        }

        /**
         * Gerencia a Visualização da Edição de Atributos
         *
         * @param attribute
         * @param edition
         */
        private void attributeEditionView(Attribute attribute, boolean edition) {
            attributeEdition = new AttributeEdition(graphEditor, classModel, attribute, edition);
            attributeEdition.setLocationRelativeTo(classEditor.getGraphComponent());

            attributeEdition.setVisible(true);
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class ClassModelEditAttributeAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private ClassEditor classEditor;

        private AttributeEdition attributeEdition;

        private Attribute editingAttribute;

        private ClassView classView;

        public ClassModelEditAttributeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                classEditor = (ClassEditor) workArea.getSelectedFrame();
                if (elementSelected()) {
                    classView = classElement.getClassView();
                    if (classView.getSelectedAttribute() != null) {
                        editingAttribute = classView.getSelectedAttribute();
                        attributeEditionView(editingAttribute, true);
                        classElement.getClassView().updateContext();
                    }
                }
            }
        }

        /**
         * Gerencia a Visualização da Edição de Atributos
         *
         * @param attribute
         * @param edition
         */
        private void attributeEditionView(Attribute attribute, boolean edition) {
            attributeEdition = new AttributeEdition(graphEditor, classModel, attribute, edition);
            attributeEdition.setLocationRelativeTo(classEditor.getGraphComponent());
            attributeEdition.setVisible(true);
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

    /**
     * Exibe Dialogo com validação do Diagrama de estados
     */
    public static class ClassModelDeleteAttributeAction extends AbstractAction {

        private static final long serialVersionUID = 1745388571801489485L;

        private GraphEditor graphEditor;

        private ClassModel classModel;

        private ClassElement classElement;

        private DiagramOfStateModel diagramOfStateModel;

        private JDesktopPane workArea;

        private ClassEditor classEditor;

        private Attribute editingAttribute;

        private ClassView classView;

        public ClassModelDeleteAttributeAction() {
            this.classModel = null;
            this.classElement = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.graphEditor = getEditor(e);
            this.workArea = graphEditor.getjDesktopPaneWorkArea();

            if (workArea.getSelectedFrame() instanceof ClassEditor) {
                classEditor = (ClassEditor) workArea.getSelectedFrame();
                if (elementSelected()) {
                    classView = classElement.getClassView();
                    if (classView.getSelectedAttribute() != null) {
                        int userOption = JOptionPane.showConfirmDialog(graphEditor, "Excluir o Atributo?", "Exclusão",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (userOption == 0) {
                            boolean result = classModel.removeAttribute(classView.getSelectedAttribute());
                            if (result) {
                                classModel.setUpdatedTableCode(false);
                                classElement.getClassView().updateContext();
                            } else {
                                JOptionPane.showMessageDialog(graphEditor, "Erro ao Excluir Atributo", mxResources.get("validation"), JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }
        }

        private boolean elementSelected() {

            mxGraphComponent graphComponent = graphEditor.getClassEditor().getGraphComponent();
            // Verifica quantos elementos foram selecionados
            if (graphComponent.getGraph().getSelectionCells().length == 1) {

                if (graphComponent.getGraph().getSelectionCell() instanceof mxCell) {
                    mxCell cell = (mxCell) graphComponent.getGraph().getSelectionCell();
                    if (cell.getValue() instanceof ClassModel) {
                        classModel = (ClassModel) cell.getValue();
                        classElement = graphEditor.getDiagramOfClassModel().getClassElement(classModel.getId());
                        diagramOfStateModel = classModel.getDiagramOfStateModel();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(graphComponent,
                            "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(graphComponent,
                        "Selecione uma Classe!", "Alert", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

}
