package qsmart.java.applications.gsm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import qsmart.java.applications.gsm.model.FINESCEComponent;
import qsmart.java.applications.gsm.security.ApiUser;

@Component
public class ComponentDataProcessor {

	/** Default Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(ComponentDataProcessor.class);
	
	class Substitution{
		String original;
		String change;
		boolean forth = true;
		boolean back = true;
		public Substitution (String o, String c){
			original = o;
			change = c;
		}
		public Substitution (String o, String c, boolean f, boolean b){
			original = o;
			change = c;
			forth = f;
			back = b;
		}
	}
	
	private ArrayList<Substitution> dataChanges;
	
	public ComponentDataProcessor(){
		dataChanges = new ArrayList<Substitution>();
		dataChanges.add(new Substitution(",", "." , true, false));
		dataChanges.add(new Substitution(";", ","));
//		dataChanges.add(new Substitution("\0", ";"));
		dataChanges.add(new Substitution("\n", ";"));
	}
	
	public String processDataToRepresent(String original){
		if (original == null || original.length() == 0)
			return original;
		String result = new String(original);
		for (Substitution s:dataChanges){
			if (s.forth)
				result = result.replace(s.original,s.change);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public String processDataToStore(String original){
		if (original == null || original.length() == 0)
			return original;
		String result = new String(original);
		ArrayList<Substitution> reverse = (ArrayList<Substitution>) dataChanges.clone();
		Collections.reverse(reverse);
		for (Substitution s:reverse){
			if (s.back)
				result = result.replace(s.change,s.original);
		}
		return result;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String,List> addRights(Map<String,List> components, ApiUser user){
		if (user == null){
			return null;
		}
		Set <Entry<String, List>> setSides = components.entrySet();
		for (Iterator<Entry<String, List>> iterMap= setSides.iterator();iterMap.hasNext();) {
			Entry<String, List> entrySide = iterMap.next();
//		}
//		for (Entry<String, List> entrySide : setSides){
			FINESCEComponent.ComponentSide side = FINESCEComponent.ComponentSide.valueOf(entrySide.getKey());
			LOG.debug("Side: " + side.toString());
			List<Map> list = (List<Map>) entrySide.getValue();
			for (Iterator<Map> iter=list.iterator();iter.hasNext();) {
				Map m = iter.next();
//			for (Map m : list ){
				String name = (String) m.get("component");
				String rw = user.getRw(side, name);
				if (rw.length() == 0){
					iter.remove();
//					list.remove(m);
					if (list.isEmpty()){
						iterMap.remove();
//						setSides.remove(entrySide);
					}
				}else{
					m.put("access", rw);
				}
			}
		}
		
		//Yes, I know the original map is already modified. But for making one-liners is easier to return it anyway
		return components;
	}
	
	

}
