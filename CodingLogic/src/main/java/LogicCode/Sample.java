package LogicCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Sample {

//	public static void main(String[] args) {
//		int a=20,b=30;
//		a=a+b;
//		b=a-b;
//		a=a-b;
//		System.out.println(" a = "+ a +"b = "+ b);
//
//	}

//	public static void main(String[] args) {
//		int a=20,b=30;
//		a=a*b;
//		b=a/b;
//		a=a/b;
//		System.out.println(" a = "+ a +"  b = "+ b);
//
//	}
//32 Swapping of string withoout using 3rd variable
//	public static void main(String[] args) {
//		String a="john" , b = "Panu";
//		a=a+b;   // a+b = johnPanu
//		b=a.substring(0,a.length()-b.length()); //(0, 8-4) = john
//		a=a.substring(b.length()); 
//		
//		
//		System.out.println(" a = "+ a +"  b = "+ b);
//
//	}

	
//	public static void main(String[] args) {
//		
//		List<String> list= new ArrayList<String>();	
//		
//		list.add("John");
//		list.add("Kutto");
//		list.add("Eli");
//		list.add("Zerah");
//		
//		Iterator<String> it = list.iterator();
//		
//		while(it.hasNext()) {
//			System.out.print(" | List of String :"+ it.next());
//			
//		}
//	}
	
//	public static void main(String[] args) {
//		String a = "This is a java class for java programming";
//
//		String st[] = a.split(" ");
//
//		HashMap<String, Integer> map = new HashMap<String, Integer>();
//		for (int i = 0; i < st.length; i++) {
//			if (map.containsKey(st[i])) {
//				int count = map.get(st[i]);
//				map.put(st[i], count + 1);
//			} else {
//				map.put(st[i], 1);
//			}
//
//		}
//		System.out.println(map);
//
//	}

	
//	public static void main(String[] args) {
//		// Reversal of string
//		
//		String str="Johnnie";
//		String nstr="";
//		for(int i=0;i<str.length();i++) {
//			char ch=str.charAt(i);
//			nstr=ch+nstr;
//			
//		}
//		System.out.println(nstr);
//	} //Done  
//	
//	
	
	
	
	
//	prime no or not
//	public static boolean isPrime(int a) {
//		if(a==0 || a==1)
//			return false;
//		if(a==2)
//			return true;
//		for(int i =2;i<a/2;i++) {
//			if(a%i==0) {
//				return false;
//			}
//		}
//		return true;
//	}
//	public static void main(String[] args) {
//		
//		Scanner sc = new Scanner(System.in);
//		
//		int a=sc.nextInt();
//		
//		if(isPrime(a)) {
//			System.out.println("Pime no");
//		}else {
//			System.out.println("Not Prime");
//		}
//			
//	}
	
	
//	//count no of digits i a number
//	public static void main(String[] args) {
//		
//		int  n=-1233454689;
//		
//		if(n==0)
//			System.out.println("1");
////		if(n<0)
////			n=-n;
//		int count = 0;
//		while(n!=0) {
//			n=n/10;
//			count++;
//		}
//		System.out.println(count );
//	}
	
//repeating digit in Integer/ counting d	
//public static void main(String[] args) {
//	
//	
//	int n =996923618;
//	int d=6;
//	if(n==0) {
//		System.out.println("1");
//	}
//	if(n<0) {
//		n=-n;
//
//	}
//	int count=0;
//	while(n!=0) {
//		int digit=n%10;
//		if(digit==d) {
//			count++;
//		}
//		n=n/10;
//	}
//	System.out.println("Cunt : " + count);
//}	

	
//okay
	
	
	public static void refrence(Object x ,Object y) {
		if(x==y) {
			System.out.println("Both pointing same refrence");
		}else {
			System.out.println("not pointing same refrence");
		}
	}
	public static void main(String[] args) {
		
		
		String S1="java";
		String S2=S1;
		refrence(S1, S2);
		S1=S1+" programming";
		refrence(S1, S2);
		welocme thanku kutto yessssssssssssssssss
		System.out.println();
		
	}
	
	
	
	
	
	
}














