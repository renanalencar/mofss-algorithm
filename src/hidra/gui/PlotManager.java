/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hidra.gui;


import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Guest
 */
public class PlotManager {

    private XYSeries xySeriesExternalArchive;
    private XYSeriesCollection xySeriesCollectionExternalArchive;
    private JFreeChart jFreeChartExternalArchive;
    private ChartPanel chartPanelExternalArchive;
    private XYPlot xyPlotExternalArchive;
    private XYLineAndShapeRenderer xyLineAndShapeRendererExternalArchive;
    private NumberAxis numberAxisExternalArchive;
    private XYSeries xySeriesSwarm;
    private XYSeriesCollection xySeriesCollectionSwarm;
    private JFreeChart jFreeChartSwarm;
    private ChartPanel chartPanelSwarm;
    private XYPlot xyPlotSwarm;
    private XYLineAndShapeRenderer xyLineAndShapeRendererSwarm;
    private NumberAxis numberAxisSwarm;
    private boolean debug;
    private double pointsize;
    
    private XYSeries xyBarycenter;
    private XYSeries xyLimits;
    
    private XYSeries xyParticlesMovInd;
    private XYSeries xyParticlesMovCoI;
    private XYSeries xyParticlesMovCoV;
    

    public PlotManager() {
    }

    public void setupPlotExternalArchive() {
        pointsize = 2.0;
        xySeriesExternalArchive = new XYSeries("Functions");
        xySeriesCollectionExternalArchive = new XYSeriesCollection(xySeriesExternalArchive);
        setJFreeChartExternalArchive(ChartFactory.createXYLineChart("External Archive", "Objective 1", "Objective 2", xySeriesCollectionExternalArchive, PlotOrientation.VERTICAL, true, true, false));
        xyPlotExternalArchive = (XYPlot) jFreeChartExternalArchive.getPlot();
        xyPlotExternalArchive.setBackgroundPaint( Color.WHITE );
        xyPlotExternalArchive.setDomainGridlinePaint(Color.GRAY);
        xyPlotExternalArchive.setRangeGridlinePaint(Color.GRAY);
        xyPlotExternalArchive.getRenderer().setSeriesPaint(0, Color.BLACK);
        xyPlotExternalArchive.getRenderer().setSeriesShape(0, new Ellipse2D.Double(pointsize/2.0*(-1),pointsize/2.0*(-1),pointsize,pointsize));
        xyLineAndShapeRendererExternalArchive = (XYLineAndShapeRenderer) xyPlotExternalArchive.getRenderer();
        xyLineAndShapeRendererExternalArchive.setBaseShapesVisible(true);
        xyLineAndShapeRendererExternalArchive.setBaseShapesFilled(true);
//        xyLineAndShapeRendererExternalArchive.
//        xyLineAndShapeRendererExternalArchive.set
        xyLineAndShapeRendererExternalArchive.setBaseLinesVisible(false);
        numberAxisExternalArchive =(NumberAxis) xyPlotExternalArchive.getRangeAxis();
        numberAxisExternalArchive.setLabelAngle(Math.PI/2);
        //numberAxisExternalArchive.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanelExternalArchive = new ChartPanel(getJFreeChartExternalArchive());
    }

