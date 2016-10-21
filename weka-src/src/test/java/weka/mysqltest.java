package weka;

import java.util.ArrayList;
import java.util.Enumeration;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.supervised.instance.Resample;

public class mysqltest {
	public static void main(String[] args) {
		try {
			InstanceQuery query= new InstanceQuery();
			query.setDebug(true);
			query.setUsername("root");
			query.setPassword("root");
			query.setDatabaseURL("jdbc:mysql://localhost:3306/world");
			String sql="SELECT c.ID,c.District,c.CountryCode,c.Population FROM city c ";
			query.setQuery(sql);
			Instances data=query.retrieveInstances();
			
			//
			ArrayList<Attribute> atts = new ArrayList<Attribute>();
			Attribute attribute1 = new Attribute("ID");
			Attribute attribute2 = new Attribute("District");
			Attribute attribute3 = new Attribute("CountryCode");
			Attribute attribute4 = new Attribute("Population");
			atts.add(attribute1);
			atts.add(attribute2);
			atts.add(attribute3);
			atts.add(attribute4);
			Instances dd = new Instances("data", atts, 0);
			//
			Instance inst = new DenseInstance(4); 
			// Set instance's values for the attributes "length", "weight", and "position"
			inst.setValue(attribute1, 5.3); 
			inst.setValue(attribute2, 300); 
			inst.setValue(attribute3, 11);
			inst.setValue(attribute4, 236); 
			// Set instance's dataset to be the dataset "race" 
			inst.setDataset(dd); 
			dd.add(inst);
			System.out.println(dd.toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
