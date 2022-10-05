/*
  Blue Red White #1 - Test Blue-Red-White Icon Spacing
  Written by: Keith Fenske, http://kwfenske.github.io/
  Friday, 16 September 2022
  Java class name: BlueRedWhite1
  Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL.

  BlueRedWhite is a Java 1.4 graphical (GUI) application to test the size and
  spacing of alternating blue and red lines on a white background, as used by
  icons for the "Hex Byte Char" application.  Medium blue and medium red are
  strong colors when placed near each other.  Separating them with white
  creates different effects depending upon how wide the lines are.

  This program is of interest to graphic artists and Java programmers.  There
  are several limitations.  The GUI layout is low effort.  Sliders are
  approximate when moved with a mouse; arrow keys are more precise, and a
  dialog box with an exact number would be better.  The basic design assumes an
  even number of pixels (that is, a multiple of two) for icon sizes and the
  width of the white lines, which is not enforced in an obvious manner.

  Apache License or GNU General Public License
  --------------------------------------------
  BlueRedWhite1 is free software and has been released under the terms and
  conditions of the Apache License (version 2.0 or later) and/or the GNU
  General Public License (GPL, version 2 or later).  This program is
  distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY,
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE.  See the license(s) for more details.  You should have
  received a copy of the licenses along with this program.  If not, see the
  http://www.apache.org/licenses/ and http://www.gnu.org/licenses/ web pages.

  Suggestions for New Features
  ----------------------------
  (1) This program was developed for a specific purpose and is equivalent to a
      "proof of concept" or prototype.  Many things could be done better, and
      are good exercises for the reader.
  (2) Slider controls could be more specific, such as a list of common icon
      sizes instead of all numbers from 16 to 512 (or more), with a text field
      where the user could enter other numbers.  The width of the white lines
      should move in correct multiples of two.
  (3) Creating the image should be separate from drawing (painting).  There is
      no need to recreate the image when resizing the window or zooming.  Have
      fun getting the details right for the JScrollPane!
  (4) More efficient methods of rounding corners of a rectangle don't make much
      difference here and are more difficult to understand.  Any algorithm that
      mixes floating-point and integer arithmetic may have strange behavior
      when dealing with indivisible pixels on a square grid.
*/

import java.awt.*;                // older Java GUI support
import java.awt.datatransfer.*;   // clipboard
import java.awt.event.*;          // older Java GUI event support
import java.awt.image.*;          // buffered images
import java.io.*;                 // standard I/O
import javax.swing.*;             // newer Java GUI support
import javax.swing.border.*;      // decorative borders
import javax.swing.event.*;       // change listener for sliders

public class BlueRedWhite1
{
  /* constants */

  static final String COPYRIGHT_NOTICE =
    "Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL.";
  static final String PROGRAM_TITLE =
    "Test Blue-Red-White Icon Spacing - by: Keith Fenske";
  static final String SYSTEM_FONT = "Dialog"; // this font is always available

  /* class variables */

