package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import utils.ReflectionUtils;

import bdbExplorerDAO.DAO;

public class SearchPanel extends JPanel{
	
	JTextField inputField = new JTextField(10);
	
	public SearchPanel(final DAO dao, final Class entityClass,final Field pkField, final MainPanel mainPanel,final JFrame searchFrame) {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(300,300));
		addFields(pkField, this);
		JButton cancel = new JButton("Cancel");
		JButton find = new JButton("Find");
		
		add(find,"gap para, newline, align right");
		add(cancel);
		
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchFrame.dispose();
			}
		});
		
		find.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fieldValues[] = new String[1];
				fieldValues[0] = inputField.getText();
				Object pkObj = ReflectionUtils.createObject(pkField.getType(), fieldValues);
				Object entity = dao.getEntityByPK(entityClass, pkObj);
				List<Object> entities = new ArrayList<Object>(1);
				entities.add(entity);
				EntitiesBrowser entitiesBrowser = new EntitiesBrowser(entities, entityClass);
				mainPanel.setEntityBrowser(entitiesBrowser);
				searchFrame.dispose();
			}
		});
	}
	
	private void addFields(Field field,JPanel parentPanel){
		parentPanel.add(new JLabel(field.getName()+":"));
		if(ReflectionUtils.isPrimitive(field.getType())){
			parentPanel.add(inputField);
		}
	}

}
