//------------------------------------------------------------------------------
// ScienceProject96 3D Real-time Atom Viewer
// Copyright 1996, All rights reserved
// Leprechaun Software
// Version:	1.0
// Author:	Jeremiah McCarthy
// Created:	12/23/96
//------------------------------------------------------------------------------
// 1996-1997 Level | Science Fair Entry
//------------------------------------------------------------------------------
import java.awt.*;
import java.net.*;
import java.applet.Applet;

public class ScienceProject96 extends Applet
{
	static boolean		isApplet = true;
	static JavaFrame	WinFrame;
	ViewerCanvas	AtomView;
	DataBase	db;
	Toolkit		toolkit = Toolkit.getDefaultToolkit();
	TextArea	AddInfo;
	DoubleLabel	Number, Name, Symbol, State, Density, 
			Negativity, Discovered,
			Melting, Boiling, Mass, Common, Other;
	Image		Green, Purple, Yellow;
	int		Frame;

	//
	// Constructor
	//
	public  ScienceProject96()
	{
	}

	//
	// Class initialization code
	//
	public void init()
	{
		setBackground(Color.lightGray);

		//
		// Button Bar
		//
		Panel ButtonBar = new Panel();
		ButtonBar.setLayout(new GridLayout(1,3));
		ButtonBar.add(new Button("Select Atom..."));
		ButtonBar.add(new Button("About..."));
		if(!isApplet)
			ButtonBar.add(new Button("Exit"));
		
		//
		// Information Panel
		//
		Panel InfoPanel = new Panel();
		InfoPanel.setLayout(new GridLayout(13,1));
		InfoPanel.add(ButtonBar);
		InfoPanel.add(Number	= new DoubleLabel("Atomic Number:"));
		InfoPanel.add(Name	= new DoubleLabel("Name:"));
		InfoPanel.add(Symbol	= new DoubleLabel("Symbol:"));
		InfoPanel.add(State	= new DoubleLabel("State:"));
		InfoPanel.add(Density	= new DoubleLabel("Density:"));
		InfoPanel.add(Negativity= new DoubleLabel("Electronegativity:"));
		InfoPanel.add(Discovered= new DoubleLabel("Year Discovered:"));
		InfoPanel.add(Melting	= new DoubleLabel("Melting Point:"));
		InfoPanel.add(Boiling	= new DoubleLabel("Boiling Point:"));
		InfoPanel.add(Mass	= new DoubleLabel("Atomic Molar Mass:"));
		InfoPanel.add(Common	= new DoubleLabel("Common Ion:"));
		InfoPanel.add(Other	= new DoubleLabel("Other Ion:"));

		//
		// Text Area Panel
		//
		Panel TextPanel = new Panel();
		TextPanel.setLayout(new BorderLayout());
		TextPanel.add("North", new Label("Y=Electrons P=Neutrons G=Protons"));
		AddInfo = new TextArea(12, 33);
		AddInfo.setEditable(false);
		TextPanel.add("Center", AddInfo);
		
		//
		// 3D Viewing Panel
		//
		Panel ViewPanel = new Panel();
		ViewPanel.setLayout(new GridLayout(2,1));
                ViewPanel.add(AtomView = new ViewerCanvas(this));
        	ViewPanel.add("2", TextPanel);

		//
		// Main Window Panel
		//
		Panel MainPanel = new Panel();
		MainPanel.setLayout(new GridLayout(1,2));
		MainPanel.add(InfoPanel);
		MainPanel.add(ViewPanel);
		add("Center", MainPanel);
		
		if(isApplet == false)
		{
			db	= new DataBase("periodic.db");
			Green	= Toolkit.getDefaultToolkit().getImage("green.gif");
			Purple	= Toolkit.getDefaultToolkit().getImage("purple.gif");
			Yellow	= Toolkit.getDefaultToolkit().getImage("yellow.gif");
		}
		else
		{
			try
			{
				db	= new DataBase(new URL(getDocumentBase(), "periodic.db"));
				Green	= getImage(getCodeBase(), "green.gif");
				Purple	= getImage(getCodeBase(), "purple.gif");
				Yellow	= getImage(getCodeBase(), "yellow.gif");
			}
			catch (MalformedURLException e)
			{
				System.out.println("File I/O Error" + e.toString());
			}
		}
		repaint();
	}