  static JSlider borderSlider, curveSlider, redSlider, sizeSlider, whiteSlider,
    zoomSlider;
  static JLabel borderText, curveText, redText, sizeText, whiteText, zoomText;
  static JLabel borderValue, curveValue, redValue, sizeValue, whiteValue,
    zoomValue;
  static BufferedImage clipImage; // image sent to clipboard
  static JFrame mainFrame;        // this application's window
  static JMenuBar menuBar;        // always visible menu bar
  static JMenuItem menuCopy, menuExit; // numerous menu items ;-)
  static BlueRedWhite1Grid outputCanvas; // where we draw the result
  static JScrollPane outputPane;  // canvas may need scroll bars

/*
  main() method

  We run as a graphical application only.  Set the window layout and then let
  the graphical interface run the show.
*/
  public static void main(String[] args)
  {
    ActionListener action = new BlueRedWhite1User(); // shared action listener
    clipImage = null;             // no image data yet for clipboard
    Font commonFont = new Font(SYSTEM_FONT, Font.PLAIN, 18); // most dialog
    Border emptyBorder = BorderFactory.createEmptyBorder(); // remove borders
    mainFrame = null;             // during setup, there is no GUI window

    /* Create the graphical interface as a series of smaller panels inside
    bigger panels.  The intermediate panel names are of no lasting importance
    and hence are only numbered (panel261, label354, etc). */

    JPanel panel20 = new JPanel(); // vertical stack of options
    panel20.setLayout(new BoxLayout(panel20, BoxLayout.Y_AXIS));
    panel20.add(Box.createVerticalStrut(20));

    menuBar = new JMenuBar();     // a menu bar has easy accelerator keys
    menuBar.setFont(commonFont);
    menuCopy = new JMenuItem("Copy Image");
    menuCopy.addActionListener(action);
    menuCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
      InputEvent.CTRL_MASK));
    menuCopy.setFont(commonFont);
    menuBar.add(menuCopy);
    menuExit = new JMenuItem("Exit (Close)");
    menuExit.addActionListener(action);
    menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
      InputEvent.ALT_MASK));
    menuExit.setFont(commonFont);
    menuBar.add(menuExit);
    JPanel panel35 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    panel35.add(menuBar);
    panel20.add(panel35);
    panel20.add(Box.createVerticalStrut(20));

    JPanel panel40 = new JPanel(new BorderLayout(10, 0));
    sizeText = new JLabel("Overall size (pixels x2):");
    sizeText.setFont(commonFont);
    panel40.add(sizeText, BorderLayout.WEST);
    sizeSlider = new JSlider(16, 512, 256);
    sizeSlider.addChangeListener((ChangeListener) action);
    panel40.add(sizeSlider, BorderLayout.CENTER);
    sizeValue = new JLabel(String.valueOf(sizeSlider.getValue()));
    sizeValue.setFont(commonFont);
    panel40.add(sizeValue, BorderLayout.EAST);
    panel20.add(panel40);
    panel20.add(Box.createVerticalStrut(10));

    JPanel panel45 = new JPanel(new BorderLayout(10, 0));
    borderText = new JLabel("Internal border (pixels):");
    borderText.setFont(commonFont);
    panel45.add(borderText, BorderLayout.WEST);
    borderSlider = new JSlider(0, 99, 8);
    borderSlider.addChangeListener((ChangeListener) action);
    panel45.add(borderSlider, BorderLayout.CENTER);
    borderValue = new JLabel(String.valueOf(borderSlider.getValue()));
    borderValue.setFont(commonFont);
    panel45.add(borderValue, BorderLayout.EAST);
    panel20.add(panel45);
    panel20.add(Box.createVerticalStrut(10));

    JPanel panel50 = new JPanel(new BorderLayout(10, 0));
    curveText = new JLabel("Corner curve (percent):");
    curveText.setFont(commonFont);
    panel50.add(curveText, BorderLayout.WEST);
    curveSlider = new JSlider(0, 99, 70);
    curveSlider.addChangeListener((ChangeListener) action);
    panel50.add(curveSlider, BorderLayout.CENTER);
    curveValue = new JLabel(String.valueOf(curveSlider.getValue()));
    curveValue.setFont(commonFont);
    panel50.add(curveValue, BorderLayout.EAST);
    panel20.add(panel50);
    panel20.add(Box.createVerticalStrut(10));

    JPanel panel55 = new JPanel(new BorderLayout(10, 0));
    whiteText = new JLabel("White width (pixels x2):");
    whiteText.setFont(commonFont);
    panel55.add(whiteText, BorderLayout.WEST);
    whiteSlider = new JSlider(0, 99, 12);
    whiteSlider.addChangeListener((ChangeListener) action);
    panel55.add(whiteSlider, BorderLayout.CENTER);
    whiteValue = new JLabel(String.valueOf(whiteSlider.getValue()));
    whiteValue.setFont(commonFont);
    panel55.add(whiteValue, BorderLayout.EAST);
    panel20.add(panel55);
    panel20.add(Box.createVerticalStrut(10));

    JPanel panel60 = new JPanel(new BorderLayout(10, 0));
    redText = new JLabel("Blue red width (pixels):");
    redText.setFont(commonFont);
    panel60.add(redText, BorderLayout.WEST);
    redSlider = new JSlider(1, 99, 18);
    redSlider.addChangeListener((ChangeListener) action);
    panel60.add(redSlider, BorderLayout.CENTER);
    redValue = new JLabel(String.valueOf(redSlider.getValue()));
    redValue.setFont(commonFont);
    panel60.add(redValue, BorderLayout.EAST);
    panel20.add(panel60);
    panel20.add(Box.createVerticalStrut(10));

    JPanel panel65 = new JPanel(new BorderLayout(10, 0));
    zoomText = new JLabel("Zoom in and enhance:");
    zoomText.setFont(commonFont);
    panel65.add(zoomText, BorderLayout.WEST);
    zoomSlider = new JSlider(1, 49, 1); // positive numbers only
    zoomSlider.addChangeListener((ChangeListener) action);
    panel65.add(zoomSlider, BorderLayout.CENTER);
    zoomValue = new JLabel(String.valueOf(zoomSlider.getValue()));
    zoomValue.setFont(commonFont);
    panel65.add(zoomValue, BorderLayout.EAST);
    panel20.add(panel65);

    JPanel panel70 = new JPanel(new BorderLayout(0, 0));
    panel70.add(panel20, BorderLayout.NORTH); // prevent vertical expansion

    outputCanvas = new BlueRedWhite1Grid();
