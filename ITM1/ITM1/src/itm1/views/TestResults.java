package itm1.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestResults extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "itm1.views.TestResults";

	private TableViewer viewer;


	/**
	 * The constructor.
	 */
	public TestResults() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "itm1.viewer");
		makeActions();
		
	}
	
	private void makeActions() {
		Display display = null;
		Shell shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		
		rowLayout.marginTop = 10;
        rowLayout.marginBottom = 10;
        rowLayout.marginLeft = 5;
        rowLayout.marginRight = 5;
        rowLayout.spacing = 10;
        shell.setLayout(rowLayout);
		
        Button OpenJUnitResults = new Button(shell, SWT.PUSH);
        OpenJUnitResults.setText("Open JUnit Results");
        OpenJUnitResults.setLayoutData(new RowData(80, 30));
        OpenJUnitResults.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				System.out.println("Button OpenJUnitResults has been clicked");
				new ShowFileDialog().run();
				
			}
			@Override
			public void mouseDown(MouseEvent arg0) {}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
        

        Button OpenSWTResults = new Button(shell, SWT.PUSH);
        OpenSWTResults.setText("Open JUnit Results");
        OpenSWTResults.setLayoutData(new RowData(80, 30));
        OpenSWTResults.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				System.out.println("Button OpenSWTResults has been clicked");
				new ShowFileDialog().run();
			}
			@Override
			public void mouseDown(MouseEvent arg0) {}
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {}
		});
        
        
        shell.setText("RowLayout");
        shell.pack();
        shell.open();
		
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
              display.sleep();
            }
        }
	}
	
	@SuppressWarnings("unused")
    public static void main(String[] args) {
        
        Display display = new Display();
        display.dispose();
    }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}