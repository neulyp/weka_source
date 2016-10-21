package weka;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.plaf.multi.MultiSeparatorUI;

import no.uib.cipr.matrix.GivensRotation;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;
import weka.filters.unsupervised.attribute.Standardize;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;
import weka.gui.visualize.VisualizePanel;

public class Pre_Day {


	public static void main(String[] args) {
		try {
			//1.将instances分为训练集和测试集
			Instances instances=toBinary(normalizeV(standardizeV(replaceMissingV(connectDB()))));
			Instances  train = new Instances(instances, 0);
			Instances test =new Instances(instances, 0);
			for(int i=0;i<instances.numInstances();i++){
				if(i<instances.numInstances()/2){
					train.add(instances.get(i));
				}else{
					test.add(instances.get(i));
				}
			}  
	       //训练1
			System.out.println("决策树算法");
	       CVParameterSelection cvParameterSelection= new CVParameterSelection();
	       String[] options={"-N"};
		   Classifier classifier= getM5PClassifer(train, options);
		   cvParameterSelection.setClassifier(classifier);
		   cvParameterSelection.addCVParameter("M 1 20 20");
		   cvParameterSelection.setNumFolds(10);
		   cvParameterSelection.buildClassifier(train);
		   System.out.println("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
		   String[] bestOptions=cvParameterSelection.getBestClassifierOptions();
		   Classifier newClassifier= getM5PClassifer(train, bestOptions);
		   
		   Evaluation eval = new Evaluation(train);
		   eval.evaluateModel(newClassifier, train);
		   System.out.println("train:"+eval.toSummaryString());
		   
		   Evaluation eval2 = new Evaluation(test);
		   eval2.evaluateModel(newClassifier, test);
		   System.out.println("test:"+eval2.toSummaryString());
		   treeVisual((M5P) newClassifier);
		  			
			//训练3
		   System.out.println("支持向量机回归");
			CVParameterSelection cvParameterSelection1 = new CVParameterSelection();
			String[] options1={};
		    Classifier smo= getSMOregClassifer(train, options1);
		    cvParameterSelection1.setClassifier(smo);
			//cvParameterSelection.addCVParameter("N 0 2 3");0
		    cvParameterSelection1.addCVParameter("C 0.1 2 20");
			cvParameterSelection1.setNumFolds(5);
			cvParameterSelection1.buildClassifier(train);
			System.out.println(Utils.joinOptions(cvParameterSelection1.getBestClassifierOptions()));
			String[] bestOptions1=cvParameterSelection1.getBestClassifierOptions();
			Classifier newsmo= getSMOregClassifer(train, bestOptions1);
			
			Evaluation smoTrainEval = new Evaluation(train);
			smoTrainEval.evaluateModel(newsmo, train);
			System.out.println(smoTrainEval.toSummaryString());
			
			Evaluation smoTestEval = new Evaluation(test);
			smoTestEval.evaluateModel(newsmo, test);
			System.out.println(smoTestEval.toSummaryString());
			
			
			   //训练
			   System.out.println("神经网络");
		        CVParameterSelection cvParameterSelection2= new CVParameterSelection();
			    String[] options2={};
			    Classifier bp= getBPClassifer(train, options2);
			    cvParameterSelection2.setClassifier(bp);
				//cvParameterSelection.addCVParameter("L 0.1 1 10");
				//cvParameterSelection.addCVParameter("M 0.1 1 10");
				cvParameterSelection2.setNumFolds(10);
				cvParameterSelection2.buildClassifier(train);
				System.out.println(Utils.joinOptions(cvParameterSelection2.getBestClassifierOptions()));
				String[] bestOptions2=cvParameterSelection2.getBestClassifierOptions();
				
				Classifier newbp= getBPClassifer(train, bestOptions2);
				
				Evaluation bptrain = new Evaluation(train);
				bptrain.evaluateModel(newbp, train);
				System.out.println(bptrain.toSummaryString());
				
				Evaluation bptest = new Evaluation(test);
				bptest.evaluateModel(newbp, test);
				System.out.println(bptest.toSummaryString());
				//eval.crossValidateModel(newClassifier, instances, 10, new Random(1));
				
	       
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//M5P
	public static Map<Integer, Evaluation> M5PClassifer(Instances instances,int i) throws Exception{
		M5P classifier =new M5P();
        classifier.getOptions();
	    String[] options = {"-M",String.valueOf(i),"-N"};
	    classifier.setOptions(options);
	    classifier.buildClassifier(instances);
        System.out.println(classifier.toString());
        Evaluation eval = new Evaluation(instances);
        //eval.evaluateModel(classifier, data);
		eval.crossValidateModel(classifier, instances, 10, new Random(1));
		Map<Integer, Evaluation> map= new HashMap<Integer, Evaluation>();
		map.put(i, eval);
		return map;
	}
	
	/**
	 * 
	 * @param instances
	 * @param options{-N,-U,-R,-M,-L} 经测试{-N,-M,6}效果最好
	 * @return
	 * @throws Exception
	 */
	public static Classifier getM5PClassifer(Instances instances,String[] options) throws Exception{
		M5P classifier= new M5P();
	    classifier.setOptions(options);
	    classifier.buildClassifier(instances);
		return classifier;
	}
	
	
	/**
	 * 
	 * @param instances
	 * @param options{-L(0-1,0.3),-M(0-1,0.2),-N(500),-V(0-100,0),-S(0),-E(20),-D}
	 * @return
	 * @throws Exception
	 */
	public static Classifier getBPClassifer(Instances instances,String[] options) throws Exception{
		MultilayerPerceptron classifier= new MultilayerPerceptron();
	    classifier.setOptions(options);
	    classifier.buildClassifier(instances);
		return classifier;
	}
	
	/**
	 * 
	 * @param instances
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public static Classifier getSMOregClassifer(Instances instances,String[] options) throws Exception{
		SMOreg classifier= new SMOreg();
		classifier.setOptions(options);
		classifier.buildClassifier(instances);
		return classifier;
	}
	
	//获取数据
	public static Instances connectDB(){
		InstanceQuery query;
		Instances instances = null;
		try {
			query = new InstanceQuery();
			query.setDebug(true);
			query.setUsername("C##TEST");
			query.setPassword("test");
			query.setDatabaseURL("jdbc:oracle:thin:@127.0.0.1:1521:ORCL");
			String sql="SELECT M,D,W,IS_HOLIDAYS,R1,R2,R3,R7,A3,R FROM INTERVAL_D WHERE CST_ID=5681";
			query.setQuery(sql);
			instances=query.retrieveInstances();
			instances.setClassIndex(9);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instances;
	}
	
	//缺省值处理
	public static Instances replaceMissingV(Instances instances) throws Exception{
		ReplaceMissingValues missingValues = new ReplaceMissingValues();
		missingValues.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, missingValues);
		return inst;
	}
	
	//标准化
	public static Instances standardizeV(Instances instances) throws Exception{
		Standardize standardize = new Standardize();
		standardize.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, standardize);
		return inst;
	}
	
	//规范化
	public static Instances normalizeV(Instances instances) throws Exception{
		Normalize normalize= new Normalize();
		normalize.setInputFormat(instances);
		Instances inst=Filter.useFilter(instances, normalize);
		return inst;
	}
	
	//标称值转化为二分值
	public static Instances toBinary(Instances instances) throws Exception{
		NominalToBinary nominalToBinary = new NominalToBinary();
		nominalToBinary.setBinaryAttributesNominal(false);
		nominalToBinary.setInputFormat(instances);
		String[] options = new String[1]; 
		options[0] = "-A"; 
		nominalToBinary.setOptions(options);
		Instances inst=Filter.useFilter(instances, nominalToBinary);
		return inst;
		
	}
	
	public static void  treeVisual(M5P classifier) throws Exception{
		TreeVisualizer treeVisualizer=new TreeVisualizer(null, classifier.graph(), new PlaceNode2());
				JFrame jFrame=new JFrame("决策树测试:M5P");
				jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jFrame.setSize(1980, 1024);
				jFrame.getContentPane().setLayout(new BorderLayout());
				jFrame.getContentPane().add(treeVisualizer, BorderLayout.CENTER);
				jFrame.setVisible(true);
				treeVisualizer.fitToScreen();
	}
	
	
	public static void  getpic(Evaluation eval) throws Exception{
		//VisualizePanel tc=new VisualizePanel();
		ThresholdCurve tc = new ThresholdCurve();
		//classIndex is the index of the class to consider as "positive"
		int classIndex = 0;
		Instances result = tc.getCurve(eval.predictions(), classIndex);
		System.out.println("The area under the ROC　curve: " + eval.areaUnderROC(classIndex));
		/*
		* 在这里我们通过结果信息Instances对象得到包含TP、FP的两个数组
		* 这两个数组用于在SPSS中通过线图绘制ROC曲面
		*/
		int tpIndex = result.attribute(ThresholdCurve.TP_RATE_NAME).index();
		int fpIndex = result.attribute(ThresholdCurve.FP_RATE_NAME).index();
		double [] tpRate = result.attributeToDoubleArray(tpIndex);
		double [] fpRate = result.attributeToDoubleArray(fpIndex);
		/*
		* 4.使用结果信息instances对象来显示ROC曲面
		*/
		ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
		//这个获得AUC的方式与上面的不同，其实得到的都是一个共同的结果
		vmc.setROCString("(Area under ROC = " +
		Utils.doubleToString(tc.getROCArea(result), 4) + ")");
		vmc.setName(result.relationName());
		PlotData2D tempd = new PlotData2D(result);
		tempd.setPlotName(result.relationName());
		tempd.addInstanceNumberAttribute();
		vmc.addPlot(tempd);
		// 显示曲面
		String plotName = vmc.getName();
		final javax.swing.JFrame jf =
		new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
		jf.setSize(500,400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(vmc, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
		public void windowClosing(java.awt.event.WindowEvent e) {
		jf.dispose();
		}
		});
		jf.setVisible(true);
	}
	
    // other options  
/*	        int seed  =1;  
    int folds =10;  
  
    // randomize data  
    Random rand = new Random(seed);  
    Instances randData = new Instances(instances);  
    randData.randomize(rand);  
    if (randData.classAttribute().isNominal()){
    	 randData.stratify(folds);  
    }*/	      
   /* // perform cross-validation  
    Evaluation eval = new Evaluation(randData);  
    for (int n = 0; n < folds; n++) {  
      Instances train = randData.trainCV(folds, n);  
      Instances test = randData.testCV(folds, n);  
      M5P classifier =new M5P();
      String[] options = {"-M","5","-R"};
      classifier.setOptions(options);
      classifier.buildClassifier(train);  
      eval.evaluateModel(classifier, test);  
    }   
    // output evaluation  
    System.out.println();  
    System.out.println("=== Setup ===");  
    //System.out.println("Classifier: " + classifier.getClass().getName() + " " + Utils.joinOptions(classifier.getOptions()));  
    System.out.println("Dataset: " + instances.relationName());  
    System.out.println("Folds: " + folds);  
    System.out.println("Seed: " + seed);  
    System.out.println();  
    System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));  */
    
    //
	 List<Map<Integer, Evaluation>> lists = new ArrayList<Map<Integer, Evaluation>>();
     //參數設置
/*     for(int i=4;i<10;i++){
			//System.out.println(eval.toSummaryString("\nResult", false));			
			//lists.add(M5PClassifer(instances, i));
     }*/
   /*  for(int i=0;i<lists.size();i++){
     	System.out.println(i+":"+lists.get(i).get(i+4).toSummaryString());
     }*/
     //查看结果
     //System.out.println(lists.get(2).get(6).toSummaryString());
     
/*	        String[] options ={"-C","1","-N","0"};
     Classifier classifier=getSMOregClassifer(instances, options);
     Evaluation eval = new Evaluation(instances);
     //eval.evaluateModel(classifier, data);
		eval.crossValidateModel(classifier, instances, 10, new Random(1));
		System.out.println(eval.toSummaryString());*/
     
     
     
     //bp测试options{-L(0-1,0.3),-M(0-1,0.2),-N(500),-V(0-100,0),-S(0),-E(20),-D}
     
   /* CVParameterSelection selection= new CVParameterSelection();
    String[] options={};
    Classifier classifier= getBPClassifer(instances, options);
    selection.setClassifier(classifier);
    //训练l
    selection.addCVParameter("L 0 1 10");
    selection.setNumFolds(5);
    selection.buildClassifier(instances);
    System.out.println(Utils.joinOptions(selection.getBestClassifierOptions()));*/
}
