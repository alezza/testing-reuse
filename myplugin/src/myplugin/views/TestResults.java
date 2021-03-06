package myplugin.views;


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


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class TestResults extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "myplugin.views.TestDashboard";

	private TableViewer viewer;
	private Action startJUnit;
	private Action startSWTBot;
	private Action ViewResults;


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
		viewer.setContentProvider(new TableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "myplugin.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TestResults.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(startJUnit);
		manager.add(new Separator());
		manager.add(startSWTBot);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(startJUnit);
		manager.add(startSWTBot);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(startJUnit);
		manager.add(startSWTBot);
	}

	private void makeActions() {
		startJUnit = new Action() {
			public void run() {
				showMessage("Started JUnit Test");
			}
		};
		startJUnit.setText("Start JUnit Test");
		startJUnit.setToolTipText("Action 1 tooltip");
		startJUnit.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		startSWTBot = new Action() {
			public void run() {
				showMessage("Started SWTBot Test");
			}
		};
		startSWTBot.setText("Start SWTBot");
		startSWTBot.setToolTipText("Action 2 tooltip");
		startSWTBot.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		ViewResults = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ViewResults.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}

//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.FileDialog;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.MenuItem;
//import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.Shell;
//
//public class FileDialogExample {
//  Display d;
//
//  Shell s;
//
//  FileDialogExample() {
//    d = new Display();
//    s = new Shell(d);
//    s.setSize(400, 400);
//    
//    s.setText("A MessageBox Example");
//    //         create the menu system
//    Menu m = new Menu(s, SWT.BAR);
//    // create a file menu and add an exit item
//    final MenuItem file = new MenuItem(m, SWT.CASCADE);
//    file.setText("&File");
//    final Menu filemenu = new Menu(s, SWT.DROP_DOWN);
//    file.setMenu(filemenu);
//    final MenuItem openItem = new MenuItem(filemenu, SWT.PUSH);
//    openItem.setText("&Open\tCTRL+O");
//    openItem.setAccelerator(SWT.CTRL + 'O');
//    final MenuItem saveItem = new MenuItem(filemenu, SWT.PUSH);
//    saveItem.setText("&Save\tCTRL+S");
//    saveItem.setAccelerator(SWT.CTRL + 'S');
//    final MenuItem separator = new MenuItem(filemenu, SWT.SEPARATOR);
//    final MenuItem exitItem = new MenuItem(filemenu, SWT.PUSH);
//    exitItem.setText("E&xit");
//
//    class Open implements SelectionListener {
//      public void widgetSelected(SelectionEvent event) {
//        FileDialog fd = new FileDialog(s, SWT.OPEN);
//        fd.setText("Open");
//        fd.setFilterPath("C:/");
//        String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
//        fd.setFilterExtensions(filterExt);
//        String selected = fd.open();
//        System.out.println(selected);
//      }
//
//      public void widgetDefaultSelected(SelectionEvent event) {
//      }
//    }
//
//    class Save implements SelectionListener {
//      public void widgetSelected(SelectionEvent event) {
//        FileDialog fd = new FileDialog(s, SWT.SAVE);
//        fd.setText("Save");
//        fd.setFilterPath("C:/");
//        String[] filterExt = { "*.txt", "*.doc", ".rtf", "*.*" };
//        fd.setFilterExtensions(filterExt);
//        String selected = fd.open();
//        System.out.println(selected);
//      }
//
//      public void widgetDefaultSelected(SelectionEvent event) {
//      }
//    }
//    openItem.addSelectionListener(new Open());
//    saveItem.addSelectionListener(new Save());
//
//    exitItem.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent e) {
//        MessageBox messageBox = new MessageBox(s, SWT.ICON_QUESTION
//            | SWT.YES | SWT.NO);
//        messageBox.setMessage("Do you really want to exit?");
//        messageBox.setText("Exiting Application");
//        int response = messageBox.open();
//        if (response == SWT.YES)
//          System.exit(0);
//      }
//    });
//    s.setMenuBar(m);
//    s.open();
//
//    while (!s.isDisposed()) {
//      if (!d.readAndDispatch())
//        d.sleep();
//    }
//    d.dispose();
//  }
//
//  public static void main(String[] argv) {
//    new FileDialogExample();
//  }