import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import java.applet.Applet;

//
// Viewer Canvas
//
class ViewerCanvas extends Canvas implements Runnable
{
        ScienceProject96        Applet;
	Image		BufferImage;
	Graphics	BufferGraphics;
	Nucleus		Center;
	Electron	Outer;
	boolean		Rotate = false;
	Thread		ViewThread;
	int		X, Y, OldX, OldY, XRot, YRot;
	
	//
	// Constructor
	//
        public ViewerCanvas(ScienceProject96 JavaApplet)
	{
                Applet = JavaApplet;
                Center  = new Nucleus(Applet);
		Outer	= new Electron(Applet);
		start();
	}

	//
	// Start Thread
	//
	public void start()
	{
		ViewThread = new Thread(this);
		ViewThread.start();
	}

	//
	// Stop Thread
	//
	public void stop()
	{
		if(ViewThread != null)
			ViewThread.stop();
		ViewThread = null;
	}
	
	//
	// Run
	//
	public void run()
	{
		while(true)
		{
			repaint();
			try { Thread.sleep(100); } catch (InterruptedException e) {}
		}
	}
	
	//
	// Setup
	//
	public void setup()
	{
		BufferImage	= createImage(size().width, size().height);
		BufferGraphics	= BufferImage.getGraphics();
		BufferGraphics.setColor(Color.lightGray);
	        BufferGraphics.fill3DRect(0, 0, size().width, size().height, false);
	}
	
	//
	// Paint
	//
	public void paint(Graphics g)
	{
		int test = 0;
		
		if(BufferImage == null)
		{
			g.clearRect(0, 0, size().width, size().height);
			setup();
		}
		
		BufferGraphics.setColor(Color.black);
	        BufferGraphics.fillRect(0, 0, size().width, size().height);
	
		XRot += (OldY - Y);
		YRot += (OldX - X);
		
		Outer.Animate();
		if(Rotate)
			Outer.Rotate(XRot*0.003, YRot*0.003);
		Outer.DrawBack(BufferGraphics, size().width/2, size().height/2, this);
		
		Center.Mat = new Matrix();
		Center.Mat.Scale(1.0f, 1.0f, 1.0f);
		if(Rotate)
		{
			Center.Mat.RotateX(XRot * 0.03f);
			Center.Mat.RotateY(YRot * 0.03f);
		}
		Center.Mat.Translate(0, 0, 2000.0f);
		Center.Render(BufferGraphics, size().width/2, size().height/2, this);
		
		Outer.DrawFront(BufferGraphics, size().width/2, size().height/2, this);
		
		g.drawImage(BufferImage, 0, 0, this);
		OldX = X;
		OldY = Y;
	}

	//
	// Set Atom
	//
	public void SetAtom(AtomFile atom)
	{
		Center.NumNeutrons	= new Double(atom.MolarMass).intValue() - 
					new Double(atom.AtomicNumber).intValue();
		Center.NumProtons	= new Double(atom.AtomicNumber).intValue();
		Outer.NumElectrons	= new Double(atom.AtomicNumber).intValue();
		Outer.Setup();
		repaint();
	}

	//
	// Mouse Button Down
	//
	public boolean mouseDown(Event evt, int X, int Y)
	{
		Rotate = true;
		stop();
		return true;
	}
	
	//
	// Mouse Drag
	//
	public boolean mouseDrag(Event evt, int X, int Y)
	{
		this.Y = Y;
		this.X = X;
		repaint();
		return true;
	}

	//
	// Mouse Button Up
	//
	public boolean mouseUp(Event evt, int X, int Y)
	{
		Rotate = false;
		start();
		return true;
	}
	
	//
	// Update
	//
	public void update(Graphics g)
	{
		paint(g);
	}
}

//
// Nucleus Class
//
class Nucleus
{
        ScienceProject96        Applet;
        PtDataBase              db;
	Matrix			Mat;
	int			NumNeutrons, NumProtons;
	
