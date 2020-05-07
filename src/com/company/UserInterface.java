package com.company;

import javax.swing.*;
import java.awt.*;

class UserInterface extends JFrame {
  public static final int WIDTH = 1920;
  public static final int HEIGHT = 1080;

  public Elevator ele[];
  private ExternalButtonPanel exPanel;//电梯外部按钮面板
  private Thread eleThread[];//电梯线程

  public UserInterface (String uiName) {
    setTitle(uiName);
    setSize(WIDTH,HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridLayout(1,6));
    Container mainPanel=getContentPane();

    ele=new Elevator[5];
    exPanel=new ExternalButtonPanel(ele);
    eleThread=new Thread[5];

    mainPanel.add(exPanel);

    for(int i = 0; i < 5; i++) {
      ele[i]=new Elevator(i + 1);
      mainPanel.add(ele[i]);
      eleThread[i]=new Thread(ele[i]);//创建电梯线程
      eleThread[i].start();
    }
  }
}