	//
	// For running standalone
	//
	public static void main(String[] argv)
	{
		// Create the frame and launch window frame
		WinFrame = new JavaFrame(new ScienceProject96());
	}

	//
	// Applet Event Handler
	//
	public boolean handleEvent(Event evt)
	{
		switch(evt.id)
		{
			case Event.ACTION_EVENT:
				if("Select Atom...".equals(evt.arg))
				{
					new ChangeAtom(this, db);
					return true;
				}
				else if("Exit".equals(evt.arg))
				{
					WinFrame.dispose();
					System.exit(0);
					return true;
				}
				else if("About...".equals(evt.arg))
				{
					new About();
					return true;
				}
				return false;

			default:
				return super.handleEvent(evt);
		}
	}
}

// ------------------------
// Application Window Frame
// ------------------------
class JavaFrame extends Frame
{
	ScienceProject96 JavaApplet;

	//
	// Constructor
	// 
	public JavaFrame(ScienceProject96 FrameApplet)
	{
		// Window Frame
		setTitle("Java'ed Atoms v1.0 - Leprechaun Software");
		resize(640, 480);
		show();

		Menu fm1= new Menu("File");
		fm1.add(new MenuItem("Select Atom..."));
		fm1.addSeparator();
		fm1.add(new MenuItem("Exit"));

		Menu fm2 = new Menu("Help");
		fm2.add(new MenuItem("About..."));

		MenuBar mb = new MenuBar();
		mb.add(fm1);
		mb.add(fm2);
		setMenuBar(mb);

		// Applet
		JavaApplet = FrameApplet;
		JavaApplet.isApplet = false;
		add("Center", JavaApplet);
		JavaApplet.init();
		pack();
		show();
	}

	//
	// Window Event Handler
	// 
	public boolean handleEvent(Event evt)
	{
		switch(evt.id)
		{
			case Event.WINDOW_DESTROY:
				dispose();
				System.exit(0);
				return true;

			case Event.ACTION_EVENT:
				return JavaApplet.handleEvent(evt);

			default:
				return super.handleEvent(evt);
		}
	}
}

// ------------------------
// Change Atom Window Frame
// ------------------------
class ChangeAtom extends Frame
{
	ScienceProject96	Applet;
	DataBase		db;
	List			AtomList;
	
	//
	// Constructor
	//
	public ChangeAtom(ScienceProject96 JavaApplet, DataBase db)
	{
		// Window Frame
		setTitle("Select Atom...");
		resize(200,300);

		Applet = JavaApplet;
		AtomList = new List(10, false);
	
		this.db = db;
		for(int i = 0; i < db.NumAtoms; i++)
			AtomList.addItem(db.AtomStruct[i].Name);
		
		Panel ButtonBar = new Panel();
		ButtonBar.setLayout(new GridLayout(1,2));
		ButtonBar.add(new Button("Ok"));
		ButtonBar.add(new Button("Cancel"));
		
		add("Center", AtomList);
		add("South", ButtonBar); 
		show();
	}

	//
	// Window Event Handler
	//
	public boolean handleEvent(Event evt)
	{
		switch(evt.id)
		{
			case Event.WINDOW_DESTROY:
				dispose();
				return true;
				
			case Event.ACTION_EVENT:
				if("Cancel".equals(evt.arg))
				{
					dispose();
					return true;
				}
				if("Ok".equals(evt.arg))
				{
					SetInformation();
					dispose();
					return true;
				}
				return false;

			default:
				return super.handleEvent(evt);
		}
	}