	//
	// Constructor 1 of 1
	//
        Nucleus(ScienceProject96 JavaApplet)
	{
                Applet = JavaApplet;

                if(Applet.isApplet == false)
		{
                        db      = new PtDataBase("nucleus.db");
		}
		else
		{
			try
			{
                                db = new PtDataBase(new URL(Applet.getDocumentBase(), "nucleus.db"));
			}
			catch (MalformedURLException e)
			{
				System.out.println("Error, Unable to locate file.");
			}
		}

		NumNeutrons = NumProtons = 0;
	}

	//
	// Transform
	//
	public void Transform(Matrix Mat)
	{
		for(int i = 0; i < (NumNeutrons + NumProtons); i++)
			db.Points[i].Transform(Mat);
	}

	//
	// Project
	//
	public void Project(int Distance)
	{
		for(int i = 0; i < (NumNeutrons + NumProtons); i++)
			db.Points[i].Project(Distance);
	}

	//
	// Render
	//
	public void Render(Graphics g, int XOff, int YOff, ImageObserver ImgObs)
	{
		int test = 0;
		for(int i = 0; i < (NumNeutrons + NumProtons); i++)
		{
			db.Points[i].Transform(Mat);
			db.Points[i].Project(100);

			if(test == 1)
			{
				g.drawImage(Applet.Green, 
					(int)db.Points[i].XP + XOff, 
					(int)db.Points[i].YP + YOff, ImgObs);
			}
			else
			{
				g.drawImage(Applet.Purple, 
					(int)db.Points[i].XP + XOff, 
					(int)db.Points[i].YP + YOff, ImgObs);
			}
			test ^= 1;
		}
	}
}

//
// Electron Class
//
class Electron
{
	ScienceProject96	Applet;
	Matrix	Mat[]		= new Matrix[600];
	Vertex	Electrons[]	= new Vertex[600];
	double	RotX[]		= new double[600];
	double	RotY[]		= new double[600];
	double	RotZ[]		= new double[600];
	int	NumElectrons;

	//
	// Constructor 1 of 1
	//
	Electron(ScienceProject96 JavaApplet)
	{
		Applet = JavaApplet;
		for(int i = 0; i < 600; i++)
			Electrons[i] = new Vertex(-700.0f, -700.0f, -700.0f);
	}

	//
	// Setup
	//
	public void Setup()
	{
		for(int i = 0; i < NumElectrons; i++)
		{
			Mat[i] = new Matrix();
			RotX[i] += Math.random()*(2*Math.PI);
			RotY[i] += Math.random()*(2*Math.PI);
			RotZ[i] += Math.random()*(2*Math.PI);
			Mat[i].RotateX(RotX[i]);
			Mat[i].RotateY(RotY[i]);
			Mat[i].RotateZ(RotZ[i]);
			Mat[i].Translate(0.0f, 0.0f, 2000.0f);
			Electrons[i].Transform(Mat[i]);
			Electrons[i].Project(100);
		}
	}

	//
	// Animate
	//
	public void Animate()
	{
		for(int i = 0; i < NumElectrons; i++)
		{
			double Rand;
		
			Mat[i] = new Matrix();
			
			if(RotX[i] >= 2*Math.PI)
				RotX[i] = Math.random();
			if(RotY[i] >= 2*Math.PI)
				RotY[i] = Math.random();
			if(RotZ[i] >= 2*Math.PI)
				RotZ[i] = Math.random();
				
			if((RotX[i] > RotY[i]) && (RotX[i] > RotZ[i]))
				RotX[i] += 0.057f;
			else if((RotY[i] > RotX[i]) && (RotY[i] > RotZ[i]))
				RotY[i] += 0.047f;
			else if((RotZ[i] > RotX[i]) && (RotZ[i] > RotY[i]))
				RotZ[i] += 0.039f;
			else
			{
				Rand = Math.random();
				if(Rand <= 0.3)
					RotX[i] += 0.044;
				else if((Rand > 0.3) && (Rand <= 0.6))
					RotY[i] += 0.057f;
				else
					RotZ[i] += 0.032f;
			}
			
			Mat[i].RotateX(RotX[i]);
			Mat[i].RotateY(RotY[i]);
			Mat[i].RotateZ(RotZ[i]);
			Mat[i].Translate(0.0f, 0.0f, 2000.0f);
			Electrons[i].Transform(Mat[i]);
			Electrons[i].Project(100);
		}
	}
	
