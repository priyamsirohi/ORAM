package ringoram;

public class PositionMap {

		private int[] map;
		

	public PositionMap(int N){
		map = new int[N+1];
	}
	
	public int getMap(int index){
		return map[index];
	}
	
	public void setMap(int index, int val){
		map[index] = val;
	}
	
	
}