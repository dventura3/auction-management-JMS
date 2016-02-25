/*
 * UI.java
 */

package client;

import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.dto.*;

/**
 * The application's main frame.
 */
public class UI extends FrameView {

    private ArrayList<Workflow> workflowsList = new ArrayList<Workflow>();
//    private ArrayList<Workflow> w = new ArrayList<Workflow>(); //lista di workflow di cui aspetto risposte dal manager
    private org.dto.Workflow workflow;
    private DefaultListModel model = new DefaultListModel();
    private DefaultListModel model2 = new DefaultListModel();
    private DefaultListModel model3 = new DefaultListModel();
    private DefaultListModel model4 = new DefaultListModel();
    private static WFQueueManagerProxy clientmanager;
    private static WFQProxyFactory proxyFactory;
    private LinkedList<NewTaskView> taskViewsList = new LinkedList<NewTaskView>();
    String serverAddress = null;
    String serverPort = null;
    boolean OneManager;
    private boolean auctionUp;
    private TopicConnectionFactory connectionFactory;
    private Topic topic;

    public UI(SingleFrameApplication app, TopicConnectionFactory tcf, Topic t) {
        super(app);

        AsWorkflowResponce workflowResponce = new AsWorkflowResponce(tcf,t,model2);
        new Thread(workflowResponce).start();

        this.connectionFactory = tcf;
        this.topic = t;
        initComponents();
        this.getFrame().setResizable(false);
        this.jPanel3.setVisible(false);
        ButtonGroup group = new ButtonGroup();
        jRadioButton1.setSelected(true);
        group.add(jRadioButton1);
        group.add(jRadioButton2);
        SizeLayout();
        auctionUp = true;
        workflow = new Workflow();
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MiddlewareMain.getApplication().getMainFrame();
            aboutBox = new MiddlewareClientAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MiddlewareMain.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(UI.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        mainPanel.setMaximumSize(new java.awt.Dimension(614, 565));
        mainPanel.setMinimumSize(new java.awt.Dimension(553, 320));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(614, 565));
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setForeground(resourceMap.getColor("jLabel1.foreground")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        mainPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, -1, -1));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(380, 180));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(380, 180));

        jList1.setModel(model);
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setName("jList1"); // NOI18N
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        mainPanel.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 30, -1, -1));

        jLabel2.setForeground(resourceMap.getColor("jLabel2.foreground")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        mainPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 10, -1, -1));

        jPanel2.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(190, 180));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(190, 190));

        jList2.setModel(model2);
        jList2.setName("jList2"); // NOI18N
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jList2);

        jPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 180));

        mainPanel.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, -1));

        jPanel3.setBackground(resourceMap.getColor("jPanel3.background")); // NOI18N
        jPanel3.setMaximumSize(new java.awt.Dimension(510, 180));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(600, 220));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane3.setMaximumSize(new java.awt.Dimension(45, 95));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(45, 95));
        jScrollPane3.setName("jScrollPane3"); // NOI18N
        jScrollPane3.setPreferredSize(new java.awt.Dimension(290, 150));

        jList3.setModel(model3);
        jList3.setMaximumSize(new java.awt.Dimension(45, 95));
        jList3.setMinimumSize(new java.awt.Dimension(45, 95));
        jList3.setName("jList3"); // NOI18N
        jList3.setPreferredSize(new java.awt.Dimension(280, 140));
        jScrollPane3.setViewportView(jList3);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 30, -1, -1));

        jScrollPane4.setMaximumSize(new java.awt.Dimension(45, 95));
        jScrollPane4.setMinimumSize(new java.awt.Dimension(45, 95));
        jScrollPane4.setName("jScrollPane4"); // NOI18N
        jScrollPane4.setPreferredSize(new java.awt.Dimension(290, 150));

        jList4.setModel(model4);
        jList4.setMaximumSize(new java.awt.Dimension(250, 150));
        jList4.setMinimumSize(new java.awt.Dimension(250, 150));
        jList4.setName("jList4"); // NOI18N
        jScrollPane4.setViewportView(jList4);

        jPanel3.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(305, 30, -1, -1));

        jLabel3.setForeground(resourceMap.getColor("jLabel3.foreground")); // NOI18N
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel4.setForeground(resourceMap.getColor("jLabel4.foreground")); // NOI18N
        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jRadioButton1.setBackground(resourceMap.getColor("jRadioButton1.background")); // NOI18N
        jRadioButton1.setFont(resourceMap.getFont("jRadioButton1.font")); // NOI18N
        jRadioButton1.setForeground(resourceMap.getColor("jRadioButton1.foreground")); // NOI18N
        jRadioButton1.setText(resourceMap.getString("jRadioButton1.text")); // NOI18N
        jRadioButton1.setName("jRadioButton1"); // NOI18N
        jPanel3.add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 190, -1, -1));

        jRadioButton2.setBackground(resourceMap.getColor("jRadioButton2.background")); // NOI18N
        jRadioButton2.setFont(resourceMap.getFont("jRadioButton2.font")); // NOI18N
        jRadioButton2.setForeground(resourceMap.getColor("jRadioButton2.foreground")); // NOI18N
        jRadioButton2.setText(resourceMap.getString("jRadioButton2.text")); // NOI18N
        jRadioButton2.setName("jRadioButton2"); // NOI18N
        jPanel3.add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, -1, -1));

        mainPanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, -1));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(UI.class, this);
        jButton1.setAction(actionMap.get("SetParams")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setToolTipText(resourceMap.getString("Connect.toolTipText")); // NOI18N
        jButton1.setActionCommand(resourceMap.getString("Connect.actionCommand")); // NOI18N
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setMaximumSize(new java.awt.Dimension(110, 38));
        jButton1.setMinimumSize(new java.awt.Dimension(110, 38));
        jButton1.setName("Connect"); // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(110, 38));
        mainPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, -1, -1));
        jButton1.getAccessibleContext().setAccessibleName(resourceMap.getString("Connect.AccessibleContext.accessibleName")); // NOI18N

        jButton2.setAction(actionMap.get("CreateNewTask")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setToolTipText(resourceMap.getString("jButton2.toolTipText")); // NOI18N
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setMaximumSize(new java.awt.Dimension(110, 38));
        jButton2.setMinimumSize(new java.awt.Dimension(110, 38));
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(110, 38));
        mainPanel.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 450, -1, -1));

        jButton3.setAction(actionMap.get("DeleteTask")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setToolTipText(resourceMap.getString("jButton3.toolTipText")); // NOI18N
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.setMaximumSize(new java.awt.Dimension(110, 38));
        jButton3.setMinimumSize(new java.awt.Dimension(110, 38));
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setPreferredSize(new java.awt.Dimension(110, 38));
        mainPanel.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 450, -1, -1));

        jButton4.setAction(actionMap.get("SendTasks")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setToolTipText(resourceMap.getString("jButton4.toolTipText")); // NOI18N
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.setEnabled(false);
        jButton4.setMaximumSize(new java.awt.Dimension(110, 38));
        jButton4.setMinimumSize(new java.awt.Dimension(110, 38));
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setPreferredSize(new java.awt.Dimension(110, 38));
        mainPanel.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 450, -1, -1));

        menuBar.setMaximumSize(new java.awt.Dimension(84, 21));
        menuBar.setMinimumSize(new java.awt.Dimension(84, 21));
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setAction(actionMap.get("ConnectToManager")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("ConnectMenu.text")); // NOI18N
        jMenuItem1.setName("ConnectMenu"); // NOI18N
        fileMenu.add(jMenuItem1);

        jMenuItem2.setAction(actionMap.get("CreateNewTask")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("NewTaskMenu.text")); // NOI18N
        jMenuItem2.setName("NewTaskMenu"); // NOI18N
        fileMenu.add(jMenuItem2);

        jMenuItem3.setAction(actionMap.get("SendTasks")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("SendTasksMenu.text")); // NOI18N
        jMenuItem3.setName("SendTasksMenu"); // NOI18N
        fileMenu.add(jMenuItem3);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setMaximumSize(new java.awt.Dimension(538, 25));
        statusPanel.setMinimumSize(new java.awt.Dimension(538, 25));
        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 538, -1));

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, -1, -1));

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusPanel.add(statusAnimationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 8, -1, -1));

        progressBar.setName("progressBar"); // NOI18N
        statusPanel.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, -1, -1));

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked

        if(evt.getClickCount() == 2){
            int index = jList1.locationToIndex(evt.getPoint());
           
            EditTaskView etv = new EditTaskView(index, workflow, model);
            etv.setVisible(true);
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList2MouseClicked
        if(evt.getClickCount() == 2){
            int index = jList2.locationToIndex(evt.getPoint());
            String[] modelElement = String.valueOf(model2.get(index)).split(" - ");
            int workflowId = Integer.parseInt(modelElement[0]);
            String workStatus = modelElement[1];
            try
            {
                Iterator<Workflow> it = workflowsList.listIterator();
                Workflow selected = null;
                while(it.hasNext())
                {
                    Workflow tmp = it.next();
                    if(tmp.getID() == workflowId)
                    {
                        selected = tmp;
                        break;
                    }
                }
                if (selected!=null)
                {
                    if(workStatus.equals("In Attesa") || workStatus.equals("Ok"))
                    {
                        AboutWorkflow abwork = new AboutWorkflow(MiddlewareMain.getApplication().getMainFrame(), true, selected);
                        abwork.setLocationRelativeTo(MiddlewareMain.getApplication().getMainFrame());
                        abwork.setVisible(true);
                    }
                    else
                    {
                        Object[] options = {"Yes",
                                "No"};
                        int n = JOptionPane.showOptionDialog(this.mainPanel,
                            "Si vuole reinviare nuovamente il workflow " + selected.getID()+ " selezionato? \n" +
                            "Attenzione, verranno eliminati tutti i task attualmente creati!",
                            "Confirm workflow resending",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                        if(n == 0)
                        {
                            this.workflow = selected;
                            model.clear();
                            Iterator<TaskDescriptor> iter = this.workflow.getTasks().iterator();
                            while(iter.hasNext())
                            {
                                TaskDescriptor td = iter.next();
                                String visualTd = String.valueOf(td.getID()) +
                                        " - Type: " + td.getType().toString() +
                                        " - Command: " +  td.getCommand() +
                                        " - CPU: " + String.valueOf(td.getCpuRequired()) +
                                        " - Ram: " + String.valueOf(td.getRamRequired()) +
                                        " - Space: " + String.valueOf(td.getSpaceRequired());
                                model.addElement(visualTd);
                            }
                            this.getFrame().repaint();
                        }
                    }
                }
            }
            catch(IndexOutOfBoundsException e)
            {

            }
        }
    }//GEN-LAST:event_jList2MouseClicked

    @Action
    public Task CreateNewTask() {
        if (workflow == null)
            workflow = new Workflow();
        return new CreateNewTaskTask(getApplication());
    }

    private void ResizeLayout() {
        this.jButton4.setEnabled(true);
        this.getFrame().setSize(620,585);
        this.jPanel3.setSize(jPanel3.getPreferredSize());
        Dimension size1 = jButton1.getPreferredSize();
        this.jButton1.setBounds(40, 455, size1.width, size1.height);
        this.jButton2.setBounds(180, 455, size1.width, size1.height);
        this.jButton3.setBounds(320, 455, size1.width, size1.height);
        this.jButton4.setBounds(460, 455, size1.width, size1.height);
        this.getFrame().repaint();
        if (!this.jPanel3.isVisible())
            this.jPanel3.setVisible(true);
    }

    private void SizeLayout() {
        this.jPanel3.setSize(0,0);
        Dimension size1 = jButton1.getPreferredSize();
        this.jButton1.setBounds(40, 250, size1.width, size1.height);
        this.jButton2.setBounds(180, 250, size1.width, size1.height);
        this.jButton3.setBounds(320, 250, size1.width, size1.height);
        this.jButton4.setBounds(460, 250, size1.width, size1.height);
        this.getFrame().repaint();
    }

    private class CreateNewTaskTask extends org.jdesktop.application.Task<Object, Void> {
        CreateNewTaskTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to CreateNewTaskTask fields, here.
            super(app);
            
            //this.workflow = workflow;
//            this.model = model;
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            NewTaskView ntv = new NewTaskView(workflow, model);
            ntv.setVisible(true);
            return ntv;
            //return null;  // return your result
        }
        @Override protected void succeeded(Object result){
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            taskViewsList.add((NewTaskView)result);
        }
    }

    public void ConnectToManager() {
        ConnectionDialog conn = new ConnectionDialog(MiddlewareMain.getApplication().getMainFrame(), true, this);
        conn.setVisible(true);
        conn.pack();
        String [] s = conn.getValidatedText();
      
        if (s!=null)
        {
            if (s[0]!=null && s[1]!=null)
            {
                proxyFactory = new WFQProxyFactory();
                clientmanager = WFQProxyFactory.getInstance().getProxy();
                if(!s[0].equals("default"))
                    clientmanager.setServerHostname(s[0]);
                if(!s[1].equals("default"))
                    clientmanager.setPort(Integer.parseInt(s[1]));
                //this.jButton1.setEnabled(false);
                this.jButton4.setEnabled(true);
                SizeLayout();
            }
        }
    }

    @Action
    public void SetParams(){
        SettingsDialog settings = new SettingsDialog(MiddlewareMain.getApplication().getMainFrame(), true, this);
        settings.setVisible(true);
        settings.pack();
        int mode = settings.getSelectedMode();
       
        if(mode != 0){
            if (mode==1){
                //Open Dialog to set Port and Adress Manager
                ConnectToManager();
            }
            else{
                //Set JPanel3 Visible, to set Auction mode
                ResizeLayout();
                //this.jButton2.setEnabled(true);
            }
        }
    }

    @Action
    public Task SendTasks() {
        return new SendTasksTask(getApplication());
    }

    private class SendTasksTask extends org.jdesktop.application.Task<Object, Void> {
        //private WFQueueManagerProxy clientmanager;
        SendTasksTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SendTasksTask fields, here.
            super(app);
            //this.clientmanager = clientmanager;
            if(taskViewsList!=null && !taskViewsList.isEmpty())
            {
                ListIterator<NewTaskView> it = taskViewsList.listIterator();
                NewTaskView tmp = it.next();
                while(it.hasNext())
                {
                    if (tmp != null)
                        tmp.dispose();
                    tmp = it.next();
                }
                taskViewsList.clear();
            }
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            if(!workflowsList.contains(workflow))
                workflowsList.add(workflow);
            if(OneManager)
            {
                model.clear();
                if (clientmanager != null && workflow != null && !workflow.getTasks().isEmpty())
                {
                    int ret = clientmanager.enqueue(workflow);
                    workflow = new Workflow();
                    return ret;
                }
                else if (workflow == null)
                    return "Workflow Nullo!";
                else if (workflow.getTasks().isEmpty())
                    return "Workflow Vuoto! Si prega di inserire dei Task!";
                return "Connettersi al server!";  // return your result
            }
            else
            {
                if(workflow != null && !workflow.getTasks().isEmpty())
                    return sendWorkflowByAuction();
                else
                    return "Workflow Vuoto! Si prega di inserire dei Task!";
            }
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
            if(result!= null){
                if (result.equals(1))
                {
                    if(OneManager){
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                                "Tasks inviati con successo",
                                "Result",
                                JOptionPane.INFORMATION_MESSAGE);
                        model2.addElement(workflow.getID() + " - " + "Ok");
                    }
                }
                else if (result.equals(-1))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                           "Connessione rifiutata!",
                           "Result",
                           JOptionPane.ERROR_MESSAGE);
                    if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else if(result.equals(-2))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                            "Server Host non trovato!",
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                    if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else if(result.equals(-3))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                            "File di uno dei Task non trovato!!!",
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                    if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else if(result.equals(-4))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                               "Errore nell'invio del Workflow",
                               "Result",
                               JOptionPane.ERROR_MESSAGE);
                    if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else if(result.equals(-5))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                            "Errore nella chiusura del Socket",
                            "Result",
                            JOptionPane.ERROR_MESSAGE);
                    if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else if(result.equals(-6))
                {
                        JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                                "Socket: Null Pointer Exception!!!!!",
                                "Result",
                                JOptionPane.ERROR_MESSAGE);
                       if(OneManager){
                        model2.addElement(workflow.getID() + " - " + "Error");
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                           result,
                           "Result",
                           JOptionPane.ERROR_MESSAGE);
                    if (!workflowsList.isEmpty())
                    {
                        workflowsList.remove(workflow);
                    }
                }
            }
            else
            {
                JOptionPane.showMessageDialog(MiddlewareMain.getApplication().getMainFrame(),
                       "Nessun Manager in ascolto!\nVerificare di aver attivato almeno un Manager.",
                       "Result",
                       JOptionPane.ERROR_MESSAGE);
                if (!workflowsList.isEmpty())
                {
                    workflowsList.remove(workflow);
                }
            }

            //in ogni caso puliamo le jList
            //workflow = new Workflow();
            model4.clear();
            if(OneManager)
                SizeLayout();
            else
                ResizeLayout();
        }
        /*
         * This method is called when Auction mode is selected.
         * It permits to send task to a topic and to manage the current auction
         */
        private Object sendWorkflowByAuction() {
            jButton4.setEnabled(false);
            AuctionBid ab = null;
            model.clear();
            if(jRadioButton1.isSelected()){
                try {
                    auctionUp = true;
                    model3.addElement(workflow.getID() + " - " + "Sent - Rialzo");
                    GestoreAsta ga = new GestoreAsta(workflow, AuctionTYPE.AscendingPriceAuction, connectionFactory, topic, model4);
                    Thread t = new Thread(ga);
                    t.start();
                    workflow = new Workflow();
                    t.join();
                    ab = ga.ab;
                } catch (InterruptedException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                auctionUp = false;
                model3.addElement(workflow.getID() + " - " + "Sent - Ribasso");
                GestoreAsta ga = new GestoreAsta(workflow, AuctionTYPE.DescendingPriceAuction,connectionFactory, topic);
                Thread t = new Thread(ga);
                t.start();
                workflow = new Workflow();
                int i = 0;
                while(ga.ab.getManagerId().equals("-") && i < 11){
                    try {
                        Thread.sleep(5100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    i++;
                }
                ab = ga.ab;
            }
            System.out.println("Asta finita, ora facciamo l'enqueue");

            if (ab!=null)
            {
                if(!ab.getManagerId().equals("-"))
                {
                    Workflow wor = null;
                    Iterator<Workflow> it = workflowsList.listIterator();
                    while(it.hasNext())
                    {
                        Workflow w = it.next();
                        if(w.getID() == ab.getWorkflowId())
                            wor = w;
                    }

                    clientmanager = new WFQueueManagerProxy();
                    clientmanager.setPort(Integer.parseInt(ab.getManagerPort()));
                    clientmanager.setServerHostname(ab.getManagerIP());
                    if (clientmanager != null && wor != null && !wor.getTasks().isEmpty())
                    {
                        int ret = clientmanager.enqueue(wor);
                        if(ret == 1){
                            model2.addElement(wor.getID() + " - " + "In Attesa - " + ab.getManagerId());
                            AsWorkflowResponce.insertNewWorkflow(ab.getWorkflowId(), ab.getManagerId());
                        }
                        else
                            model2.addElement(wor.getID() + " - " + "Error");
                        return ret;
                    }
                    else if (wor == null)
                        return "Workflow Nullo!";
                    else if (wor.getTasks().isEmpty())
                        return "Workflow Vuoto! Si prega di inserire dei Task!";
                    return "Connettersi al server!";  // return your result
                }
                else
                    return null;
            }
            else
                return null;
        }
    }

    @Action
    public void DeleteTask() {
        if (!this.jList1.isSelectionEmpty())
        {
            Object[] options = {"Yes",
                    "No"};
            int n = JOptionPane.showOptionDialog(this.mainPanel,
                "Do you want to delete this task? ",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
            if(n == 0)
            {
                workflow.getTasks().remove(workflow.getTask(this.jList1.getSelectedIndex()));
                model.remove(this.jList1.getSelectedIndex());
            }
        }
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