	//
	// Rotate X, Y
	//
	public void Rotate(double X, double Y)
	{
		for(int i = 0; i < NumElectrons; i++)
		{
			Mat[i] = new Matrix();
			RotX[i] += X;
			RotY[i] += Y;
			Mat[i].RotateX(RotX[i]);
			Mat[i].RotateY(RotY[i]);
			Mat[i].RotateZ(RotZ[i]);
			Mat[i].Translate(0.0f, 0.0f, 2000.0f);
			Electrons[i].Transform(Mat[i]);
			Electrons[i].Project(100);
		}
	}
	
	//
	// Draw Background Electrons
	//
	public void DrawBack(Graphics g, int XOff, int YOff, ImageObserver ImgObs)
	{
		for(int i = 0; i < NumElectrons; i++)
		{
			if(Electrons[i].ZP > 2000.0f)
			{
				g.drawImage(Applet.Yellow, 
					(int)Electrons[i].XP + XOff, 
					(int)Electrons[i].YP + YOff, ImgObs);
			}
		}
	}

	//
	// Draw Foreground Electrons
	//
	public void DrawFront(Graphics g, int XOff, int YOff, ImageObserver ImgObs)
	{
		for(int i = 0; i < NumElectrons; i++)
		{
			if(Electrons[i].ZP < 2000.0f)
			{
				g.drawImage(Applet.Yellow, 
					(int)Electrons[i].XP + XOff, 
					(int)Electrons[i].YP + YOff, ImgObs);
			}
		}
	}
}

//
// Vertex Class
//
class Vertex
{
	double X, Y, Z;
	double XP, YP, ZP;
	
	//
	// Constructor
	//
	Vertex(double X, double Y, double Z)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	//
	// Transform Coordinates
	//
	public void Transform(Matrix Mat)
	{
		XP = X*Mat.Main[0][0] + Y*Mat.Main[1][0] + Z*Mat.Main[2][0] + Mat.Main[3][0];
		YP = X*Mat.Main[0][1] + Y*Mat.Main[1][1] + Z*Mat.Main[2][1] + Mat.Main[3][1];
		ZP = X*Mat.Main[0][2] + Y*Mat.Main[1][2] + Z*Mat.Main[2][2] + Mat.Main[3][2];
	}

	//
	// Project
	//
	public void Project(int Distance)
	{
		double val = Distance/ZP;
		XP *= val;
		YP *= val;
	}
}

//
// Matrix Class
//
class Matrix
{
	double Main[][];
	double Temp[][];
	
	//
	// Constructor
	//
	Matrix()
	{
		Main		= new double[4][4];
		Temp		= new double[4][4];
		Main		= Identity();
	}

	//
	// Identity Matrix
	//
	private double[][] Identity()
	{
		double Tmp[][] = new double[4][4];
		
		Tmp[0][0] = 1.0f; Tmp[0][1] = 0.0f; Tmp[0][2] = 0.0f; Tmp[0][3] = 0.0f;
		Tmp[1][0] = 0.0f; Tmp[1][1] = 1.0f; Tmp[1][2] = 0.0f; Tmp[1][3] = 0.0f;
		Tmp[2][0] = 0.0f; Tmp[2][1] = 0.0f; Tmp[2][2] = 1.0f; Tmp[2][3] = 0.0f;
		Tmp[3][0] = 0.0f; Tmp[3][1] = 0.0f; Tmp[3][2] = 0.0f; Tmp[3][3] = 1.0f;
		return Tmp;
	}
	
	//
	// Multiply Matrices
	//
	private void Multiply(double TmpMat[][])
	{
		Temp = Identity();
		for(int i = 0; i < 4; i++)
			for(int j = 0; j < 4; j++)
			{
				Temp[i][j] =	Main[i][0] * TmpMat[0][j] +
						Main[i][1] * TmpMat[1][j] +
						Main[i][2] * TmpMat[2][j] +
						Main[i][3] * TmpMat[3][j];
			}
		for(int i = 0; i < 4; i++)
			System.arraycopy(Temp[i], 0, Main[i], 0, 4);
	}

