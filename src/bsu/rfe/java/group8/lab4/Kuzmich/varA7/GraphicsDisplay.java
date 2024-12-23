package bsu.rfe.java.group8.lab4.Kuzmich.varA7;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private Font axisFont;

    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f,
                new float[]{15, 5, 15, 5, 15, 5, 5, 5,5,5,5,5}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 30);
    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0) return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;
        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        scale = Math.min(scaleX, scaleY);
        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        Font originalFont = canvas.getFont();
        canvas.setFont(new Font("Serif", Font.PLAIN, 12));

        for (Double[] point : graphicsData) {
            Point2D.Double center = xyToPoint(point[0], point[1]);
            GeneralPath marker = new GeneralPath();

            if (conditionMet(point[1])) {
                canvas.setColor(Color.BLUE);
            } else {
                canvas.setColor(Color.RED);
            }
            marker.moveTo(center.getX() - 5.5, center.getY() - 5.5);
            marker.lineTo(center.getX() + 5.5, center.getY() - 5.5);
            marker.lineTo(center.getX() + 5.5, center.getY() + 5.5);
            marker.lineTo(center.getX() - 5.5, center.getY() + 5.5);
            marker.closePath();
            marker.moveTo(center.getX() - 5.5, center.getY() - 5.5);
            marker.lineTo(center.getX() + 5.5, center.getY() + 5.5);
            marker.moveTo(center.getX() + 5.5, center.getY() - 5.5);
            marker.lineTo(center.getX() - 5.5, center.getY() + 5.5);
            canvas.draw(marker);

            String yValue = String.format("%.2f", point[1]);
            canvas.drawString(yValue, (float) center.getX() + 10, (float) center.getY() - 10);
        }

        canvas.setFont(originalFont); // Возвращаем оригинальный шрифт
    }


    protected boolean conditionMet(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
        String valueStr = bd.toString().replace(".", ""); // Удаляем точку для проверки цифр

        for (int i = 0; i < valueStr.length() - 1; i++) {
            if (Character.isDigit(valueStr.charAt(i)) && Character.isDigit(valueStr.charAt(i + 1))) {
                if (valueStr.charAt(i) >= valueStr.charAt(i + 1)) {
                    return false;
                }
            }
        }
        return true;
    }




    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }
        if (minY <= 0.0 && maxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

            Rectangle2D bounds0 = axisFont.getStringBounds("0", context);
            Point2D.Double labelPos0 = xyToPoint(0, 0);
            canvas.drawString("0", (float) (labelPos0.getX() - bounds0.getWidth()), (float) (labelPos0.getY() + bounds0.getY() + 30));

            Rectangle2D bounds1 = axisFont.getStringBounds("–", context);
            Point2D.Double labelPos1 = xyToPoint(0, 1);
            canvas.drawString("–", (float) (labelPos1.getX() - bounds1.getWidth() + 10), (float) (labelPos1.getY() + bounds1.getY()));

            Rectangle2D bounds2 = axisFont.getStringBounds("1", context);
            Point2D.Double labelPos2 = xyToPoint(0, 1);
            canvas.drawString("1", (float) (labelPos2.getX() - bounds2.getWidth()), (float) (labelPos2.getY() + bounds2.getY()));

            Rectangle2D bounds3 = axisFont.getStringBounds("|", context);
            Point2D.Double labelPos3 = xyToPoint(1, 0);
            canvas.drawString("|", (float) (labelPos3.getX() - bounds3.getWidth() - 5), (float) (labelPos3.getY() + bounds3.getY()) + 40);

            Rectangle2D bounds4 = axisFont.getStringBounds("1", context);
            Point2D.Double labelPos4 = xyToPoint(1, 0);
            canvas.drawString("1", (float) (labelPos4.getX() - bounds4.getWidth()), (float) (labelPos4.getY() + bounds4.getY()) + 60);

            Rectangle2D bounds5 = axisFont.getStringBounds("–", context);
            Point2D.Double labelPos5 = xyToPoint(1, 1);
            canvas.drawString("–", (float) (labelPos5.getX() - bounds5.getWidth() + 10), (float) (labelPos5.getY() + bounds5.getY()) + 30);

            Rectangle2D bounds6 = axisFont.getStringBounds("1", context);
            Point2D.Double labelPos6 = xyToPoint(1, 1);
            canvas.drawString("1", (float) (labelPos6.getX() - bounds6.getWidth()), (float) (labelPos6.getY() + bounds6.getY()) + 30);
        }
    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }


}
