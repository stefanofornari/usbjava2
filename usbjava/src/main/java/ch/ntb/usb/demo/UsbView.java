/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbConfigDescriptor;
import ch.ntb.usb.UsbDevice;
import ch.ntb.usb.UsbDeviceDescriptor;
import ch.ntb.usb.UsbEndpointDescriptor;
import ch.ntb.usb.UsbInterfaceDescriptor;
import ch.ntb.usb.demo.TestApp;
import ch.ntb.usb.demo.TestDevice;
import ch.ntb.usb.demo.AbstractDeviceInfo.TransferMode;

public class UsbView extends JFrame {

	private static final long serialVersionUID = 4693554326612734263L;

	private static final int APP_WIDTH = 600, APP_HIGHT = 800;

	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu commandsMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem updateMenuItem = null;
	JTree usbTree = null;
	private JSplitPane jSplitPane = null;

	private JTextArea jPropertiesArea = null;

	UsbTreeModel treeModel;

	JPopupMenu testAppPopup;

	protected JPopupMenu endpointPopup;

	/**
	 * This is the default constructor
	 */
	public UsbView() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setSize(APP_WIDTH, APP_HIGHT);
		this.setContentPane(getJContentPane());
		this.setTitle("USB View");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (commandsMenu == null) {
			commandsMenu = new JMenu();
			commandsMenu.setText("Commands");
			commandsMenu.add(getUpdateMenuItem());
			commandsMenu.add(getExitMenuItem());
		}
		return commandsMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getUpdateMenuItem() {
		if (updateMenuItem == null) {
			updateMenuItem = new JMenuItem();
			updateMenuItem.setText("Update");
			updateMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F5, 0, true));
			updateMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// open bus
							LibusbJava.usb_init();
							LibusbJava.usb_find_busses();
							LibusbJava.usb_find_devices();

