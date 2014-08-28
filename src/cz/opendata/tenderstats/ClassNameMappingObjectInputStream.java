package cz.opendata.tenderstats;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class ClassNameMappingObjectInputStream extends ObjectInputStream {

	@SuppressWarnings("rawtypes")
	public static Map<String, Class> classNameMapping = initclassNameMapping();

	@SuppressWarnings("rawtypes")
	private static Map<String, Class> initclassNameMapping() {
		Map<String, Class> res = new HashMap<String, Class>();
		res.put("info.snoha.matej.contractsnearme.Geocoder", cz.opendata.tenderstats.Geocoder.class);
		res.put("info.snoha.matej.contractsnearme.Geocoder$Position", cz.opendata.tenderstats.Geocoder.Position.class);
		return Collections.unmodifiableMap(res);
	}

	public ClassNameMappingObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	protected ClassNameMappingObjectInputStream() throws IOException, SecurityException {
		super();
	}

	@Override
	protected java.io.ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
		ObjectStreamClass desc = super.readClassDescriptor();
		if (classNameMapping.containsKey(desc.getName())) {
			return ObjectStreamClass.lookup(classNameMapping.get(desc.getName()));
		}
		return desc;
	}
}
