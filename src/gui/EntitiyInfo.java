package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorListener;

import utils.ReflectionUtils;

import net.miginfocom.swing.MigLayout;

import bdbExplorerDAO.DAO;
/**
 * This panel show information about the primary key of the entity, and add two
 * buttons to show all entities of this type and to search one entity by pk 
 */
public class EntitiyInfo extends JPanel {
	public EntitiyInfo(final DAO dao,String entityClazz,final MainPanel mainPanel) {
		setLayout(new MigLayout(""));
		JPanel entityInfoPanel = new JPanel(new MigLayout());
		entityInfoPanel.setBorder(BorderFactory.createTitledBorder("Entity pk info"));
		
		final Class<?> entityClass = ReflectionUtils.getClass(entityClazz);
		final Field pkField = ReflectionUtils.getPrimaryKeyField(entityClass);
		entityInfoPanel.add(new JLabel("name:"),"");
		entityInfoPanel.add(new JLabel(pkField.getName()),"wrap");
		entityInfoPanel.add(new JLabel("type:"),"");
		entityInfoPanel.add(new JLabel(pkField.getType().toString()),"wrap");
		add(entityInfoPanel,"wrap, span 2");
		JButton browse = new JButton("Browse");
		JButton search = new JButton("Search");
		add(browse);
		add(search);
		
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Object> entities = dao.getAllEntities(entityClass);
				EntitiesBrowser entitiesBrowser = new EntitiesBrowser(entities, entityClass);
				mainPanel.setEntityBrowser(entitiesBrowser);
			}
		});
		
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame searchFrame = new JFrame("Enter pk value");
				searchFrame.setLayout(new MigLayout());
				searchFrame.setLocation(500, 500);
//				searchFrame.setPreferredSize(new Dimension(200, 200));
				SearchPanel searchPanel = new SearchPanel(dao,entityClass,pkField,mainPanel,searchFrame);
				searchFrame.add(searchPanel);
				searchFrame.pack();                                      
				searchFrame.setVisible(true);
			}
		});
	}
}