//  outputCanvas.setBackground(Color.GRAY);
    outputCanvas.setPreferredSize(new Dimension(350, 350));
    outputPane = new JScrollPane(outputCanvas); // may need to scroll
    outputPane.setBorder(emptyBorder); // no border necessary here

    JPanel panel75 = new JPanel(new BorderLayout(20, 0));
    panel75.add(panel70, BorderLayout.WEST);
    panel75.add(outputPane, BorderLayout.CENTER); // expands with window

    /* Create the main window frame for this application.  We use a border
    layout to add margins around a central area for the panels above. */

    mainFrame = new JFrame(PROGRAM_TITLE);
    JPanel panel90 = (JPanel) mainFrame.getContentPane();
    panel90.setLayout(new BorderLayout(0, 0));
    panel90.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
    panel90.add(Box.createHorizontalStrut(20), BorderLayout.WEST);
    panel90.add(panel75, BorderLayout.CENTER);
    panel90.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
    panel90.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setLocation(250, 150); // normal top-left corner
    mainFrame.pack();             // do component layout with minimum size
    mainFrame.validate();         // recheck application window layout
    mainFrame.setVisible(true);   // and then show application window

    /* Let the graphical interface run the application now. */

  } // end of main() method

} // end of BlueRedWhite1 class

// ------------------------------------------------------------------------- //

/*
  BlueRedWhite1Grid class

  This class draws a pattern centered in the panel, with alternating blue and
  red lines on a white background, inside a cropped and rounded rectangle.
*/

class BlueRedWhite1Grid extends JPanel implements Transferable
{
  /* constants */

  static Color OUR_BLUE = new Color(0, 0, 128); // medium blue
  static Color OUR_GRAY = new Color(204, 204, 204); // background
  static Color OUR_RED = new Color(128, 0, 0); // medium red

  /* class constructor */

  public BlueRedWhite1Grid()
  {
    super();                      // initialize our superclass first (JPanel)
  }

  /* transferable interface to copy image to clipboard */

  public Object getTransferData(DataFlavor flavor)
    throws IOException, UnsupportedFlavorException
  {
    if (BlueRedWhite1.clipImage == null)
      throw new IOException("no clipboard image created");
    else if (flavor.equals(DataFlavor.imageFlavor))
      return(BlueRedWhite1.clipImage);
    else
      throw new UnsupportedFlavorException(flavor);
  }

  public DataFlavor[] getTransferDataFlavors()
  {
    final DataFlavor[] result = { DataFlavor.imageFlavor };
    return(result);
  }

  public boolean isDataFlavorSupported(DataFlavor flavor)
  {
    return(flavor.equals(DataFlavor.imageFlavor));
  }

  /* paint the display panel */

