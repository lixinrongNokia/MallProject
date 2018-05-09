package iliker.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SerializableList implements Serializable {
	private List<Cartitem> list = new ArrayList<>();

	public void setList(List<Cartitem> list) {
		this.list = list;
	}

	public List<Cartitem> getList() {
		return list;
	}

}