	//
	// Rotate on X-Axis
	//
	public void RotateX(double XAngle)
	{
		double vCos = Math.cos(XAngle);
		double vSin = Math.sin(XAngle);
		Temp = Identity();
		Temp[1][1] = vCos;
		Temp[1][2] = vSin;
		Temp[2][1] = -vSin;
		Temp[2][2] = vCos;
		Multiply(Temp);
	}
	
	//
	// Rotate on Y-Axis
	//
	public void RotateY(double YAngle)
	{
		double vCos = Math.cos(YAngle);
		double vSin = Math.sin(YAngle);
		Temp = Identity();
		Temp[0][0] = vCos;
		Temp[0][2] = -vSin;
		Temp[2][0] = vSin;
		Temp[2][2] = vCos;
		Multiply(Temp);
	}

	//
	// Rotate on Z-Axis
	//
	public void RotateZ(double ZAngle)
	{
		double vCos = Math.cos(ZAngle);
		double vSin = Math.sin(ZAngle);
		Temp = Identity();
		Temp[0][0] = vCos;
		Temp[0][1] = vSin;
		Temp[1][0] = -vSin;
		Temp[1][1] = vCos;
		Multiply(Temp);
	}

	//
	// Scale
	//
	public void Scale(double ScaleX, double ScaleY, double ScaleZ)
	{
		Temp = Identity();
		Temp[0][0] = ScaleZ;
		Temp[1][1] = ScaleY;
		Temp[2][2] = ScaleX;
		Multiply(Temp);
	}
	
	//
	// Translate
	//
	public void Translate(double TransX, double TransY, double TransZ)
	{
		Temp = Identity();
		Temp[3][0] = TransX;
		Temp[3][1] = TransY;
		Temp[3][2] = TransZ;
		Multiply(Temp);
	}
}

//
// Load Point DataBase
//
class PtDataBase
{
	DataInputStream	dis;
	public Vertex	Points[] = new Vertex[600];
	public int	NumPoints = 0;
	
	//
	// Constructor 1 of 2
	//
	PtDataBase(String Filename)
	{
		try
		{
			File file = new File(Filename);
			dis = new DataInputStream(new FileInputStream(file));
			CompileList();
			Terminate();
		}
		catch (IOException e)
		{
			System.out.println("Error, unable to open" + Filename);
		}
	}

	//
	// Constructor 2 of 2
	//
	PtDataBase(URL Location)
	{
		try
		{
			dis = new DataInputStream(Location.openStream());
			CompileList();
			Terminate();
		}
		catch (MalformedURLException e)
		{
			System.out.println("Error, unable to open location" + Location.toString());
		}
		catch (IOException e)
		{
			System.out.println("Error, unable to open location" + Location.toString());
		}
	}

	//
	// Initate
	//
	public void Init()
	{
	}
	
	//
	// Compile Information
	//
	public void CompileList()
	{
		StringTokenizer	st;
		String buff;
		
		try
		{
read:			while((buff = dis.readLine()) != null)
			{
				if(buff.indexOf(";") != -1)
					continue read;

				st = new StringTokenizer(buff);
				Points[NumPoints]	= new Vertex(0.0f,0.0f,0.0f);
				Points[NumPoints].X	= new Double(st.nextToken()).doubleValue();
				Points[NumPoints].Y	= new Double(st.nextToken()).doubleValue();
				Points[NumPoints].Z	= new Double(st.nextToken()).doubleValue();
				NumPoints++;
			}
		}
		catch (IOException e)
		{
			System.out.println("Error parsing line");
		}
	}
	
	//
	// Cleanup
	//
	public void Terminate()
	{
		try
		{
			dis.close();
		}
		catch (IOException e)
		{
			System.out.println("Error, closing file.");
		}
	}
}