							UsbBus bus = LibusbJava.usb_get_busses();
							if (bus != null) {
								treeModel.fireTreeStructureChanged(bus);
								expandAll(usbTree);
							}
						}
					});
		}
		return updateMenuItem;
	}

	/**
	 * This method initializes usbTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getUsbTree() {
		if (usbTree == null) {
			// open bus
			                 LibusbJava.usb_init();
                    LibusbJava.usb_find_busses();
                    LibusbJava.usb_find_devices();

                    UsbBus bus = LibusbJava.usb_get_busses();

			treeModel = new UsbTreeModel(bus, jPropertiesArea);
			usbTree = new JTree(treeModel);
			expandAll(usbTree);
			usbTree.addTreeSelectionListener(treeModel);
			getJTestAppPopup();
			usbTree.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						Object source = e.getSource();
						if (source instanceof JTree) {
							JTree tree = (JTree) source;
							TreePath path = tree.getPathForLocation(e.getX(), e
									.getY());
							if (path != null
									&& (path.getLastPathComponent() instanceof UsbInterfaceDescriptor)) {
								usbTree.setSelectionPath(path);
								testAppPopup.show(tree, e.getX(), e.getY());
							}
						}
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						if (e.isPopupTrigger()) {
							Object source = e.getSource();
							if (source instanceof JTree) {
								JTree tree = (JTree) source;
								TreePath path = tree.getPathForLocation(e
										.getX(), e.getY());
								if (path != null
										&& (path.getLastPathComponent() instanceof UsbInterfaceDescriptor)) {
									usbTree.setSelectionPath(path);
									testAppPopup.show(tree, e.getX(), e.getY());
								}
							}
						}
					}
				}
			});
		}
		return usbTree;
	}

	private void getJTestAppPopup() {
		// Create the popup menu.
		testAppPopup = new JPopupMenu();
		endpointPopup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(
				"Start a test application using this interface");
		menuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				initAndStartTestApp();
			}

			private void initAndStartTestApp() {
				JTree tree = (JTree) testAppPopup.getInvoker();
				TreePath path = tree.getSelectionPath();
				TreePath parent = path;
				UsbEndpointDescriptor[] endpoints = null;
				int altinterface = -1;
				int interface_ = -1;
				int configuration = -1;
				short vendorId = -1;
				short productId = -1;
				while (parent != null
						&& !(parent.getLastPathComponent() instanceof UsbBus)) {
					Object usbObj = parent.getLastPathComponent();
					if (usbObj instanceof UsbInterfaceDescriptor) {
						UsbInterfaceDescriptor usbIntDesc = (UsbInterfaceDescriptor) usbObj;
						endpoints = usbIntDesc.getEndpoints();
						interface_ = usbIntDesc.getInterfaceNumber();
						altinterface = usbIntDesc.getAlternateSetting();
					} else if (usbObj instanceof UsbConfigDescriptor) {
						configuration = ((UsbConfigDescriptor) usbObj)
								.getConfigurationValue();
					} else if (usbObj instanceof UsbDevice) {
						UsbDeviceDescriptor devDesc = ((UsbDevice) usbObj)
								.getDescriptor();
						productId = devDesc.getProductId();
						vendorId = devDesc.getVendorId();
					}
					parent = parent.getParentPath();
				}
				if (parent != null) {
					// present a dialog to select in/out endpoint
					// TODO: present dialog to select in/out endpoint
					UsbEndpointDescriptor[] outEPs = null;
					int nofOutEPs = 0;
					UsbEndpointDescriptor[] inEPs = null;
					int nofInEPs = 0;

					if (endpoints != null) {
						outEPs = new UsbEndpointDescriptor[endpoints.length];
						inEPs = new UsbEndpointDescriptor[endpoints.length];
						for (int i = 0; i < endpoints.length; i++) {
							int epAddr = endpoints[i].getBEndpointAddress() & 0xFF;
							if ((epAddr & 0x80) > 0) {
								// is IN endpoint
								inEPs[nofInEPs++] = endpoints[i];
							} else {
								// is OUT endpoint
								outEPs[nofOutEPs++] = endpoints[i];
							}
						}
					}
					// create a new TestDevice
					TestDevice testDevice = new TestDevice();
					testDevice.setIdProduct(productId);
					testDevice.setIdVendor(vendorId);
					testDevice.setAltinterface(altinterface);
					testDevice.setConfiguration(configuration);
					testDevice.setInterface(interface_);
					if (inEPs != null) {
						for (int i = 0; i < nofInEPs; i++) {
							int type = inEPs[i].getBmAttributes() & 0x03;
							switch (type) {
							case UsbEndpointDescriptor.USB_ENDPOINT_TYPE_BULK:
								testDevice.setInEPBulk(inEPs[i]
										.getBEndpointAddress() & 0xff);
								testDevice.setInMode(TransferMode.Bulk);
								break;
							case UsbEndpointDescriptor.USB_ENDPOINT_TYPE_INTERRUPT:
								testDevice.setInEPInt(inEPs[i]
										.getBEndpointAddress() & 0xff);
								testDevice.setInMode(TransferMode.Interrupt);
								break;
							default:
								break;
							}
						}
					}
					if (outEPs != null) {
						for (int i = 0; i < nofOutEPs; i++) {
							int type = outEPs[i].getBmAttributes() & 0x03;
							switch (type) {
							case UsbEndpointDescriptor.USB_ENDPOINT_TYPE_BULK:
								testDevice.setOutEPBulk(outEPs[i]
										.getBEndpointAddress() & 0xff);
								testDevice.setOutMode(TransferMode.Bulk);
								break;
							case UsbEndpointDescriptor.USB_ENDPOINT_TYPE_INTERRUPT:
								testDevice.setOutEPInt(outEPs[i]
										.getBEndpointAddress() & 0xff);
								testDevice.setOutMode(TransferMode.Interrupt);
								break;
							default:
								break;
							}
						}
					}
					// open a new testApp
					TestApp app = new TestApp(testDevice);
					app.setVisible(true);
				} else {
					System.out.println("error, could not find device node");
					// TODO: handle error
				}
			}
		});
		testAppPopup.add(menuItem);
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setContinuousLayout(true);
			jSplitPane.setDividerLocation(APP_HIGHT / 2);
			jSplitPane
					.setBottomComponent(createScrollPane(getJPropertiesArea()));
			jSplitPane.setTopComponent(createScrollPane(getUsbTree()));
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jPropertiesArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJPropertiesArea() {
		if (jPropertiesArea == null) {
			jPropertiesArea = new JTextArea();
		}
		return jPropertiesArea;
	}

	private JScrollPane createScrollPane(Component view) {
		JScrollPane scrollPane = new JScrollPane(view);
		return scrollPane;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		UsbView application = new UsbView();
		application.setVisible(true);
	}

	void expandAll(JTree tree) {
		for (int row = 0; row < tree.getRowCount(); row++) {
			tree.expandRow(row);
		}
	}
}