  protected void paintComponent(Graphics context)
  {
    super.paintComponent(context); // anything base JPanel wants first

    int panelHeight = this.getHeight(); // height of this panel in pixels
    int panelWidth = this.getWidth(); // width of this panel in pixels

    /* There are two parts to drawing the icon.  First is to create the blue
    and red stripes on a white background.  Second is to crop off the border
    and rounded corners.

    It would be nice if Java could use a rounded rectangle to clip the colored
    stripes.  This may be quite complicated, if it's even possible.  A mask was
    previously created from the fillRoundRect() method, but was not symmetrical
    (and never has been).

    What we do here is old school geometry: draw the stripes, calculate curves,
    and replace cropped parts with a shade of gray.  While this is not the most
    efficient method, it does get the job done with a reasonable effort. */

    int iconSize = BlueRedWhite1.sizeSlider.getValue(); // user's option
    iconSize -= iconSize % 2;     // basic design requires an even number
    int iconSizeHalf = iconSize / 2; // half the size on left, half on right

    /* Create an empty icon image with a white background. */

    BufferedImage image = new BufferedImage(iconSize, iconSize,
      BufferedImage.TYPE_INT_RGB);
    Graphics2D imageContext = image.createGraphics();
    imageContext.setColor(Color.WHITE);
    imageContext.fillRect(0, 0, iconSize, iconSize);

    /* Draw alternating blue and red stripes in the top-left quadrant.  This
    quadrant will later be duplicated into the other three positions.  We use
    an <offset> variable relative to the center. */

    int redWidth = BlueRedWhite1.redSlider.getValue(); // one or more
    redWidth = Math.max(1, redWidth); // infinite loop if red white both zero
    int whiteWidth = BlueRedWhite1.whiteSlider.getValue(); // zero or more
    whiteWidth -= whiteWidth % 2; // basic design requires an even number
    int offset = whiteWidth / 2;  // may be zero, don't really care
    boolean flipFlag = false;     // flips back and forth to alternate
    while (offset < iconSizeHalf) // yes, spills over, gets cropped
    {
      imageContext.setColor(flipFlag ? OUR_RED : OUR_BLUE);
      imageContext.fillRect(0, (iconSizeHalf - offset - redWidth),
        (iconSizeHalf - offset), redWidth); // horizontal
      imageContext.fillRect((iconSizeHalf - offset - redWidth), 0,
        redWidth, (iconSizeHalf - offset)); // vertical

      flipFlag = ! flipFlag;      // reverse colors next loop
      offset += redWidth + whiteWidth; // where next stripe starts
    }

    /* Crop off the border and rounded corners, using a method that works even
    if the corners are completely round or completely square.  Pixels are
    within a rounded rectangle if the square of their distance from a center
    point is within range.  Don't waste time calculating square roots here. */

    int borderWidth = BlueRedWhite1.borderSlider.getValue(); // zero or more
    borderWidth = Math.min(borderWidth, iconSizeHalf); // limit
    int radius = (int) Math.round(BlueRedWhite1.curveSlider.getValue()
      * (iconSizeHalf - borderWidth) / 100.0); // radius of circle
    int center = borderWidth + radius; // center of circle for rounding
    int square = (int) ((radius + 0.3) * (radius + 0.3)); // allow some fuzz
    int rgbGray = OUR_GRAY.getRGB(); // background color as integer
    for (int i = 0; i < iconSizeHalf; i ++)
      for (int k = 0; k < iconSizeHalf; k ++)
      {
        if ((i < borderWidth) || (k < borderWidth))
          image.setRGB(i, k, rgbGray); // always clear border
        else if ((i >= center) || (k >= center))
          { /* solid section, no curve */ }
        else if ((((i - center) * (i - center))
          + ((k - center) * (k - center))) > square)
        {
          image.setRGB(i, k, rgbGray); // outside the curve
        }
      }

    /* Mirror the top-left quadrant onto the other three positions.  Blue and
    red are inverted for the top-right and bottom-left quadrants. */

    int rgbBlue = OUR_BLUE.getRGB(); // colors as combined integers
    int rgbRed = OUR_RED.getRGB();
    for (int i = 0; i < iconSizeHalf; i ++)
      for (int k = 0; k < iconSizeHalf; k ++)
      {
        int color = image.getRGB(i, k); // original from top-left
        int invert = color;       // correct for gray and white
        if (color == rgbBlue)     // must invert blue and red
          invert = rgbRed;
        else if (color == rgbRed)
          invert = rgbBlue;
        image.setRGB((iconSize - i - 1), k, invert); // top-right
        image.setRGB(i, (iconSize - k - 1), invert); // bottom-left
        image.setRGB((iconSize - i - 1), (iconSize - k - 1), color);
                                  // bottom-right quadrant
      }

    /* Put our icon image onto the user's display.  Save the image in case we
    want it later for the system clipboard. */

    int centerHeight = panelHeight / 2; // middle of the icon (from panel top)
    int centerWidth = panelWidth / 2; // middle from panel left
    int zoomSize = iconSize * BlueRedWhite1.zoomSlider.getValue();
    int zoomSizeHalf = zoomSize / 2; // need to center image
    context.drawImage(image, (centerWidth - zoomSizeHalf),
      (centerHeight - zoomSizeHalf), zoomSize, zoomSize, null, null);

    context.setColor(Color.GRAY); // thin gray border around icon
    context.drawRect((centerWidth - zoomSizeHalf - 1),
      (centerHeight - zoomSizeHalf - 1), (zoomSize + 1), (zoomSize + 1));

    BlueRedWhite1.clipImage = image; // save for clipboard, if requested

  } // end of paintComponent() method

} // end of BlueRedWhite1Grid class

