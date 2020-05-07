package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

class ExternalButtonPanel extends JPanel {

  private JButton []externalUpButton;//外部上行按钮
  private JButton []externalDownButton;//外部下行按钮

  public ExternalButtonPanel (Elevator lift[]) {
    setPreferredSize(new Dimension(100,700));
    externalUpButton=new JButton[20];
    externalDownButton=new JButton[20];
    setLayout(new GridLayout(20,2));
    for (int i = 20;i > 0;i--) {
      externalUpButton[i-1]=new JButton("up"+i);
      externalUpButton[i-1].setPreferredSize(new Dimension(45,25));
      externalUpButton[i-1].addActionListener(new externalUpButtonListener(lift));
      add(externalUpButton[i-1]);

      externalDownButton[i-1]=new JButton("down"+i);
      externalDownButton[i-1].setPreferredSize(new Dimension(45,25));
      externalDownButton[i-1].addActionListener(new externalDownButtonListener(lift));
      add(externalDownButton[i-1]);
    }//画出外部按钮面板

    externalUpButton[19].setEnabled(false);//20层向上的外部按钮无法按下
    externalDownButton[0].setEnabled(false);//1层向下的外部按钮无法按下

  }

  private class externalUpButtonListener implements ActionListener {
    private Elevator eleOfListener[];
    public externalUpButtonListener (Elevator eleOfConstructor[])
    {
      eleOfListener=eleOfConstructor;
    }

    public void actionPerformed (ActionEvent e) {
      String s=e.getActionCommand();
      int externalUpRequestFloor=Integer.parseInt(s.substring(2));
      externalUpTaskScheduler(externalUpRequestFloor);
    }

    public void externalUpTaskScheduler (int requestFloor) {
      int distance[]=new int[5];

      for(int i = 0;i < 5;i++) {
        if (eleOfListener[i].getState() != ElevatorState.ALARM) {
          //如果有电梯处于alarm状态则不能分配
          if (eleOfListener[i].getState() == ElevatorState.IDLE) {
            distance[i]=Math.abs(eleOfListener[i].getCurrentFloor() - requestFloor);
          } else if (eleOfListener[i].getState() == ElevatorState.UP) {

            if (eleOfListener[i].getCurrentFloor() <= requestFloor) {
              //电梯在外部请求楼层之下
              distance[i] = requestFloor - eleOfListener[i].getCurrentFloor();
            } else if (eleOfListener[i].getCurrentFloor() == requestFloor) {
              //电梯与外部请求楼层相同
              distance[i] = requestFloor-eleOfListener[i].getCurrentFloor();
            } else if (eleOfListener[i].getCurrentFloor() > requestFloor) {
              //电梯在外部请求楼层之上
              distance[i] = 20 - eleOfListener[i].getCurrentFloor() + 20 - requestFloor;
            }
          }  else if (eleOfListener[i].getState() == ElevatorState.DOWN) {

            if (eleOfListener[i].getCurrentFloor() < requestFloor) {
              distance[i] = eleOfListener[i].getCurrentFloor() + requestFloor;
            } else if (eleOfListener[i].getCurrentFloor() == requestFloor) {
              distance[i] = 0;
            } else if (eleOfListener[i].getCurrentFloor() > requestFloor) {
              distance[i] = eleOfListener[i].getCurrentFloor() - requestFloor;
            }
          } else{

          }
        }

      }

      int minDistance=distance[0];

      for (int j = 0;j < 5;j++) {
        if(distance[j] < minDistance)
          minDistance=distance[j];
      }

      LinkedList<Integer> eleIndex=new LinkedList<>();

      for (int m = 0;m < 5;m++) {
        if(distance[m] == minDistance)
          eleIndex.add((Integer)m);
      }

      int index=eleIndex.getFirst();

      eleOfListener[index].taskScheduler(requestFloor);

    }

  }//外部向上请求按钮监听

  private class externalDownButtonListener implements ActionListener {
    public Elevator eleOfListener[];

    public externalDownButtonListener (Elevator eleOfConstructor[])
    {
      eleOfListener=eleOfConstructor;
    }

    public void actionPerformed (ActionEvent e) {
      String s=e.getActionCommand();
      int externalDownRequestFloor=Integer.parseInt(s.substring(4));
      externalDownTaskScheduler(externalDownRequestFloor);
    }

    public void externalDownTaskScheduler (int requestFloor) {
      int distance[] = new int[5];

      for (int i = 0;i < 5;i++) {
        if (eleOfListener[i].getState() == ElevatorState.IDLE) {
          distance[i]=Math.abs(eleOfListener[i].getCurrentFloor()-requestFloor);
        } else if (eleOfListener[i].getState() == ElevatorState.UP) {
          if (eleOfListener[i].getCurrentFloor() < requestFloor) {
            distance[i] = requestFloor - eleOfListener[i].getCurrentFloor();
          } else if (eleOfListener[i].getCurrentFloor() == requestFloor) {
            distance[i] = 0;
          } else if(eleOfListener[i].getCurrentFloor() > requestFloor) {
            distance[i] = 20 - eleOfListener[i].getCurrentFloor() + 20 - requestFloor;
          }
        } else if (eleOfListener[i].getState() == ElevatorState.DOWN) {
          if (eleOfListener[i].getCurrentFloor() < requestFloor) {
            distance[i] = eleOfListener[i].getCurrentFloor()+requestFloor;
          } else if (eleOfListener[i].getCurrentFloor() == requestFloor) {
            distance[i]=eleOfListener[i].getCurrentFloor() - requestFloor;
          } else if(eleOfListener[i].getCurrentFloor() > requestFloor) {
            distance[i] = eleOfListener[i].getCurrentFloor()-requestFloor;
          }
        }
      }

      int minDistance = distance[0];

      for (int j = 0;j < 5;j++) {
        if (distance[j] < minDistance)
          minDistance = distance[j];
      }//找出最近距离的电梯

      LinkedList<Integer>eleIndex=new LinkedList<>();

      for(int m = 0;m < 5;m++) {
        if(distance[m]==minDistance)
          eleIndex.add((Integer)m);
      }

      int index=eleIndex.getFirst();//如果两个电梯距离相同则插入序号在前的电梯

      eleOfListener[index].taskScheduler(requestFloor);

    }

  }//外部向下请求按钮监听






}