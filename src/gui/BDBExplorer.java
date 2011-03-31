package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
/**
 * Main class that creates and shows a JFrame that contains only
 * one panel: {@link MainPanel}
 */
public class BDBExplorer extends JFrame {
	private static final long serialVersionUID = 1L;
	public BDBExplorer() {
		setTitle("BDB Explorer");
		getContentPane().add(new MainPanel());
		setPreferredSize(new Dimension(1000, 500));
		setLocation(200, 200);
	}	
	
    private static void createAndShowGUI() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {	}
        //Create and set up the window.
		BDBExplorer mainFrame = new BDBExplorer();
		mainFrame.pack();                                      
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true); 
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
