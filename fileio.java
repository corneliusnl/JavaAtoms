import java.io.*;
import java.net.*;
import java.util.*;

//
// AtomFile Class
//
class AtomFile
{
	public String	AtomicNumber;
	public String	Name;
	public String	Symbol;
	public String	State;
	public String	Density;
	public String	ElectroNegativity;
	public String	Discovered;
	public String	MeltingPt;
	public String	BoilingPt;
	public String	MolarMass;
	public String	CommonIon;
	public String	OtherIon;
	public String	Comment;
}

//
// DataBase Class
//
class DataBase
{
	DataInputStream	dis;
	public AtomFile AtomStruct[] = new AtomFile[300];
	public int	NumAtoms = 0;
	
	//
	// Constructor 1 of 2
	//
	DataBase(String Filename)
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
	DataBase(URL Location)
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
	// Compile Information
	//
	public void CompileList()
	{
		StringTokenizer	st;
		String buff, temp;
		
		try
		{
read:			while((buff = dis.readLine()) != null)
			{
				if(buff.indexOf(";") != -1)
					continue read;
            
                        st = new StringTokenizer(buff, "|");
				AtomStruct[NumAtoms] = new AtomFile();
				AtomStruct[NumAtoms].AtomicNumber	= st.nextToken();
				AtomStruct[NumAtoms].Name		= st.nextToken();
				AtomStruct[NumAtoms].Symbol		= st.nextToken();
				AtomStruct[NumAtoms].State		= st.nextToken();
				AtomStruct[NumAtoms].Density		= st.nextToken();
				AtomStruct[NumAtoms].ElectroNegativity	= st.nextToken();
				AtomStruct[NumAtoms].Discovered		= st.nextToken();
				AtomStruct[NumAtoms].MeltingPt		= st.nextToken();
				AtomStruct[NumAtoms].BoilingPt		= st.nextToken();
				AtomStruct[NumAtoms].MolarMass		= st.nextToken();
				AtomStruct[NumAtoms].CommonIon		= st.nextToken();
				AtomStruct[NumAtoms].OtherIon		= st.nextToken();
				AtomStruct[NumAtoms].Comment		= st.nextToken();
				NumAtoms++;
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
