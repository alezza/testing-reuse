package myplugin.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class TableContentProvider implements IStructuredContentProvider {
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	public void dispose() {
	}
	public Object[] getElements(Object parent) {
		return new String[] {  "JUnit Tests", "SWTBot Tests" };
	}
	ILaunchManager manager = ((Object) DebugPlugin.getDefault()).getLaunchManager();
	
	ILaunchConfigurationType type = manager.getLaunchConfigurationType("ID_APPLICATION");
	
	ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
	
	for (int i = 0; i < configurations.length; i++) 
	{
		ILaunchConfiguration configuration = configurations[i];
		if (configuration.getName().equals("ConfigurationName")) {
			configuration.delete();
			break;
		}	
	}
	
	ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "ConfigurationName");
	
	ILaunchConfiguration configuration = workingCopy.doSave();
	
	DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);

}
