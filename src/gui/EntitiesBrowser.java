package gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import utils.MyField;
import utils.MyObject;
import utils.ReflectionUtils;

import net.miginfocom.swing.MigLayout;

public class EntitiesBrowser extends JPanel implements TreeSelectionListener, MouseListener {
	JTree entitiesTree = null;
	public EntitiesBrowser(List<Object> entitiesList,Class<?> entityClass) {
		setLayout(new MigLayout());
		setPreferredSize(new Dimension(500,500));
		entitiesTree = buildEntitiesTree(entitiesList, entityClass);
		JScrollPane scrollPane = new JScrollPane(entitiesTree);
		scrollPane.setPreferredSize(new Dimension(500,500));
		add(scrollPane);
	}
	
	private JTree buildEntitiesTree(List<Object> entitiesList,Class<?> entityClass){
		Field pkField = ReflectionUtils.getPrimaryKeyField(entityClass);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Entities");
		JTree entitieTree = new JTree(root);
		entitieTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		entitieTree.addTreeSelectionListener(this);
		entitieTree.addMouseListener(this);
		int i=0;
		for(Object entity:entitiesList){
			DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(new MyField(null,entity,i++));
			root.add(entityNode);
		}
		entitieTree.expandRow(0);
		return entitieTree;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) entitiesTree.getLastSelectedPathComponent();
		if(node == null)
			return;
		
		// check if this node was already expanded
		if(node.getChildCount()>0)
			return;
		Object nodeObj = ((MyField)node.getUserObject()).getFieldValue();
		if(nodeObj == null)
			return;
		if(ReflectionUtils.isArray(nodeObj)){
			int size = Array.getLength(nodeObj);
			for(int i=0;i<size;i++){
				Object arrElem = Array.get(nodeObj, i);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new MyField(null,arrElem,i));
				node.add(newNode);
			}
		}else if(nodeObj instanceof Collection<?>){
			Collection<?> col = (Collection<?>) nodeObj;
			int i=0;
			for(Object obj:col){
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new MyField(null,obj,i++));
				node.add(newNode);				
			}
		}else if(!ReflectionUtils.isPrimitive(nodeObj)){
			List<Field> fields = ReflectionUtils.getAllNonStaticFieds(nodeObj);
			for(Field field:fields){
				field.setAccessible(true);
					DefaultMutableTreeNode newNode = null;
					try {
						newNode = new DefaultMutableTreeNode(new MyField(field.getName(),field.get(nodeObj)));
					} catch (IllegalArgumentException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
					node.add(newNode);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) entitiesTree.getLastSelectedPathComponent();
			// if this is a leaf node, display it's value
			if(node.getChildCount()==0){
				Object nodeObj = ((MyField)node.getUserObject()).getFieldValue();
				JOptionPane.showInputDialog(this,null,"Value of the field",JOptionPane.PLAIN_MESSAGE,null,null,nodeObj);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
}
