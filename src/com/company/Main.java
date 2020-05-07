package com.company;

enum ElevatorState {
  IDLE,
  UP,
  DOWN,
  OPEN_DOOR,
  CLOSE_DOOR,
  ALARM
}

public class Main {
  public static void main (String[] args) {
    UserInterface ui = new UserInterface("ElevatorUI");//创建图形界面
    ui.show();
  }
}


