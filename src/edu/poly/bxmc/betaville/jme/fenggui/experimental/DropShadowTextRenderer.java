package edu.poly.bxmc.betaville.jme.fenggui.experimental;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferInt;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.HashMap;

import org.fenggui.renderer.text.BufferedTextRenderer;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.fenggui.theme.xml.InputOnlyStream;
import org.fenggui.theme.xml.InputOutputStream;

/**
 * DropShadowTextRenderer adds a drop shadow to text textures.
 * Based on http://www.jroller.com/gfx/entry/non_rectangular_shadow.
 * 
 * @author Romain Guy
 * @author Sebastien Petrucci
 * @author Peter Schulz
 */
public class DropShadowTextRenderer extends BufferedTextRenderer
{
  public static String              KEY_BLUR_QUALITY        = "blur_quality";
  public static String              VALUE_BLUR_QUALITY_FAST = "fast";
  public static String              VALUE_BLUR_QUALITY_HIGH = "high";

  protected float                   angle                   = 90;
  protected int                     distance                = 1;

  protected int                     distance_x              = 0;
  protected int                     distance_y              = 0;
  protected HashMap<Object, Object> hints;

  protected Color                   shadowColor             = new Color(
                                                                0x000000);
  protected float                   shadowOpacity           = 0.5f;
  
  protected int                     shadowSize              = 1;
  
  public DropShadowTextRenderer() {
    init();
  }

  public DropShadowTextRenderer(InputOnlyStream stream) throws IOException,
      IXMLStreamableException
  {
    init();
    process(stream);
  }
  
  @Override
  public void process(InputOutputStream stream) throws IOException,
      IXMLStreamableException
  {
    super.process(stream);
    setShadowColor(org.fenggui.util.Util.convert(
        stream.processChild("Color", 
        org.fenggui.util.Util.convert(shadowColor), 
        org.fenggui.util.Color.BLACK, org.fenggui.util.Color.class)));
    setShadowOpacity(stream.processAttribute("opacity", shadowOpacity, 0.5f));
    setShadowSize(stream.processAttribute("size", shadowSize, 1));
    setDistance(stream.processAttribute("distance", distance, 0));
    setAngle(stream.processAttribute("angle", angle, 0));
    setRenderingHint(KEY_BLUR_QUALITY, stream.processAttribute("quality", 
        hints.get(KEY_BLUR_QUALITY).toString(), VALUE_BLUR_QUALITY_FAST));
  }

  public float getAngle() {
      return angle;
  }

  public int getDistance() {
      return distance;
  }

  public Color getShadowColor() {
      return shadowColor;
  }

  public float getShadowOpacity() {
      return shadowOpacity;
  }

  public int getShadowSize() {
      return shadowSize;
  }

  public void setAngle(float angle) {
      this.angle = angle;
      computeShadowPosition();
  }

  public void setDistance(int distance) {
      this.distance = distance;
      computeShadowPosition();
  }

  public void setRenderingHint(Object hint, Object value) {
      hints.put(hint, value);
  }

  public void setShadowColor(Color shadowColor) {
      if (shadowColor != null) {
          this.shadowColor = shadowColor;
      }
  }

  public void setShadowOpacity(float shadowOpacity) {
      this.shadowOpacity = shadowOpacity;
  }

  public void setShadowSize(int shadowSize) {
      this.shadowSize = shadowSize;
  }

