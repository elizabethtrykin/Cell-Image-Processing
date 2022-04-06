
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class CellGUI
{

  public CellGUI(ArrayList<BufferedImage> imageList, ArrayList<String> functionList, int cellNum) throws Exception
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run() 
      {
        JFrame frame = new JFrame("Cell Image Processing + Counter");
        frame.setSize(660,610);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Allows user to close window

        //Shows the opened + closed final image first
        ImageIcon imageIcon = new ImageIcon(imageList.get(imageList.size()-1));
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        //Prints Number of Cells
        JLabel cellNumLabel = new JLabel("Number of Cells: " + cellNum);
        
        //Creates a List, the values of while will then be move into a JList, so that they can be displayed on the screen. 
        DefaultListModel list1 = new DefaultListModel<String>();
        for (String string : functionList) {
          list1.addElement(string);
        }

        JList list = new JList<>(list1);


        //Adding specifications to the frame 
        frame.add(jLabel, BorderLayout.CENTER);
        frame.getContentPane().add(cellNumLabel, BorderLayout.PAGE_END);
        frame.getContentPane().add(list, BorderLayout.LINE_START);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //Setting up event listener (When element in list is select changes the image displayed)
        list.addListSelectionListener(new ListSelectionListener(){
        
            @Override
            public void valueChanged(ListSelectionEvent e) {
                imageIcon.setImage(imageList.get(list.getSelectedIndex()));
                jLabel.setIcon(imageIcon);
                jLabel.repaint();

            }
        });
      }
    });
    
  }

}


