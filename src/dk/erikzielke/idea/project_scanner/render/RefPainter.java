package dk.erikzielke.idea.project_scanner.render;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

/**
 * @author erokhins
 */
public class RefPainter {
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12); // wtf, shouldn't we use ui font?
    public static final int REF_HEIGHT = 22;
    private static final int RECTANGLE_X_PADDING = 4;
    private static final int RECTANGLE_Y_PADDING = 3;
    private static final int REF_PADDING = 13;
    private static final int LABEL_PADDING = 3;

    private static final int ROUND_RADIUS = 5;
    private static final Color DEFAULT_FONT_COLOR = JBColor.BLACK;

    public RefPainter() {
    }

    private static double getStringWidth(@NotNull String str, @NotNull FontRenderContext renderContext) {
        return DEFAULT_FONT.getStringBounds(str, renderContext).getWidth();
    }

    private static void drawText(@NotNull Graphics2D g2, @NotNull String str, int paddingX, int paddingY) {
        FontMetrics metrics = g2.getFontMetrics();
        g2.setColor(DEFAULT_FONT_COLOR);
        int x = paddingX + REF_PADDING / 2;
        int y = paddingY + REF_HEIGHT / 2 + (metrics.getAscent() - metrics.getDescent()) / 2;
        g2.drawString(str, x, y);
    }

    private static void setupGraphics(Graphics2D g2) {
        g2.setFont(DEFAULT_FONT);
        g2.setStroke(new BasicStroke(1.5f));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    protected int getRowHeight() {
        return REF_HEIGHT;
    }

    public void drawLabels(@NotNull Graphics2D g2, @NotNull Map<String, Color> labels, int startPadding) {
        int padding = startPadding;
        for (Map.Entry<String, Color> entry : labels.entrySet()) {
            Rectangle rectangle = drawLabel(g2, entry.getKey(), padding, entry.getValue());
            padding += rectangle.width + LABEL_PADDING;
        }
    }

    public Rectangle drawLabel(@NotNull Graphics2D g2,
                               @NotNull String label,
                               int paddingX,
                               @NotNull Color bgColor) {
        setupGraphics(g2);
        FontMetrics metrics = g2.getFontMetrics();
        HorizontalDimensions horizontalDimensions = getHorizontalDimensions(label, paddingX, metrics);
        int x = horizontalDimensions.x;
        int width = horizontalDimensions.width;
        int y = RECTANGLE_Y_PADDING + (getRowHeight() - REF_HEIGHT) / 2;
        int height = REF_HEIGHT - 2 * RECTANGLE_Y_PADDING;
        RoundRectangle2D rectangle2D = new RoundRectangle2D.Double(x, y, width, height, ROUND_RADIUS, ROUND_RADIUS);

        g2.setColor(bgColor);
        g2.fill(rectangle2D);

        g2.setColor(JBColor.GRAY);
        g2.draw(rectangle2D);

        g2.setColor(JBColor.BLACK);
        drawText(g2, label, paddingX + flagWidth(), (getRowHeight() - REF_HEIGHT) / 2);


        return new Rectangle(x, y, width, height);
    }



    private HorizontalDimensions getHorizontalDimensions(String label, int paddingX, FontMetrics metrics) {
        int x = paddingX + REF_PADDING / 2 - RECTANGLE_X_PADDING;
        int width = metrics.stringWidth(label) + 2 * RECTANGLE_X_PADDING + flagWidth();
        return new HorizontalDimensions(x, width);
    }


    private int flagWidth() {
        return 0;
    }

    private static class HorizontalDimensions {
        private final int x;
        private final int width;

        private HorizontalDimensions(int x, int width) {
            this.x = x;
            this.width = width;
        }
    }

}
