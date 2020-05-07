package com.company;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Runnable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

class Elevator extends JPanel implements Runnable {
    private ElevatorDisplay position;//显示电梯在哪里
    private InternalButtonPanel inPanel;//电梯内部请求面板
    private int currentFloor;
    private LinkedList<Integer> upTasks;//上行链表
    private LinkedList<Integer> downTasks;//下行链表
    private ElevatorState state;

    public Elevator(int id) {
        setPreferredSize(new Dimension(150,1080));
        setBorder(BorderFactory.createTitledBorder("Elevator" + id));
        position = new ElevatorDisplay(id);
        inPanel = new InternalButtonPanel();
        setLayout(new BorderLayout());
        add(position,BorderLayout.NORTH);
        add(inPanel,BorderLayout.SOUTH);

        upTasks = new LinkedList<>();
        downTasks = new LinkedList<>();

        state = ElevatorState.IDLE;//设置电梯初始状态
        currentFloor = 1;//电梯初始在1层

        for(int i = 0; i < 20; i++) {
            inPanel.getInternalButton()[i].addActionListener(internalButtonListener);//加入内部楼层按钮监听
        }

        inPanel.getInternalButton()[20].addActionListener(internalOpenDoorButtonListener);//加入内部开门按钮监听
        inPanel.getInternalButton()[21].addActionListener(internalCloseDoorButtonListener);//加入内部关门按钮监听
        inPanel.getInternalButton()[22].addActionListener(internalAlarmButtonListener);//加入内部报警按钮监听
    }


    public ElevatorState getState() {
        return state;
    }

    public int getCurrentFloor()
    {
        return currentFloor;
    }

    //内部请求调度函数
    public void taskScheduler (int targetFloor) {
        if (targetFloor >= currentFloor) {
            upTasks.add(targetFloor);
            Collections.sort(upTasks, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 - o2;
                }
            });//上行任务从低到高排列
        } else if (targetFloor < currentFloor) {
            downTasks.add(targetFloor);
            Collections.sort(downTasks, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2 - o1;
                }
            });//下行任务从高到低排列
        }
    }

    ActionListener internalButtonListener = new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            String s = e.getActionCommand();
            int targetFloor = Integer.parseInt(s);
            taskScheduler(targetFloor);
        }
    };

    ActionListener internalOpenDoorButtonListener = new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            state = ElevatorState.OPEN_DOOR;
        }
    };

    ActionListener internalCloseDoorButtonListener = new ActionListener() {
        public void actionPerformed (ActionEvent e) {
            state = ElevatorState.CLOSE_DOOR;
        }
    };

    ActionListener internalAlarmButtonListener=new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            state = ElevatorState.ALARM;
        }
    };

    public void setDisplay() {
        switch (state) {
            case IDLE:
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.ORANGE);
                break;
            case CLOSE_DOOR:
                position.getElevatorPosition()[currentFloor-1].setLabel("CLOSE");
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.ORANGE);
                break;
            case OPEN_DOOR:
                position.getElevatorPosition()[currentFloor-1].setLabel("OPEN");
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.ORANGE);
                break;
            case UP:
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.PINK);
                break;
            case DOWN:
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.BLUE);
                break;
            case ALARM:
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.RED);break;
        }

        for(int i = 0;i < 20;i++) {
            if(i != currentFloor - 1) {
                position.getElevatorPosition()[i].setBackground(Color.LIGHT_GRAY);//若不是当前楼层则恢复默认背景色
                position.getElevatorPosition()[i].setLabel((i+1)+"F");//若不是当前楼层则恢复默认楼层标签
            }
        }
    }


    public void run() {
        while (true) {
            if (state == ElevatorState.IDLE) {
                if (upTasks.isEmpty() && downTasks.isEmpty()) {
                    //如果上下队列都为空则静止
                    setDisplay();
                } else if (!upTasks.isEmpty() && downTasks.isEmpty()) {
                    //上行队列不为空，下行队列为空，处理上行队列
                    state = ElevatorState.UP;
                } else if (upTasks.isEmpty() && !downTasks.isEmpty()) {
                    //上行队列为空，下行队列不为空，处理下行队列
                    state = ElevatorState.DOWN;
                } else if (!upTasks.isEmpty() && !downTasks.isEmpty()) {
                    //上下队列都不为空
                    if ((currentFloor - downTasks.getFirst()) - (upTasks.getFirst()) < 0) {
                        //离上行队列更近先处理上行请求
                        state = ElevatorState.UP;
                    } else if ((currentFloor - downTasks.getFirst()) - (upTasks.getFirst()) > 0) {
                        //离下行队列更近先处理下行请求
                        state = ElevatorState.DOWN;
                    } else {
                        //距离相等时先处理上行请求
                        state = ElevatorState.UP;
                    }
                }
            } else if (state == ElevatorState.UP) {
                //若电梯上行
                while (!upTasks.isEmpty()) {
                    for (; currentFloor <= upTasks.getLast(); currentFloor++) {
                        setDisplay();
                        //每到达一层则检查在不在队列中，若在则出队
                        if (upTasks.contains(currentFloor)) {
                            inPanel.getInternalButton()[upTasks.poll() - 1].setSelected(false);//到达此层则取消内部选取这层的按钮
                            state = ElevatorState.OPEN_DOOR;//电梯开门
                            setDisplay();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                            state = ElevatorState.CLOSE_DOOR;//关门
                            setDisplay();

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                        }


                        if (upTasks.isEmpty() && !downTasks.isEmpty()) {
                            state = ElevatorState.DOWN;//上行队列处理完改为下行
                            break;
                        } else if (upTasks.isEmpty() && downTasks.isEmpty()) {
                            state = ElevatorState.IDLE;
                            break;
                        } else {
                            state = ElevatorState.UP;//继续上行
                            try {
                                Thread.sleep(1000);
                            }
                            catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }

                        }

                    }
                }
            } else if (state == ElevatorState.DOWN) {
                while (!downTasks.isEmpty()) {
                    for (; currentFloor >= downTasks.getLast(); currentFloor--) {
                        setDisplay();
                        if (downTasks.contains(currentFloor)) {
                            //每到达一层则检查在不在队列中，若在则出队
                            inPanel.getInternalButton()[downTasks.poll() - 1].setSelected(false);//到达此层则取消内部选取这层的按钮
                            state = ElevatorState.OPEN_DOOR;//电梯开门
                            setDisplay();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                            state = ElevatorState.CLOSE_DOOR;//关门
                            setDisplay();

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }

                        }

                        if (downTasks.isEmpty() && upTasks.isEmpty()) {
                            state = ElevatorState.IDLE;
                            break;
                        } else if (downTasks.isEmpty() && !upTasks.isEmpty()) {
                            state = ElevatorState.UP;//下行队列处理完改为上行
                            break;
                        } else {
                            state = ElevatorState.DOWN;//继续下行
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }

                        }
                    }
                }
            } else if (state == ElevatorState.ALARM) {
                setDisplay();
            }
        }
    }
}



