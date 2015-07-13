package com.github.tachesimazzoca.aws.examples.lambda.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.awt.*;
import java.io.*;

import static org.junit.Assert.*;

public class ImageUtilsTest {
    private static class TestPattern {
        public final String source;
        public final String destination;
        public final int width;
        public final int height;
        public final String formatName;

        private TestPattern(String source, String destination,
                            int width, int height, String formatName) {
            this.source = source;
            this.destination = destination;
            this.width = width;
            this.height = height;
            this.formatName = formatName;
        }
    }

    private void runTestPatterns(TestPattern... patterns)
            throws IOException {
        for (int i = 0; i < patterns.length; i++) {
            FileInputStream fis = new FileInputStream(openTestFile(patterns[i].source));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageUtils.convert(fis, baos, patterns[i].width, patterns[i].height,
                    patterns[i].formatName);
            assertArrayEquals("patterns[" + i + "]", FileUtils.readFileToByteArray(
                    openTestFile(patterns[i].destination)), baos.toByteArray());
        }
    }

    private File openTestFile(String path) {
        return new File(getClass().getResource("/test").getPath(), path);
    }

    @Test
    public void testScale() {
        int[][] wh = {
                // 150x100
                { 320, 240, 150, 100, 150, 100 },
                { 0, 0, 150, 100, 150, 100 },
                { 0, 240, 150, 100, 150, 100 },
                { 320, 0, 150, 100, 150, 100 },
                // 400x100
                { 320, 240, 400, 100, 320, 80 },
                { 0, 50, 400, 100, 200, 50 },
                { 320, 0, 400, 100, 320, 80 },
                // 300x480
                { 320, 240, 300, 480, 150, 240 },
                { 0, 240, 300, 480, 150, 240 },
                { 320, 0, 300, 480, 300, 480 } };

        for (int i = 0; i < wh.length; i++) {
            Dimension dim = ImageUtils.scale(wh[i][0], wh[i][1], wh[i][2], wh[i][3]);
            assertEquals(wh[i][4], (int) dim.getWidth());
            assertEquals(wh[i][5], (int) dim.getHeight());
        }
    }

    @Test
    public void testConvert() throws IOException {
        runTestPatterns(
                new TestPattern("/peacock.jpg", "/peacock_60x60.jpg", 60, 60, null),
                new TestPattern("/desktop.png", "/desktop_80x50.png", 80, 50, null),
                new TestPattern("/cmyk.gif", "/cmyk_20x10.gif", 20, 10, null)
        );
    }
}
