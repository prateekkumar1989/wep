#include <iostream>
#include <iomanip>
#include <stdlib.h>
using namespace std;

int  C = 90;
unsigned long A,B,D,E,F,G,H,S;
unsigned long limit = 90;

int main()
{
int i;
long j;
int guess, found;
int K[8];
K[3] = 63;
K[4] = 63;
K[5] = 88;
K[6] = 73;
K[7] = 12;


//S=37821; A=1341324; B=314; D=314; E=89234; F=3142; G=13424; H=234524; srandom(S);
//cout << "./rc4 0 8  5000000  0  ";  for (i=3;i<8;i++) cout << setw(3) << 
//(E*random()+F*random()+G*random()+H*random()+D*random()+A*random()+B)%C << " "; cout << "> A00.data" <<  endl;


for (S=0; S<=5000000; ++S) 
{
	for (A=0; A<=limit; ++A) 
	{
		for (B=0; B<=limit; ++B) 
		{
			for (D=0; D<=limit; ++D) 
			{	
				for (E=0; E<=limit; ++E) 
				{
					for (F=0; F<=limit; ++F) 
					{
						for (G=0; G<=limit; ++G) 
						{	
							for (H=0; H<=limit; ++H) 
							{
								srandom(S);
								found = 0;
								for (i=3;i<8;i++)
								{
									guess = (E*random()+F*random()+G*random()+H*random()+D*random()+A*random()+B)%C;
									if ( guess != K[i] ) { found = 0; break; } 
								}
								if(found) { cout<<endl<<" S="<<S<<" A="<<A<<" B="<<B<<" D="<<D<<" E="<<E<<" F="<<F<<" G="<<G<<" H="<<H<<endl; return 0;}
							}
						}
					}
				}
			}
		}
	}
	cout<<endl<<"Gone past S = "<<S;
}

cout<<endl<<"Completed";
return 0;
}
