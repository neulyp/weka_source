package weka;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import libsvm.svm;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.experiment.InstanceQuery;
import libsvm.*;
public class oracletest {
	
	public static Date convertDate(long timestmap){
		Date d= new Date(Long.valueOf(timestmap));
		Calendar calendar=Calendar.getInstance();
	    calendar.setTime(d);
	    System.out.println("现在时间是："+new Date());
	    calendar.get(Calendar.HOUR);
	    
	    String year=String.valueOf(calendar.get(Calendar.YEAR));
	    String month=String.valueOf(calendar.get(Calendar.MONTH)+1);
	    String day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	    String hour=String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
	    String min=String.valueOf(calendar.get(Calendar.MINUTE));
	    String week=String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)-1);
	    System.out.println("现在时间是："+year+"年"+month+"月"+day+"日，星期"+week);
	    System.out.println(hour+":"+min);
		return d;
	}
	
	public static void main(String[] args) {
		try {
			InstanceQuery query= new InstanceQuery();
			query.setDebug(true);
			query.setUsername("C##TEST");
			query.setPassword("test");
			query.setDatabaseURL("jdbc:oracle:thin:@127.0.0.1:1521:ORCL");
			String sql="SELECT * FROM (SELECT MDI_READING , MDI_TS from M_FS_DB_INTERVAL where  CST_ID= 5695 ) where rownum < 10000";
			query.setQuery(sql);
			Instances train=query.retrieveInstances();
			
			
			String sql2="(SELECT MDI_READING , MDI_TS from M_FS_DB_INTERVAL where  CST_ID= 5695 AND rownum<11001) MINUS (SELECT MDI_READING , MDI_TS from M_FS_DB_INTERVAL where  CST_ID= 5695 AND rownum<10001)";
			query.setQuery(sql2);
			Instances test=query.retrieveInstances();
			
			M5P classifier = new M5P();
			
			train.setClassIndex(0);
			test.setClassIndex(0);
			classifier.buildClassifier(train);
			for(String s:classifier.getOptions()){
				System.out.println("ssss");
				System.out.println(s);
			}
			System.out.println(classifier.getCapabilities());
			System.out.println(classifier.listOptions().toString());
			Enumeration<Option> e =classifier.listOptions();
			
			System.out.println(classifier.toString());
			double sum=test.numInstances();
			double right=0.0f;
			double sumt=0;
			double num=0;
			NumberFormat fmt = NumberFormat.getPercentInstance();  
	        fmt.setMaximumFractionDigits(6);//最多两位百分小数，如25.23%  
			for(int i=0;i<sum;i++){
				double t=classifier.classifyInstance(test.instance(i))-test.instance(i).classValue();	
		        String s=fmt.format(t/test.instance(i).classValue());
				System.out.println(test.instance(i).classValue()+":"+classifier.classifyInstance(test.instance(i))+":"+s);
				sumt+=t;
				num+=test.instance(i).classValue();
			}
			 System.out.println(fmt.format(sumt/num));  
			 convertDate(1473330000000L);
			 		                 
			 Evaluation evaluation = new Evaluation(test);            
			 System.out.println( "正确率为：" + ( 1 - evaluation.errorRate() ) );
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
