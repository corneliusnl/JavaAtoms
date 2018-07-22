//
// Vector Class
//
class Vector
{
	Vector Temp = new Vector(0,0,0);
	double	X,Y,Z;

	//
	// Constructor
	//
	Vector(double X, double Y, double Z)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	//
	// Dot Product
	//
	public double DotProduct(Vertex Vert)
	{
		return Vert.X*X + Vert.Y*Y + Vert.Z*Z;
	}

	//
	// Cross Multiply
	//
	public void CrossMultiply(Vector Vect)
	{
		Temp.X	= Y*Vect.Z - Z*Vect.Y;
		Temp.Y	= Z*Vect.X - X*Vect.Z;
		Temp.Z	= X*Vect.Y - Y*Vect.X;
		X = Temp.X;
		Y = Temp.Y;
		Z = Temp.Z;
	}

	//
	// Normalize
	//
	public void Normalize()
	{
		double Length = Math.sqrt(X*X + Y*Y + Z*Z);
		X = X/Length;
		Y = Y/Length;
		Z = Z/Length;
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

//
// Polygon Class
//
class FlatPolygon
{
	Vertex	VertList[];
	int	xList[], yList[];
	int	NumVertices;
	
	//
	// Constructor
	//
	FlatPolygon(int NumVertices)
	{
		this.NumVertices = NumVertices;
		VertList	= new Vertex[NumVertices];
		xList		= new int[NumVertices];
		yList		= new int[NumVertices];
	}

	//
	// Transform
	//
	public void Transform(Matrix Mat)
	{
		for(int i = 0; i < NumVertices; i++)
			VertList[i].Transform(Mat);
	}

	//
	// Project
	//
	public void Project(int Dist)
	{
		for(int i = 0; i < NumVertices; i++)
			VertList[i].Project(Dist);
	}
	
	//
	// Draw Polygon
	//
	public void Draw(Graphics g, int XOff, int YOff)
	{
		for(int i = 0; i < NumVertices; i++)
		{
			xList[i] = (int)VertList[i].XP + XOff;
			yList[i] = (int)VertList[i].YP + YOff;
		}
		g.fillPolygon(xList, yList, NumVertices);
	}
}

//
// Entity Class
//
class Entity
{
	FlatPolygon	Polygons[];
	int		NumPolygons;

	//
	// Constructor
	//
	Entity(int NumPolygons)
	{
		this.NumPolygons = NumPolygons;
		Polygons = new FlatPolygon[NumPolygons];
	}

	//
	// Transform
	//
	public void Transform(Matrix Mat)
	{
		for(int i = 0; i < NumPolygons; i++)
			Polygons[i].Transform(Mat);
	}
	//
	// Project
	//
	public void Project(int Distance)
	{
		for(int i = 0; i < NumPolygons; i++)
			Polygons[i].Project(Distance);
	}

	//
	// Draw Polygon
	//
	public void Draw(Graphics g, int XOff, int YOff)
	{
		for(int i = 0; i < NumPolygons; i++)
			Polygons[i].Draw(g, XOff, YOff);
	}
}