// ------------------------------------------------------------------------- //

/*
  BlueRedWhite1User class

  This class listens to input from the user: menu items and sliders.
*/

class BlueRedWhite1User implements ActionListener, ChangeListener
{
  /* empty constructor */

  public BlueRedWhite1User() { }

  /* action listener for menu items */

  public void actionPerformed(ActionEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == BlueRedWhite1.menuCopy) // "Copy Image" menu item
    {
      try                         // clipboard may not be available
      {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
          BlueRedWhite1.outputCanvas, null); // place notice on clipboard
      }
      catch (IllegalStateException ise)
      {
        JOptionPane.showMessageDialog(BlueRedWhite1.mainFrame,
          ("Can't put image on clipboard:\n" + ise.getMessage()));
      }
    }
    else if (source == BlueRedWhite1.menuExit) // "Exit (Close)" menu item
    {
      System.exit(0);             // immediate exit from GUI with no status
    }
    else                          // fault in program logic, not by user
    {
      System.err.println("Error in actionPerformed(): unknown ActionEvent: "
        + event);                 // should never happen, so write on console
    }
  } // end of actionPerformed() method

  /* change listener for sliders */

  public void stateChanged(ChangeEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == BlueRedWhite1.borderSlider)
    {
      BlueRedWhite1.borderValue.setText(String.valueOf(BlueRedWhite1
        .borderSlider.getValue()));
      BlueRedWhite1.outputCanvas.repaint();
    }
    else if (source == BlueRedWhite1.curveSlider)
    {
      BlueRedWhite1.curveValue.setText(String.valueOf(BlueRedWhite1
        .curveSlider.getValue()));
      BlueRedWhite1.outputCanvas.repaint();
    }
    else if (source == BlueRedWhite1.redSlider)
    {
      BlueRedWhite1.redValue.setText(String.valueOf(BlueRedWhite1
        .redSlider.getValue()));
      BlueRedWhite1.outputCanvas.repaint();
    }
    else if (source == BlueRedWhite1.sizeSlider)
    {
      BlueRedWhite1.sizeValue.setText(String.valueOf(BlueRedWhite1
        .sizeSlider.getValue()));
      updateCanvasSize();         // may need to change scroll bars
      BlueRedWhite1.outputCanvas.repaint();
    }
    else if (source == BlueRedWhite1.whiteSlider)
    {
      BlueRedWhite1.whiteValue.setText(String.valueOf(BlueRedWhite1
        .whiteSlider.getValue()));
      BlueRedWhite1.outputCanvas.repaint();
    }
    else if (source == BlueRedWhite1.zoomSlider)
    {
      BlueRedWhite1.zoomValue.setText(String.valueOf(BlueRedWhite1
        .zoomSlider.getValue()));
      updateCanvasSize();         // may need to change scroll bars
      BlueRedWhite1.outputCanvas.repaint();
    }
    else                          // fault in program logic, not by user
    {
      System.err.println("Error in stateChanged(): unknown ChangeEvent: "
        + event);                 // should never happen, so write on console
    }
  } // end of stateChanged() method

  /* update canvas size inside scroll pane */

  static void updateCanvasSize()
  {
    int size = BlueRedWhite1.sizeSlider.getValue()
      * BlueRedWhite1.zoomSlider.getValue() + 2;
    BlueRedWhite1.outputCanvas.setPreferredSize(new Dimension(size, size));
    BlueRedWhite1.outputCanvas.invalidate(); // recheck layout
  }

} // end of BlueRedWhite1User class

/* Copyright (c) 2022 by Keith Fenske.  Apache License or GNU GPL. */
