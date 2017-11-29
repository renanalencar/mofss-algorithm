/*
 * RouterJFrame.java
 *
 * Created on 3 de Maio de 2008, 18:18
 */
package hidra.gui;

import hidra.many.metaheuristics.mofssv1.MOFSSv4;
import hidra.many.metaheuristics.mofssv1.MOFSSv5;
import hidra.many.metaheuristics.mofssv1.MOFSSv6;
import hidra.many.metaheuristics.mofssv1.MOFSSv7;
import hidra.many.metaheuristics.mofssv1.MOFSSv8;
import hidra.many.metaheuristics.mofssv1.MOFSSv9;
import hidra.many.metaheuristics.mofssv1.MOFSSv9s1;
import hidra.many.metaheuristics.mofssv1.MOFSSv9s1s2;
import hidra.many.metaheuristics.mofssv1.MOFSSv9s2;
import hidra.metaheuristics.mopsocdr.MOPSOCDR;
import hidra.metaheuristics.mopsocdrs.MOPSOCDRS;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.smpso.SMPSO;
import jmetal.metaheuristics.spea2.SPEA2;


import org.jfree.chart.ChartPanel;


/**
 *
 * @author  robson
 */
public class MOPSOCDRSJFrame extends javax.swing.JFrame {

//    private static MOPSOJFrame instance;
    private ChartPanel chartPanelExternalArchive;
    private ChartPanel chartPanelSwarm;
    private MOPSOCDRS mopso;

    
    public MOPSOCDRSJFrame(MOPSOCDRS algorithm ) {

        kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
    }
    
    public MOPSOCDRSJFrame(MOFSSv9s1 algorithm) {
    	kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
	}
    
    public MOPSOCDRSJFrame(MOFSSv9s2 algorithm) {
    	kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
	}
    
    public MOPSOCDRSJFrame(MOFSSv9s1s2 algorithm) {
		
		kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
	}
    
    public MOPSOCDRSJFrame( MOPSOCDR algorithm ) {

        kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
    }

    public MOPSOCDRSJFrame( SMPSO algorithm ) {

        kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
    }

	/*public MOPSOCDRSJFrame( NSGAII algorithm ) {

        kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
    }*/

	public MOPSOCDRSJFrame( SPEA2 algorithm ) {

        kit = Toolkit.getDefaultToolkit();
        frameIcon = kit.getImage("./imagens/dsc.gif");

        initComponents();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = this.getSize();
        this.setLocation(Math.max(0, (screenSize.width - windowSize.width) / 2), Math.max(0, (screenSize.height - windowSize.height) / 2));

        chartPanelExternalArchive = algorithm.getChartPanelExternalArchive();  
        chartPanelSwarm = algorithm.getChartPanelSwarm();
        
        jPanel2.add( chartPanelExternalArchive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );        
        jPanel4.add( chartPanelSwarm, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 500) );

        this.setVisible(false);
    }

	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MOPSO_CDR");
        setIconImage(frameIcon);
        setName("MOPSO"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 500, 500));

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 500, 500));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 522, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 522, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 523, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 523, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
    private Toolkit kit;
    private Image frameIcon;

    public MOPSOCDRS getMopso() {
        return mopso;
    }

    public void setMopso(MOPSOCDRS mopso) {
        this.mopso = mopso;
//        chartPanel = mopso.getChartPanel();
    }
}
