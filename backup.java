import java.awt.*;

//
// Viewer Canvas
//
class ViewerCanvas extends Canvas
{
	Image		BufferImage;
	Graphics	BufferGraphics;
	Vertex		v1,v2,v3,v4,v5,v6,v7,v8;
	Entity		Cube;
	Matrix		M;
	int X, Y, OldX, OldY;
	
	//
	// Constructor
	//
	public ViewerCanvas()
	{
		v1 = new Vertex(-20,20,-20);
		v2 = new Vertex(20,20,-20);
		v3 = new Vertex(20,-20,-20);
		v4 = new Vertex(-20,-20,-20);
		v5 = new Vertex(-20,20,20);
		v6 = new Vertex(20,20,20);
		v7 = new Vertex(20,-20,20);
		v8 = new Vertex(-20,-20,20);
		
		Cube = new Entity(6);
		M = new Matrix();
		
		Cube.Polygons[0] = new FlatPolygon(4);
		Cube.Polygons[0].VertList[0] = v1;
		Cube.Polygons[0].VertList[1] = v2;
		Cube.Polygons[0].VertList[2] = v6;
		Cube.Polygons[0].VertList[3] = v5;
		
		Cube.Polygons[1] = new FlatPolygon(4);
		Cube.Polygons[1].VertList[0] = v4;
		Cube.Polygons[1].VertList[1] = v3;
		Cube.Polygons[1].VertList[2] = v2;
		Cube.Polygons[1].VertList[3] = v1;

		Cube.Polygons[2] = new FlatPolygon(4);
		Cube.Polygons[2].VertList[0] = v8;
		Cube.Polygons[2].VertList[1] = v7;
		Cube.Polygons[2].VertList[2] = v3;
		Cube.Polygons[2].VertList[3] = v4;
		
		Cube.Polygons[3] = new FlatPolygon(4);
		Cube.Polygons[3].VertList[0] = v5;
		Cube.Polygons[3].VertList[1] = v6;
		Cube.Polygons[3].VertList[2] = v7;
		Cube.Polygons[3].VertList[3] = v8;
		
		Cube.Polygons[4] = new FlatPolygon(4);
		Cube.Polygons[4].VertList[0] = v5;
		Cube.Polygons[4].VertList[1] = v8;
		Cube.Polygons[4].VertList[2] = v4;
		Cube.Polygons[4].VertList[3] = v1;

		Cube.Polygons[5] = new FlatPolygon(4);
		Cube.Polygons[5].VertList[0] = v7;
		Cube.Polygons[5].VertList[1] = v6;
		Cube.Polygons[5].VertList[2] = v2;
		Cube.Polygons[5].VertList[3] = v3;
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
//		setBackground(Color.black);
	}
	
	//
	// Paint
	//
	public void paint(Graphics g)
	{
		if(BufferImage == null)
		{
			g.clearRect(0, 0, size().width, size().height);
			setup();
		}
		
		BufferGraphics.setColor(Color.black);
	        BufferGraphics.fillRect(0, 0, size().width, size().height);
		BufferGraphics.setColor(new Color(128,0,0));
//		M.Scale(0.23f,0.23f,0.23f);
//		M.Translate(1,1,1);
		M.RotateX((OldY-Y) * 0.03f);
		M.RotateY((OldX-X) * 0.03f);
		Cube.Transform(M);
		Cube.Project(300);
	
		Cube.Draw(BufferGraphics, size().width/2, size().height/2);
		g.drawImage(BufferImage, 0, 0, this);
		OldX = X;
		OldY = Y;
	}

	public boolean mouseDrag(Event evt, int X, int Y)
	{
		this.Y = Y;
		this.X = X;
		repaint();
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
		double val = (1-ZP/Distance);
		XP /= val;
		YP /= val;
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
		Temp[0][0] = ScaleX;
		Temp[1][1] = ScaleY;
		Temp[2][2] = ScaleZ;
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
