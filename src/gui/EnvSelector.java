package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import bdbExplorerDAO.DAO;
/**
 * This panel shows list of available environments and reloads list of entities 
 * if the environment selection has been changed  
 */
public class EnvSelector extends JPanel {
	List<String> envsList = DAO.getEnvironments();
	public EnvSelector(final MainPanel mainPanel) {
		setLayout(new MigLayout());
		final JComboBox envsCombo = new JComboBox(envsList.toArray());
		add(new JLabel("Select Environment:"),"wrap");
		add(envsCombo,"gap para");
		envsCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String env = (String)envsCombo.getSelectedItem();
				DAO dao = new DAO(env);
				EntitySelector entitySelector = new EntitySelector(dao,mainPanel);
				mainPanel.setEntitySelector(entitySelector);
			}
		});
		if(envsList.size() > 0)
			envsCombo.setSelectedIndex(0);
	}
}
