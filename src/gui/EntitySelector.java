package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import bdbExplorerDAO.DAO;
/**
 * This panel shows list of available entities and reloads the information
 * about the primary key of the selected entity on selection change.
 */
public class EntitySelector extends JPanel {
	public EntitySelector(final DAO dao,final MainPanel mainPanel) {
		setLayout(new MigLayout());
		final JComboBox entitiesCombo = new JComboBox(dao.getEntities().toArray());
		add(new JLabel("Select Entity Type:"),"wrap");
		add(entitiesCombo,"gap para");
		entitiesCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String entity = (String)entitiesCombo.getSelectedItem();
				EntitiyInfo entityInfo = new EntitiyInfo(dao, entity,mainPanel);
				mainPanel.setEntityInfo(entityInfo);
			}
		});
		entitiesCombo.setSelectedIndex(0);
	}
}