    public void setupPlotSwarm() {
        xySeriesSwarm = new XYSeries("Particle Position");
        xyBarycenter = new XYSeries("Barycentre");
        xyLimits = new XYSeries("Search Space Boundaries");
        
        xyParticlesMovInd = new XYSeries("Individual Movement");
        xyParticlesMovCoI = new XYSeries("Collective-instinctive Movement");
        xyParticlesMovCoV = new XYSeries("Collective-volitive Movement");
        
        xySeriesCollectionSwarm = new XYSeriesCollection();
        xySeriesCollectionSwarm.addSeries(xyLimits);
        xySeriesCollectionSwarm.addSeries(xyBarycenter);
        xySeriesCollectionSwarm.addSeries(xySeriesSwarm);
        
        
        xySeriesCollectionSwarm.addSeries(xyParticlesMovInd);
        xySeriesCollectionSwarm.addSeries(xyParticlesMovCoI);
        xySeriesCollectionSwarm.addSeries(xyParticlesMovCoV);
        
        jFreeChartSwarm = ChartFactory.createXYLineChart("Iteration: 0", "X", "Y", xySeriesCollectionSwarm, PlotOrientation.VERTICAL, true, true, false);
        xyPlotSwarm = (XYPlot) jFreeChartSwarm.getPlot();
        
        //xyPlotSwarm.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        
        
        //IntervalMarker target = new IntervalMarker(5.0, 2.0);
        //target.setLabel("Target Range");
        //xyPlotSwarm.addRangeMarker(target);
        //XYItemRenderer renderer = xyPlotSwarm.getRenderer();
        //renderer.setSeriesPaint(0, Color.blue);
        xyLineAndShapeRendererSwarm = (XYLineAndShapeRenderer) xyPlotSwarm.getRenderer();
        xyLineAndShapeRendererSwarm.setSeriesPaint(0, Color.black);

        xyLineAndShapeRendererSwarm.setSeriesPaint(1, Color.blue);
        xyLineAndShapeRendererSwarm.setSeriesPaint(2, Color.gray);
        
        xyLineAndShapeRendererSwarm.setSeriesPaint(3, Color.red);
        xyLineAndShapeRendererSwarm.setSeriesPaint(4, Color.magenta);
        xyLineAndShapeRendererSwarm.setSeriesPaint(5, Color.cyan);
        
        xyLineAndShapeRendererSwarm.setBaseShapesVisible(true);
        xyLineAndShapeRendererSwarm.setBaseShapesFilled(true);
        
//        xylineandshaperenderer.setBaseShape(new Shape());
        xyLineAndShapeRendererSwarm.setBaseLinesVisible(false);
        numberAxisSwarm = (NumberAxis) xyPlotSwarm.getRangeAxis();
        numberAxisSwarm.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanelSwarm = new ChartPanel(jFreeChartSwarm);
        
    }
    
    public void plotLimitsProblem(Problem problem){
    	xyLimits.add(problem.getLowerLimit(0), problem.getUpperLimit(0));
    	xyLimits.add(problem.getUpperLimit(0), problem.getLowerLimit(0));
    	xyLimits.add(problem.getLowerLimit(0), problem.getLowerLimit(0));
    	xyLimits.add(problem.getUpperLimit(0), problem.getUpperLimit(0));
    }

    public void plotExternalArchive(SolutionSet externalArchive, int currentNCallObjectiveFunction) {
        xySeriesExternalArchive.clear();

        if ((externalArchive.size() != 0) && (externalArchive.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < externalArchive.size(); i++) {
                xySeriesExternalArchive.add(externalArchive.get(i).getObjective(0), externalArchive.get(i).getObjective(1));
            }
        }
    }

    public void plotSwarm(SolutionSet swarm, int currentNCallObjectiveFunction) throws JMException {
        xySeriesSwarm.clear();
        xyBarycenter.clear();
        
        if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
                xySeriesSwarm.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
    }
    
    public void plotSwarmBary(SolutionSet swarm, int currentNCallObjectiveFunction, Solution barycenter) throws JMException {
        xySeriesSwarm.clear();
        xyBarycenter.clear();
        
        if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
                xySeriesSwarm.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
        
        xyBarycenter.add(barycenter.getDecisionVariables()[0].getValue(), barycenter.getDecisionVariables()[1].getValue());
    }
    
    public void plotMOFSSMovInd(SolutionSet swarm, int currentNCallObjectiveFunction) throws JMException{
    	xyBarycenter.clear();
    	xyParticlesMovInd.clear();
    	xyParticlesMovCoI.clear();
    	xyParticlesMovCoV.clear();
    	
    	if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
            	xyParticlesMovInd.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
    	
    }
    
    public void plotMOFSSMovCoI(SolutionSet swarm, int currentNCallObjectiveFunction) throws JMException{
    	
    	if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
            	xyParticlesMovCoI.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
    	
    }
    
    public void plotMOFSSMovCoV(SolutionSet swarm, int currentNCallObjectiveFunction) throws JMException{
    	
    	if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
            	xyParticlesMovCoV.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
    }
    
    public void plotMOFSSBarycenter(Solution barycenter) throws JMException{
    	xyBarycenter.add(barycenter.getDecisionVariables()[0].getValue(), barycenter.getDecisionVariables()[1].getValue());
    }
    