  private void applyShadow(BufferedImage image) {
      int dstWidth = image.getWidth();
      int dstHeight = image.getHeight();

      int left = (shadowSize - 1) >> 1;
      int right = shadowSize - left;
      int xStart = left;
      int xStop = dstWidth - right;
      int yStart = left;
      int yStop = dstHeight - right;

      int shadowRgb = shadowColor.getRGB() & 0x00FFFFFF;

      int[] aHistory = new int[shadowSize];
      int historyIdx = 0;

      int aSum;

      int[] dataBuffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
      int lastPixelOffset = right * dstWidth;
      float sumDivider = shadowOpacity / shadowSize;

      // horizontal pass

      for (int y = 0, bufferOffset = 0; y < dstHeight; y++, bufferOffset = y * dstWidth) {
          aSum = 0;
          historyIdx = 0;
          for (int x = 0; x < shadowSize; x++, bufferOffset++) {
              int a = dataBuffer[bufferOffset] >>> 24;
              aHistory[x] = a;
              aSum += a;
          }

          bufferOffset -= right;

          for (int x = xStart; x < xStop; x++, bufferOffset++) {
              int a = (int) (aSum * sumDivider);
              dataBuffer[bufferOffset] = a << 24 | shadowRgb;

              // substract the oldest pixel from the sum
              aSum -= aHistory[historyIdx];

              // get the lastest pixel
              a = dataBuffer[bufferOffset + right] >>> 24;
              aHistory[historyIdx] = a;
              aSum += a;

              if (++historyIdx >= shadowSize) {
                  historyIdx -= shadowSize;
              }
          }
      }

      // vertical pass
      for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {
          aSum = 0;
          historyIdx = 0;
          for (int y = 0; y < shadowSize; y++, bufferOffset += dstWidth) {
              int a = dataBuffer[bufferOffset] >>> 24;
              aHistory[y] = a;
              aSum += a;
          }

          bufferOffset -= lastPixelOffset;

          for (int y = yStart; y < yStop; y++, bufferOffset += dstWidth) {
              int a = (int) (aSum * sumDivider);
              dataBuffer[bufferOffset] = a << 24 | shadowRgb;

              // substract the oldest pixel from the sum
              aSum -= aHistory[historyIdx];

              // get the lastest pixel
              a = dataBuffer[bufferOffset + lastPixelOffset] >>> 24;
              aHistory[historyIdx] = a;
              aSum += a;

              if (++historyIdx >= shadowSize) {
                  historyIdx -= shadowSize;
              }
          }
      }
  }

  private void computeShadowPosition() {
      double angleRadians = Math.toRadians(angle);
      distance_x = (int) (Math.cos(angleRadians) * distance);
      distance_y = (int) (Math.sin(angleRadians) * distance);
      
      System.out.println(String.format("%d %d", distance_x, distance_y));
  }

  private BufferedImage createDropShadow(BufferedImage image) {
      BufferedImage subject = prepareImage(image);

      if (hints.get(KEY_BLUR_QUALITY) == VALUE_BLUR_QUALITY_HIGH) {
          BufferedImage shadow = new BufferedImage(subject.getWidth(),
                                                   subject.getHeight(),
                                                   BufferedImage.TYPE_INT_ARGB);
          BufferedImage shadowMask = createShadowMask(subject);
          getLinearBlurOp(shadowSize).filter(shadowMask, shadow);
          return shadow;
      }

      applyShadow(subject);
      return subject;
  }

  private BufferedImage createShadowMask(BufferedImage image) {
      BufferedImage mask = new BufferedImage(image.getWidth(),
                                             image.getHeight(),
                                             BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2d = mask.createGraphics();
      g2d.drawImage(image, 0, 0, null);
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN,
                                                  shadowOpacity));
      g2d.setColor(shadowColor);
      g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
      g2d.dispose();

      return mask;
  }

  private ConvolveOp getLinearBlurOp(int size) {
      float[] data = new float[size * size];
      float value = 1.0f / (float) (size * size);
      for (int i = 0; i < data.length; i++) {
          data[i] = value;
      }
      return new ConvolveOp(new Kernel(size, size, data));
  }

  private BufferedImage prepareImage(BufferedImage image) {
      BufferedImage subject = new BufferedImage(image.getWidth() + shadowSize * 2,
                                                image.getHeight() + shadowSize * 2,
                                                BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2 = subject.createGraphics();
      g2.drawImage(image, null, shadowSize, shadowSize);
      g2.dispose();

      return subject;
  }
  
  private void init()
  {
    computeShadowPosition();
    hints = new HashMap<Object, Object>();
    hints.put(KEY_BLUR_QUALITY, VALUE_BLUR_QUALITY_HIGH);
  }

  @Override
  protected void drawChar(Graphics g, int x, int y, BufferedImage charImg)
  {
    BufferedImage shadow = createDropShadow(charImg);
    
    if (shadow != null) {
      g.drawImage(shadow, x + distance_x - shadowSize , y + distance_y - shadowSize, null);
    }
    
    super.drawChar(g, x, y, charImg);
  }
  
}
