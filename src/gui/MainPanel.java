package gui;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
/**
 * Main panel in the JFrame.
 * It contains place holders for different GUI components of the application.
 */
public class MainPanel extends JPanel {
	JPanel entitySelecorHolder = new JPanel(new MigLayout("insets 0"));;
	JPanel entitiesInfoHolder = new JPanel(new MigLayout("insets 0"));;
	JPanel entitiesBrowserHolder = new JPanel(new MigLayout("insets 0"));
	public MainPanel() {
		setLayout(new MigLayout("flowy,insets 0","[][grow]","[][][top]"));
        add(new EnvSelector(this));
        add(entitySelecorHolder);
        add(entitiesInfoHolder,"wrap");
        add(entitiesBrowserHolder,"spany");
	}
	
	public void setEntitySelector(EntitySelector entitySelector){
		entitySelecorHolder.removeAll();
		entitySelecorHolder.add(entitySelector);
		entitySelecorHolder.revalidate();
	}

	public void setEntityInfo(EntitiyInfo entityInfo){
		entitiesInfoHolder.removeAll();
		entitiesInfoHolder.add(entityInfo);
		entitiesInfoHolder.revalidate();
	}

	public void setEntityBrowser(EntitiesBrowser entitiesBrowser){
		entitiesBrowserHolder.removeAll();
		entitiesBrowserHolder.add(entitiesBrowser);
		entitiesBrowserHolder.revalidate();
	}
}