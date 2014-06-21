package cz.opendata.tenderstats.matchmaker;

/**
 * @author Matej Snoha
 * 
 */
public class BusinessEntity {

	public String name;
	public String url;
	public String place;

	public BusinessEntity(String name, String url, String place) {
		this.name = name;
		this.url = url;
		this.place = place;
	}

	@Override
	public String toString() {
		return url;
	}
}