	//
	// Set Atom Information
	//
	private void SetInformation()
	{
		String Temp;
		Temp = AtomList.getItem(AtomList.getSelectedIndex());
		for(int i = 0; i < db.NumAtoms; i++)
		{
			if(Temp.equals(db.AtomStruct[i].Name))
			{
				Applet.AddInfo.setText(db.AtomStruct[i].Comment);
				Applet.Number.setLabel(db.AtomStruct[i].AtomicNumber);
				Applet.Name.setLabel(db.AtomStruct[i].Name);
				Applet.Symbol.setLabel(db.AtomStruct[i].Symbol);
				Applet.State.setLabel(db.AtomStruct[i].State);
				if((db.AtomStruct[i].State.equals("Solid")) || db.AtomStruct[i].State.equals("Liquid"))
					Applet.Density.setLabel(db.AtomStruct[i].Density.trim() + "g/cm3");
				else
					Applet.Density.setLabel(db.AtomStruct[i].Density.trim() + "g/L");
				Applet.Negativity.setLabel(db.AtomStruct[i].ElectroNegativity.trim());
				Applet.Discovered.setLabel(db.AtomStruct[i].Discovered);
				Applet.Melting.setLabel(db.AtomStruct[i].MeltingPt.trim() + "*C");
				Applet.Boiling.setLabel(db.AtomStruct[i].BoilingPt.trim() + "*C");
				Applet.Mass.setLabel(db.AtomStruct[i].MolarMass.trim() + "g/mol");
				Applet.Common.setLabel(db.AtomStruct[i].CommonIon);
				Applet.Other.setLabel(db.AtomStruct[i].OtherIon);
				Applet.AtomView.SetAtom(db.AtomStruct[i]);
				break;
			}
		}
	}
}

// --------------------------------
// About Jave'ed Atoms Window Frame
// --------------------------------
class About extends Frame implements Runnable
{
	Thread	AnimThread;
	Image	BitmapImage;
	int	Frame;

	//
	// Constructor
	//
	public About()
	{
		// Window Frame
		setTitle("About Java'ed Atoms...");
		resize(320,200);
		show();
		start();
	}

	//
	// Run
	//
	public void run()
	{
		System.out.println("Run");
		while(true)
		{
			repaint();
			try
			{
				Thread.sleep(200);
			} catch (Exception e) {}
		}
	}

	//
	// Start
	//
	public void start()
	{
		System.out.println("Start");
		if(AnimThread == null)
		{
			AnimThread = new Thread(this);
			AnimThread.start();
		}
	}

	//
	// Stop
	//
	public void stop()
	{
		System.out.println("Stop");
		if(AnimThread != null)
		{
			AnimThread.stop();
			AnimThread = null;
		}
	}
	
	//
	// Paint
	//
	public void paint(Graphics g)
	{
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, 320, 200);

//		if(isApplet == true)
//			BitmapImage = getImage(getCodeBase(), "atom"+Frame+".gif");
//		else
			BitmapImage = Toolkit.getDefaultToolkit().getImage("Atom"+Frame+".gif");

		if(BitmapImage != null)
		{
			g.clipRect(0, 0, 320, 200);
			g.drawImage(BitmapImage, 0, 0, this);
			Frame++;
			if(Frame >= 39)
				Frame = 0;
		}
	}
	
	//
	// Update
	//
	public void update(Graphics g)
	{
		paint(g);
	}
	
	//
	// Window Event Handler
	//
	public boolean handleEvent(Event evt)
	{
		switch(evt.id)
		{
			case Event.WINDOW_DESTROY:
				dispose();
				return true;
		}
		return super.handleEvent(evt);
	}
}

//
// Double Label Class
//
class DoubleLabel extends Panel
{
	Label	Label1, Label2;
	
	//
	// Constructor 1 of 2
	//
	DoubleLabel(String Text1, String Text2)
	{
		setLayout(new GridLayout(1,2));
		add(Label1 = new Label(Text1));
		add(Label2 = new Label(Text2));
	}

	//
	// Constructor 2 of 2
	//
	DoubleLabel(String Text)
	{
		setLayout(new GridLayout(1,2));
		add(Label1 = new Label(Text));
		add(Label2 = new Label());
		show();
	}

	//
	// Set Label
	//
	public void setLabel(String Text)
	{
		Label2.setText(Text);
	}
}
