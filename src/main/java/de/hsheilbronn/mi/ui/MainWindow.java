package de.hsheilbronn.mi.ui;

import de.hsheilbronn.mi.controller.SvmController;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    public static final int DEFAULT_SQUARE_PIXEL = 10;
    public static final int MAX_2D_CATEGORY_VALUE = 3;
    final static Color DEFAULT_COLORS[] =
            {
                    new Color(0, 0, 0),
                    new Color(0, 120, 120),
                    new Color(120, 120, 0),
                    new Color(120, 0, 120),
                    new Color(0, 200, 200),
                    new Color(200, 200, 0),
                    new Color(200, 0, 200)
            };
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MainWindow.class);
    private Image buffer;
    private Graphics graphicsBuffer;
    private List<Svm2DPoint> pointList = new ArrayList<>();
    private byte category = 1;

    private SvmController svmController;

    public MainWindow(SvmController svmController) {
        this.setTitle("zlibsvm: example project");

        this.setPreferredSize(new Dimension(800, 800));

        JButton run = new JButton("Run");

        this.svmController = svmController;

        run.addActionListener(actionEvent -> {
            if (!pointList.isEmpty()) {
                svmController.train(pointList);

                Graphics windowGraphic = getGraphics();

                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {
                        Svm2DPoint p = new Svm2DPoint((double) x / getWidth(), (double) y / getHeight());

                        byte category = svmController.predict(p);

                        graphicsBuffer.setColor(DEFAULT_COLORS[category]);
                        windowGraphic.setColor(DEFAULT_COLORS[category]);
                        graphicsBuffer.drawLine(x, y, x, y);
                        windowGraphic.drawLine(x, y, x, y);
                    }
                }

                drawAll();
            }
        });

        JButton clear = new JButton("Clear");

        clear.addActionListener(e -> clearAll());

        JButton change = new JButton("Switch Category");
        change.addActionListener(e -> changeCategory());

        JButton load = new JButton("Load");
        load.addActionListener(e -> loadData());

        JButton save = new JButton("Save");
        save.addActionListener(e -> saveData());

        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        Panel p = new Panel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        p.setLayout(gridBagLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gridBagLayout.setConstraints(run, gbc);
        gridBagLayout.setConstraints(clear, gbc);
        gridBagLayout.setConstraints(change, gbc);
        gridBagLayout.setConstraints(load, gbc);
        gridBagLayout.setConstraints(save, gbc);

        p.add(load);
        p.add(save);
        p.add(change);
        p.add(clear);
        p.add(run);


        this.add(p, BorderLayout.SOUTH);

        this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

    }

    private void saveData() {

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            StringBuilder sb = new StringBuilder();

            for (Svm2DPoint point : pointList) {
                sb.append(point.toString());
                sb.append("\n");
            }

            try {
                FileUtils.write(file, sb.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }

    }

    private void loadData() {

        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);

                clearAll();

                for (String line : lines) {
                    String[] split = line.split(",");

                    byte category = Byte.parseByte(split[0]);
                    double x = Double.parseDouble(split[1]);
                    double y = Double.parseDouble(split[2]);

                    Svm2DPoint point = new Svm2DPoint(x, y, category);

                    pointList.add(point);

                }

                drawAll();
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
    }

    private void clearAll() {
        pointList.clear();
        if (buffer != null) {
            graphicsBuffer.setColor(DEFAULT_COLORS[0]);
            graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());
        }
        repaint();
    }

    private void drawAll() {
        for (Svm2DPoint point : pointList) {
            drawPoint(point);
        }
    }

    private void changeCategory() {
        ++category;
        if (category > MAX_2D_CATEGORY_VALUE) {
            category = 1;
        }
    }


    public void paint(Graphics g) {
        // create buffer first time
        if (buffer == null) {
            buffer = this.createImage(getWidth(), getHeight());
            graphicsBuffer = buffer.getGraphics();
            graphicsBuffer.setColor(DEFAULT_COLORS[0]);
            graphicsBuffer.fillRect(0, 0, getWidth(), getHeight());
        }
        g.drawImage(buffer, 0, 0, this);
    }


    private void drawPoint(Svm2DPoint point) {
        Color pointColor = DEFAULT_COLORS[point.getCategory() + 3];

        Graphics windowsGraphics = getGraphics();
        graphicsBuffer.setColor(pointColor);

        int x = (int) (point.getX() * getWidth());
        int y = (int) (point.getY() * getHeight());
        graphicsBuffer.fillRect(x, y, DEFAULT_SQUARE_PIXEL, DEFAULT_SQUARE_PIXEL);
        windowsGraphics.setColor(pointColor);
        windowsGraphics.fillRect(x, y, DEFAULT_SQUARE_PIXEL, DEFAULT_SQUARE_PIXEL);
    }

    protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if (e.getX() >= getWidth() || e.getY() >= getHeight()) {
                return;
            }
            Svm2DPoint p = new Svm2DPoint((double) e.getX() / getWidth(), (double) e.getY() / getHeight(), category);
            pointList.add(p);
            drawPoint(p);
        }
    }

}
