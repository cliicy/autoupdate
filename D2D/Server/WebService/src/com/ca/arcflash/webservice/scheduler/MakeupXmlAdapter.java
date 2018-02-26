package com.ca.arcflash.webservice.scheduler;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MakeupXmlAdapter extends XmlAdapter<MakeupItem[], Map<Long, ConfilctData>> {

	@Override
	public Map<Long, ConfilctData> unmarshal(MakeupItem[] items) throws Exception {
		Map<Long, ConfilctData> r = new HashMap<Long, ConfilctData>();
		for (MakeupItem entry : items) {
			r.put(entry.time, entry.data);
		}
		return r;
	}

	@Override
	public MakeupItem[] marshal(Map<Long, ConfilctData> map) throws Exception {
		MakeupItem[] makeupItems = new MakeupItem[map.size()];
		int i = 0;
		for (Map.Entry<Long, ConfilctData> entry : map.entrySet())
			makeupItems[i++] = new MakeupItem(entry.getKey(), entry.getValue());

		return makeupItems;
	}

}
