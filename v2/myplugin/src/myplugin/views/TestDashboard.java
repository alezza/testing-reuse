package myplugin.views;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TestDashboard extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "myplugin.views.TestDashboard";

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		try {		
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType junitLaunchConfigtype = manager.getLaunchConfigurationType("org.eclipse.jdt.junit.launchconfig");
			ILaunchConfiguration[] junitConfigurations = manager.getLaunchConfigurations(junitLaunchConfigtype);
			Combo comboJUnit = new Combo(parent, SWT.NONE);
			comboJUnit.removeAll();
			for  (ILaunchConfiguration junitConfiguration:junitConfigurations) {
				comboJUnit.add(junitConfiguration.getName());
			}
			Button buttonJUNit = new Button(parent, SWT.NONE);
			buttonJUNit.setText("JUnit");
			buttonJUNit.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(MouseEvent arg0) {
					System.out.println("Button JUnit has been clicked");
					
					try {
						ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
						
						ILaunchConfigurationType[] types = manager.getLaunchConfigurationTypes();
						for (ILaunchConfigurationType type:types) {
							System.out.println(type.getName() + ": " + type.getIdentifier());
							System.out.println();
						}
						ILaunchConfigurationType typ = manager.getLaunchConfigurationType("org.eclipse.jdt.junit.launchconfig");
						ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(typ);
						
						ILaunchConfiguration selectedJUnitTestConfiguration = null;
						
						for (int i = 0; i < configurations.length; i++) {
							ILaunchConfiguration configuration = configurations[i];
							if (configuration.getName().equals(comboJUnit.getText())) {
								selectedJUnitTestConfiguration = configuration;
								break;
							}
						}
						if (selectedJUnitTestConfiguration != null) {
							selectedJUnitTestConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
						}
						System.out.println("End of running JUnit");
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void mouseDown(MouseEvent arg0) {}
				@Override
				public void mouseDoubleClick(MouseEvent arg0) {}
			});
			
			ILaunchConfigurationType SWTLaunchConfigtype = manager.getLaunchConfigurationType("org.eclipse.pde.ui.JunitLaunchConfig");
			ILaunchConfiguration[] SWTConfigurations = manager.getLaunchConfigurations(SWTLaunchConfigtype);
			Combo comboSWTBot = new Combo(parent, SWT.NONE);
			comboSWTBot.removeAll();
			for  (ILaunchConfiguration SWTConfiguration:SWTConfigurations) {
				comboSWTBot.add(SWTConfiguration.getName());
			}
			Button buttonSWTBot = new Button(parent, SWT.NONE);
			buttonSWTBot.setText("SWTBot");
			buttonSWTBot.addMouseListener(new MouseListener() {
				@Override
				public void mouseUp(MouseEvent arg0) {
					System.out.println("Button JUnit has been clicked");
					
					try {
						ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
						
						ILaunchConfigurationType[] types = manager.getLaunchConfigurationTypes();
						for (ILaunchConfigurationType type:types) {
							System.out.println(type.getName() + ": " + type.getIdentifier());
							System.out.println();
						}
						ILaunchConfigurationType typ = manager.getLaunchConfigurationType("org.eclipse.pde.ui.JunitLaunchConfig");
						ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(typ);
						
						ILaunchConfiguration selectedSWTTestConfiguration = null;
						
						for (int i = 0; i < configurations.length; i++) {
							ILaunchConfiguration configuration = configurations[i];
							if (configuration.getName().equals(comboJUnit.getText())) {
								selectedSWTTestConfiguration = configuration;
								break;
							}
						}
						if (selectedSWTTestConfiguration != null) {
							selectedSWTTestConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void mouseDown(MouseEvent arg0) {}
				@Override
				public void mouseDoubleClick(MouseEvent arg0) {}
			});
			
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void setFocus() {}

}