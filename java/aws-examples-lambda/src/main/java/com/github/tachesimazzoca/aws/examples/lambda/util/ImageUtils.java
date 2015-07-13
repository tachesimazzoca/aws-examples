package com.github.tachesimazzoca.aws.examples.lambda.util;

import org.apache.commons.io.IOUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

public class ImageUtils {
    private static ImageReader createImageReader(ImageInputStream input) {
        ImageReader ir;
        Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
        if (readers != null && readers.hasNext()) {
            ir = readers.next();
            ir.setInput(input);
        } else {
            throw new IllegalArgumentException("No available image readers.");
        }
        return ir;
    }

    private static ImageWriter createImageWriter(
            ImageOutputStream output, ImageReader reader) {
        ImageWriter iw = ImageIO.getImageWriter(reader);
        if (iw == null)
            throw new IllegalArgumentException("No available image writers.");
        iw.setOutput(output);
        return iw;
    }

    private static ImageWriter createImageWriter(
            ImageOutputStream output, String formatName) {
        ImageWriter iw;
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(formatName);
        if (writers != null && writers.hasNext()) {
            iw = writers.next();
            iw.setOutput(output);
        } else {
            throw new IllegalArgumentException("No available image writers.");
        }
        return iw;
    }

    public static void convert(InputStream input, OutputStream output,
                               int width, int height, String formatName)
            throws IOException {
        ImageReader ir = null;
        ImageWriter iw = null;
        ImageOutputStream ios = null;

        try {
            ImageIO.setUseCache(false);
            ImageInputStream iis = ImageIO.createImageInputStream(input);
            ir = createImageReader(iis);

            ios = ImageIO.createImageOutputStream(output);
            if (null != formatName) {
                iw = createImageWriter(ios, formatName);
            } else {
                iw = createImageWriter(ios, ir);
            }
            IIOImage img = ir.readAll(0, null);
            // Strip thumbnails and metadata of the image
            img.setThumbnails(null);
            img.setMetadata(null);
            // Resize the image
            img = resize(img, width, height);

            // write images
            if (formatName == null || formatName.equals(ir.getFormatName())) {
                iw.write(img);
            } else {
                // convert file format
                BufferedImage bimg = (BufferedImage) img.getRenderedImage();
                if (formatName.equals("png") || !bimg.getColorModel().hasAlpha()) {
                    iw.write(new IIOImage(bimg, null, null));
                } else {
                    BufferedImage buf = new BufferedImage(bimg.getWidth(), bimg.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = buf.createGraphics();
                    g2d.setRenderingHint(
                            RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bimg, 0, 0, bimg.getWidth(), bimg.getHeight(), null);
                    g2d.dispose();
                    iw.write(new IIOImage(buf, null, null));
                }
            }

        } catch (IOException e) {
            throw e;
        } finally {
            if (ir != null)
                ir.dispose();
            if (iw != null)
                iw.dispose();
            IOUtils.closeQuietly(ios);
        }
    }

    public static IIOImage resize(IIOImage image, int width, int height) {
        IIOImage img;
        BufferedImage bimg = (BufferedImage) image.getRenderedImage();
        Dimension dim = scale(width, height, bimg.getWidth(), bimg.getHeight());
        int w = (int) dim.getWidth();
        int h = (int) dim.getHeight();
        ColorModel cm = bimg.getColorModel();
        boolean transparentGIF = cm.hasAlpha() && (cm instanceof IndexColorModel);
        // convert if the image is not a transparent GIF
        if (!transparentGIF && (w != bimg.getWidth() || h != bimg.getHeight())) {
            BufferedImage buf;
            if (cm instanceof IndexColorModel)
                buf = new BufferedImage(w, h, bimg.getType(), (IndexColorModel) cm);
            else
                buf = new BufferedImage(w, h, bimg.getType());
            Graphics2D g2d = buf.createGraphics();
            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(bimg, 0, 0, w, h, null);
            g2d.dispose();
            img = new IIOImage(buf, null, null);
        } else {
            img = image;
        }
        return img;
    }

    public static Dimension scale(int boundaryW, int boundaryH,
                                  int sourceW, int sourceH) {
        int gw = (boundaryW == 0) ? sourceW : boundaryW;
        int gh = (boundaryH == 0) ? (sourceH * gw / sourceW) : boundaryH;
        int w = sourceW;
        int h = sourceH;
        if (w > gw) {
            w = gw;
            h = sourceH * w / sourceW;
        }
        if (h > gh) {
            h = gh;
            w = sourceW * h / sourceH;
        }
        return new Dimension(w, h);
    }
}
