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

}