    public void plotSwarmMOFSS(SolutionSet swarm, int currentNCallObjectiveFunction, Solution barycenter) throws JMException {
        xySeriesSwarm.clear();
    	xyParticlesMovInd.clear();
        xyParticlesMovCoI.clear();
    	
        if ((swarm.size() != 0) && (swarm.get(0).numberOfObjectives() >= 2)) {
            for (int i = 0; i < swarm.size(); i++) {
            	//xySeriesSwarm.add(swarm.get(i).getObjective(0), swarm.get(i).getObjective(1));
                xySeriesSwarm.add(swarm.get(i).getDecisionVariables()[0].getValue(), swarm.get(i).getDecisionVariables()[1].getValue());
            } 
            jFreeChartSwarm.setTitle("Iteration: " + currentNCallObjectiveFunction);
        }
        
        //xyBarycenter.add(barycenter.getDecisionVariables()[0].getValue(), barycenter.getDecisionVariables()[1].getValue());
        
    }
    
    
    
    
   


    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the chartPanelExternalArchive
     */
    public ChartPanel getChartPanelExternalArchive() {
        return chartPanelExternalArchive;
    }

    /**
     * @param chartPanelExternalArchive the chartPanelExternalArchive to set
     */
    public void setChartPanelExternalArchive(ChartPanel chartPanelExternalArchive) {
        this.chartPanelExternalArchive = chartPanelExternalArchive;
    }

    /**
     * @return the chartPanelSwarm
     */
    public ChartPanel getChartPanelSwarm() {
        return chartPanelSwarm;
    }

    /**
     * @param chartPanelSwarm the chartPanelSwarm to set
     */
    public void setChartPanelSwarm(ChartPanel chartPanelSwarm) {
        this.chartPanelSwarm = chartPanelSwarm;
    }

   /* public void printParticles( ArrayList<Particle> particles, Problem problem ) {
        for (int i = 0; i < particles.size(); i++) {
            this.printParticle(particles.get(i), problem, particles.get(i).getParticleData().getIndex());
        }
        System.out.println("");
    }
    
    public void printParticles_CSS( ArrayList<Particle_CSS> particles, Problem problem ) {
        for (int i = 0; i < particles.size(); i++) {
            this.printParticle(particles.get(i), problem, particles.get(i).getParticleData().getIndex());
        }
        System.out.println("");
    }    */
 
   /* private void printParticle(Particle particle, Problem problem, int index) {

        System.out.println("Particula " + index + ":");

        System.out.println("Posicoes: ");
        for (int j = 0; j < problem.getNDimensions() - 1; j++) {
            System.out.print(particle.getParticleData().getPositions()[j] + ", ");
        }
        System.out.println(particle.getParticleData().getPositions()[problem.getNDimensions() - 1]);

        System.out.println("Velocidades: ");
        for (int j = 0; j < problem.getNDimensions() - 1; j++) {
            System.out.print(particle.getParticleData().getVelocities()[j] + ", ");
        }
        System.out.println(particle.getParticleData().getVelocities()[problem.getNDimensions() - 1]);

        System.out.println("Fitness: ");
        for (int k = 0; k < problem.getNObjectives() - 1; k++) {
            System.out.print(particle.getParticleData().getFitness()[k] + ", ");
        }
        System.out.println(particle.getParticleData().getFitness()[problem.getNObjectives() - 1]);

        System.out.println("g(): " + particle.getParticleData().getGValue());

        System.out.println("Crowding Distance: " + particle.getParticleData().getCrowdingDistance());

        System.out.println("");
    }
    
   */

    public JFreeChart getJFreeChartExternalArchive() {
        return jFreeChartExternalArchive;
    }

    public void setJFreeChartExternalArchive(JFreeChart jFreeChartExternalArchive) {
        this.jFreeChartExternalArchive = jFreeChartExternalArchive;
    }

    public NumberAxis getNumberAxisExternalArchive() {
        return numberAxisExternalArchive;
    }

    public void setNumberAxisExternalArchive(NumberAxis numberAxisExternalArchive) {
        this.numberAxisExternalArchive = numberAxisExternalArchive;
    }
}
