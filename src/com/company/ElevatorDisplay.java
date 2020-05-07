package com.company;

import javax.swing.*;
import java.awt.*;

class ElevatorDisplay extends JPanel {
  private JButton[] elevatorPosition;

  public ElevatorDisplay() {
    setSize(150,500);
    setLayout(new GridLayout(20,1));
    elevatorPosition = new JButton[20];
    for (int i = 20; i > 0; i--) {
      elevatorPosition[i-1] = new JButton(i+"F");
      elevatorPosition[i-1].setBackground(Color.LIGHT_GRAY);
      elevatorPosition[i-1].setSize(55,30);
      elevatorPosition[i-1].setEnabled(false);
      elevatorPosition[i-1].setOpaque(true);
      add(elevatorPosition[i-1]);
    }
  }

  public JButton[] getElevatorPosition()
  {
    return elevatorPosition;
  }

}
