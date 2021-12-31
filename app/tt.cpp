#include<iostream>
using namespace std;

int main()
{
    int n,t1,t2,t3;
    string h;
    cin>>n>>t1>>t2>>t3>>h;
    int tt;cin>>tt;
    int num=1;
    while(tt--)
    {
        int t,a,b,c,d;
        cin>>t>>a>>b>>c>>d;
        if(d==0)d=t+b;
        cout<<h<<" "<<a<<" "<<"OFR"<<" "<<num++<<d<<endl;
        return 0;
    }
}