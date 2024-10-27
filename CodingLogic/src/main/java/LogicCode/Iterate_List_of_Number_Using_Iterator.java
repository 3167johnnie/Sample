package LogicCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//3.Iterate list of number using iterator

public class Iterate_List_of_Number_Using_Iterator {
	
	
		public static void main(String args[]) {
			List<Integer> list = new ArrayList<Integer>();
			list.add(1);
			list.add(12);
			list.add(67);
			list.add(24);
			list.add(96);
			
			Iterator<Integer> it = list.iterator();
			
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}

}