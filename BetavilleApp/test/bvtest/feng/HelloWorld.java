package bvtest.feng;

import java.io.IOException;

import org.fenggui.FengGUI;
import org.fenggui.actor.ScreenshotActor;
import org.fenggui.binding.render.lwjgl.EventHelper;
import org.fenggui.binding.render.lwjgl.LWJGLBinding;
import org.fenggui.event.InputEvent;
import org.fenggui.theme.ITheme;
import org.fenggui.theme.xml.IXMLStreamableException;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import edu.poly.bxmc.betaville.jme.fenggui.extras.BetavilleXMLTheme;

/**
 * 
 */
public class HelloWorld
{
  public static void main(String[] args)
  {
    new HelloWorld().execute();
    System.exit(0);
  }

  org.fenggui.Display     desk           = null;
  private IExample        example        = new WindowTest();

  private int             lastButtonDown = -1;

  private ScreenshotActor screenshotActor;

  private long            lastRender     = 0;
  private long            renderPause    = 1000/10;

  public void buildGUI()
  {
    // init. the LWJGL Binding
    LWJGLBinding binding = new LWJGLBinding();

    // init the root Widget, that spans the whole
    // screen (i.e. the OpenGL context within the
    // Microsoft XP Window)
    desk = new org.fenggui.Display(binding);

    // Creation of the themes
    try
    {
      ITheme theme = new BetavilleXMLTheme("data/themes/default/default.xml");
      FengGUI.AddTheme("default", theme);
      FengGUI.setTheme("default");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (IXMLStreamableException e)
    {
      e.printStackTrace();
    }

    // build a simple test FengGUI-Window
    example.buildGUI(desk);
  }

  /**
   * 
   */
  private void destroy()
  {
    Display.destroy();
  }

  /**
   * 
   */
  public void execute()
  {
    try
    {
      initEverything();
    }
    catch (LWJGLException le)
    {
      le.printStackTrace();
      System.out.println("Failed to initialize Gears.");
      return;
    }

    mainLoop();

    destroy();
  }

  private void glInit(int width, int height)
  {
    // Go into orthographic projection mode.
    GL11.glMatrixMode(GL11.GL_PROJECTION);
    GL11.glLoadIdentity();
    GLU.gluOrtho2D(0, width, 0, height);
    GL11.glMatrixMode(GL11.GL_MODELVIEW);
    GL11.glLoadIdentity();
    GL11.glViewport(0, 0, width, height);

    // set clear color to ... ugly
    GL11.glClearColor(0.1f, 0.5f, 0.2f, 0.0f);
    // sync frame (only works on windows ) => THAT IS NOT TRUE (ask Rainer)
    Display.setVSyncEnabled(false);
  }

  /**
   * 
   */
  private void glRender()
  {

    GL11.glLoadIdentity();
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    GLU.gluLookAt(10, 8, 8, 0, 0, 0, 0, 0, 1);

    // GL11.glRotatef(rotAngle, 1f, 1f, 1);
    // GL11.glColor3f(0.42f, 0.134f, 0.44f);

    // rotAngle += 0.5;

    // draw GUI stuff
    desk.display();

    screenshotActor.renderToDos(desk.getBinding().getOpenGL(), desk.getWidth(),
        desk.getHeight());

    // hmm, i think we need to query the mouse pointer and
    // keyboard here and call the according
    // desk.mouseMoved, desk.keyPressed, etc.
    // methods...
  }

  /**
   * 
   */
  public final void initEverything() throws LWJGLException
  {
    Display.setDisplayMode(new DisplayMode(800, 600));
    Display.setFullscreen(false);

    Display.create();
    Display.setTitle("Gears");

    glInit(800, 600);

    // initialize keyboard
    Keyboard.create();

    // build the gui
    buildGUI();

    screenshotActor = new ScreenshotActor();
    screenshotActor.hook(desk);
  }

  private void mainLoop()
  {
    while (!Display.isCloseRequested())
    {
        readBufferedKeyboard();
        readBufferedMouse();

        glRender();
        Display.update();
        
        if (System.currentTimeMillis() - lastRender > (renderPause))
        {
          lastRender = System.currentTimeMillis();
        } else {
          try
          {
            Thread.sleep(renderPause);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
        }
    }
  }

  private void readBufferedKeyboard()
  {

    // check keys, buffered
    Keyboard.poll();

    while (Keyboard.next())
    {
      if (Keyboard.getEventKeyState()) // if pressed
      {
        desk.fireKeyPressedEvent(EventHelper.mapKeyChar(),
            EventHelper.mapEventKey());
      }
      else
      {
        desk.fireKeyReleasedEvent(EventHelper.mapKeyChar(),
            EventHelper.mapEventKey());
      }
    }

  }

  /**
   * reads a mouse in buffered mode
   */
  private void readBufferedMouse()
  {
    int x = Mouse.getX();
    int y = Mouse.getY();

    InputEvent event = null;

    // @todo the click count is not considered in LWJGL! #

    if (lastButtonDown != -1 && Mouse.isButtonDown(lastButtonDown))
    {
      event = desk.fireMouseDraggedEvent(x, y,
          EventHelper.getMouseButton(lastButtonDown));
    }
    else
    {
      if (Mouse.getDX() != 0 || Mouse.getDY() != 0)
        event = desk.fireMouseMovedEvent(x, y);

      if (lastButtonDown != -1)
      {
        event = desk.fireMouseReleasedEvent(x, y,
            EventHelper.getMouseButton(lastButtonDown));
        lastButtonDown = -1;
      }
      while (Mouse.next())
      {
        if (Mouse.getEventButton() != -1 && Mouse.getEventButtonState())
        {
          lastButtonDown = Mouse.getEventButton();
          event = desk.fireMousePressedEvent(x, y,
              EventHelper.getMouseButton(lastButtonDown));
        }
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0)
        {
          event = desk.fireMouseWheel(x, y, wheel > 0, 1, 1);
        }
      }
    }

    // check if event did hit a widget
    if (event != null && event.isUIHit())
    {
      // ui hit
    }
  }
}