package mc.everyos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BidirectionalList<Type> {

	private List<Type> negative = new ArrayList<>();
	private List<Type> positive = new ArrayList<>();
	
	//TODO: Proper bounds
	
	public Type get(int i) {
		if (!indexIsInBounds(i)) {
			return null;
		}
		if (i < 0) {
			return negative.get(-i - 1);
		} else {
			return positive.get(i);
		}
	}
	
	public void set(int i, Type data) {
		if (i < 0) {
			while (negative.size() <= -i - 1) {
				negative.add(null);
			}
			negative.set(-i - 1, data);
		} else {
			while (positive.size() <= i) {
				positive.add(null);
			}
			positive.set(i, data);
		}
	}
	
	public Type getOrSet(int i, Supplier<Type> dataSupplier) {
		Type data = get(i);
		if (data == null) {
			data = dataSupplier.get();
			set(i, data);
		}
		
		return data;
	}
	
	private boolean indexIsInBounds(int index) {
		return
			index < positive.size() &&
			index >= -negative.size();
	}
	
}
