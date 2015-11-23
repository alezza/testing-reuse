package myplugin.views;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.junit.launcher.JUnitLaunchShortcut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
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
		
		Button buttonJUNit = new Button(parent, SWT.NONE);
		buttonJUNit.setText("JUNit");
		buttonJUNit.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				System.out.println("Button JUnit has been clicked");

				try {
					ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
					/*ILaunchConfigurationType[] types = manager.getLaunchConfigurationTypes();
					for (ILaunchConfigurationType type:types) {
						System.out.println(type.getName() + ": " + type.getIdentifier());
						System.out.println();
					}*/
					ILaunchConfigurationType typ = manager.getLaunchConfigurationType("org.eclipse.jdt.junit.launchconfig");
					ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(typ);
					/*for (int i = 0; i < configurations.length; i++) {
						ILaunchConfiguration configuration = configurations[i];
						if (configuration.getName().equals("ConfigurationName")) {
							configuration.delete();
							break;
						}
					}*/
					ILaunchConfiguration firstJUnitTestConfiguration = configurations[0];
					firstJUnitTestConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
					
				} catch (CoreException e) {
					e.printStackTrace();
				}

				/*result = JUnitCore.runClasses(TestJunit.class);
				System.out.println(result.getRunCount());
				System.out.println(result.getRunTime());
				System.out.println(result.getFailureCount());
*/
//				try {
//					Process p = Runtime.getRuntime().exec("");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				JUnitLaunchShortcut jUnitLaunchShortcut = new JUnitLaunchShortcut();
//				jUnitLaunchShortcut.launch("Pass the Java Project containing JUnits Classes", "run");

			}
			@Override
			public void mouseDown(MouseEvent arg0) {}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
		
		Button buttonSWTBot = new Button(parent, SWT.NONE);
		buttonSWTBot.setText("SWTBot");
		buttonSWTBot.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				try {
				System.out.println("Button SWTBot has been clicked");
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType typ = manager.getLaunchConfigurationType("org.eclipse.swt.program");
				ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(typ);
				ILaunchConfiguration firstJUnitTestConfiguration = configurations[0];
				firstJUnitTestConfiguration.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
				}

			catch (CoreException e) {
				e.printStackTrace();
			}			
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
	}

	@Override
	public void setFocus() {}

}