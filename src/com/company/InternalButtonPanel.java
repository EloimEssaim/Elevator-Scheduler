package com.company;

import javax.swing.*;
import java.awt.*;

class InternalButtonPanel extends JPanel {
  private JToggleButton[]internalButton;

  public InternalButtonPanel () {
    setPreferredSize(new Dimension(150,120));
    setLayout(new GridLayout(6,4));
    setBorder(BorderFactory.createTitledBorder("panel"));

    internalButton=new JToggleButton[23];
    for (int i = 1;i < 21;i++) {
      internalButton[i-1]=new JToggleButton(""+i);
      internalButton[i-1].setSize(30,30);
      add(internalButton[i-1]);
    }

    internalButton[20]=new JToggleButton("open");
    internalButton[21]=new JToggleButton("close");
    internalButton[22]=new JToggleButton("alarm");
    add(internalButton[20]);
    add(internalButton[21]);
    add(internalButton[22]);
  }

  public JToggleButton[] getInternalButton() {
    return internalButton;
  }


}
