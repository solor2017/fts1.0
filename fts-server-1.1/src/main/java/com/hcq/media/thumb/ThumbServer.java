package com.hcq.media.thumb;

import com.sun.image.codec.jpeg.JPEGCodec;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class ThumbServer {
    public void process(String paramString1, String paramString2, int paramInt1, int paramInt2) {
        process(new File(paramString1), new File(paramString2), paramInt1, paramInt2);
    }

    public void process(File paramFile1, File paramFile2, int paramInt1, int paramInt2) {
        ImageTools localImageTools = new ImageTools();
        localImageTools.imageZoom(paramFile1, paramInt1, paramInt2, paramFile2);
    }

    private class ImageTools {
        private ImageTools() {
        }

        public void imageZoom(File paramFile1, int paramInt1, int paramInt2, File paramFile2) {
            imageZoom(paramFile1.getAbsolutePath(), paramInt1, paramInt2, paramFile2.getAbsolutePath());
        }

        public void imageZoom(InputStream paramInputStream, int paramInt1, int paramInt2, OutputStream paramOutputStream) {
            try {
                BufferedImage localBufferedImage1 = ImageIO.read(paramInputStream);
                int i = localBufferedImage1.getWidth();
                int j = localBufferedImage1.getHeight();
                BufferedImage localBufferedImage2 = new BufferedImage(paramInt1, paramInt2, 1);
                Graphics localGraphics = localBufferedImage2.getGraphics();
                Rect localRect = getRect(paramInt1, paramInt2, i, j);
                localGraphics.setColor(new Color(255, 255, 255));
                localGraphics.fillRect(0, 0, paramInt1, paramInt2);
                localGraphics.drawImage(localBufferedImage1, localRect.x, localRect.y, localRect.width, localRect.height, null);
                JPEGCodec.createJPEGEncoder(paramOutputStream).encode(localBufferedImage2);
                paramOutputStream.close();
            } catch (Exception localException3) {
            } finally {
                try {
                    if (paramOutputStream != null)
                        paramOutputStream.close();
                    paramOutputStream = null;
                } catch (Exception localException4) {
                }
            }
        }

        public void imageZoom(String paramString1, int paramInt1, int paramInt2, String paramString2) {
            try {
                imageZoom(new FileInputStream(paramString1), paramInt1, paramInt2, new FileOutputStream(paramString2));
            } catch (Exception localException) {
            }
        }

        public void imageZoom(InputStream paramInputStream, int paramInt1, int paramInt2, String paramString) {
            try {
                imageZoom(paramInputStream, paramInt1, paramInt2, new FileOutputStream(paramString));
            } catch (Exception localException) {
            }
        }

        private Rect getRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
            Rect localRect = new Rect();
            if ((paramInt1 >= paramInt3) && (paramInt2 >= paramInt4)) {
                localRect.x = ((paramInt1 - paramInt3) / 2);
                localRect.y = ((paramInt2 - paramInt4) / 2);
                localRect.width = paramInt3;
                localRect.height = paramInt4;
            } else {
                float f1 = paramInt1 / paramInt2;
                float f2 = paramInt3 / paramInt4;
                if (f2 > f1) {
                    localRect.width = paramInt1;
                    localRect.x = 0;
                    localRect.height = (paramInt4 * localRect.width / paramInt3);
                    localRect.y = ((paramInt2 - localRect.height) / 2);
                } else {
                    localRect.height = paramInt2;
                    localRect.y = 0;
                    localRect.width = (paramInt2 * paramInt3 / paramInt4);
                    localRect.x = ((paramInt1 - localRect.width) / 2);
                }
            }
            return localRect;
        }

        class Rect {
            int x;
            int y;
            int width;
            int height;

            Rect() {
            }
        }
    }
}
