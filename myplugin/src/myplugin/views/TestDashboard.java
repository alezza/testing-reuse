package myplugin.views;


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
				//
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
				System.out.println("Button SWTBot has been clicked");
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