package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.lang.Integer;
/**
 * Created by chrischen on 2017/4/27.
 */

enum elevatorState
{
    IDLE,
    UP,
    DOWN,
    OPEN_DOOR,
    CLOSE_DOOR,
    ALARM
}

class upOrder implements Comparator<Integer>
{
    public int compare(Integer floor1, Integer floor2 )
    {
        return (floor1-floor2);
    }//上行链表从低到高排序
}

class downOrder implements Comparator<Integer>
{
    public int compare(Integer floor1, Integer floor2 )
    {
        return (floor2-floor1);
    }//下行链表从高到低排序
}


public class Main
{
    public static void main(String[] args)
    {
        userInterface ui = new userInterface("ElevatorUI");//创建图形界面
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.show();
    }
}

class userInterface extends JFrame
{
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    public elevator ele[];
    private externalButtonPanel exPanel;//电梯外部按钮面板
    private Thread eleThread[];//电梯线程

    public userInterface(String str)
    {
        setTitle("Elevator Scheduler");
        setSize(WIDTH,HEIGHT);
        setLayout(new GridLayout(1,6));
        Container mainPanel=getContentPane();


        ele=new elevator[5];
        exPanel=new externalButtonPanel(ele);
        eleThread=new Thread[5];

        mainPanel.add(exPanel);

        for(int i=0;i<5;i++)
        {
            ele[i]=new elevator(i+1);
            mainPanel.add(ele[i]);
            eleThread[i]=new Thread(ele[i]);//创建电梯线程
            eleThread[i].start();
        }
    }
}


class externalButtonPanel extends JPanel
{

    private JButton []externalUpButton;//外部上行按钮
    private JButton []externalDownButton;//外部下行按钮

    public externalButtonPanel(elevator lift[])//传入ele数组参数（为了访问每个电梯的上下请求）
    {

        setPreferredSize(new Dimension(100,700));
        externalUpButton=new JButton[20];
        externalDownButton=new JButton[20];
        setLayout(new GridLayout(20,2));
        for(int i=20;i>0;i--)
        {
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

    private class externalUpButtonListener implements ActionListener
    {
        private elevator eleOfListener[];
        public externalUpButtonListener(elevator eleOfConstructor[])
        {
            eleOfListener=eleOfConstructor;
        }

        public void actionPerformed(ActionEvent e)
        {
            String s=e.getActionCommand();
            int externalUpRequestFloor=Integer.parseInt(s.substring(2));
            externalUpTaskScheduler(externalUpRequestFloor);

        }

        public void externalUpTaskScheduler(int requestFloor)//外部请求分配
        {
            int distance[]=new int[5];

            for(int i=0;i<5;i++)
            {
                if(eleOfListener[i].getState()!=elevatorState.ALARM)//如果有电梯处于alarm状态则不能分配
                {

                    if(eleOfListener[i].getState()==elevatorState.IDLE)//电梯静止
                    {
                        distance[i]=Math.abs(eleOfListener[i].getCurrentFloor()-requestFloor);
                    }

                    else if(eleOfListener[i].getState()==elevatorState.UP)//电梯上行
                    {
                        if(eleOfListener[i].getCurrentFloor()<=requestFloor)//电梯在外部请求楼层之下
                        {
                            distance[i]=requestFloor-eleOfListener[i].getCurrentFloor();
                        }
                        else if(eleOfListener[i].getCurrentFloor()==requestFloor)//电梯与外部请求楼层相同
                        {
                            distance[i]=requestFloor-eleOfListener[i].getCurrentFloor();
                        }
                        else if(eleOfListener[i].getCurrentFloor()>requestFloor)//电梯在外部请求楼层之上
                        {
                            distance[i]=20-eleOfListener[i].getCurrentFloor()+20-requestFloor;
                        }
                    }

                    else if(eleOfListener[i].getState()==elevatorState.DOWN)//电梯下行
                    {
                        if(eleOfListener[i].getCurrentFloor()<requestFloor)//电梯在外部请求楼层之下
                        {
                            distance[i]=eleOfListener[i].getCurrentFloor()+requestFloor;
                        }
                        else if(eleOfListener[i].getCurrentFloor()==requestFloor)//电梯与外部请求楼层相同
                        {
                            distance[i]=0;
                        }
                        else if(eleOfListener[i].getCurrentFloor()>requestFloor)//电梯在外部请求楼层之上
                        {
                            distance[i]=eleOfListener[i].getCurrentFloor()-requestFloor;
                        }
                    }

                    else//电梯正在开关门
                    {

                    }
                }




            }

            int minDistance=distance[0];

            for(int j=0;j<5;j++)
            {
                System.out.println(distance[j]);
                if(distance[j]<minDistance)
                    minDistance=distance[j];
            }

            LinkedList<Integer>eleIndex=new LinkedList<>();

            for(int m=0;m<5;m++)
            {
                if(distance[m]==minDistance)
                    eleIndex.add((Integer)m);
            }

            int index=eleIndex.getFirst();

            eleOfListener[index].taskScheduler(requestFloor);



        }


    }//外部向上请求按钮监听

    private class externalDownButtonListener implements ActionListener
    {
        public elevator eleOfListener[];


        public externalDownButtonListener(elevator eleOfConstructor[])
        {
            eleOfListener=eleOfConstructor;
        }

        public void actionPerformed(ActionEvent e)
        {
            String s=e.getActionCommand();
            int externalDownRequestFloor=Integer.parseInt(s.substring(4));
            externalDownTaskScheduler(externalDownRequestFloor);
        }

        public void externalDownTaskScheduler(int requestFloor)
        {
            int distance[]=new int[5];

            for(int i=0;i<5;i++)
            {
                if(eleOfListener[i].getState()==elevatorState.IDLE)//电梯静止
                {
                    distance[i]=Math.abs(eleOfListener[i].getCurrentFloor()-requestFloor);
                }

                else if(eleOfListener[i].getState()==elevatorState.UP)//电梯上行
                {
                    if(eleOfListener[i].getCurrentFloor()<requestFloor)//电梯在外部请求楼层之下
                    {
                        distance[i]=requestFloor-eleOfListener[i].getCurrentFloor();
                    }
                    else if(eleOfListener[i].getCurrentFloor()==requestFloor)//电梯与外部请求楼层相同
                    {
                        distance[i]=0;
                    }
                    else if(eleOfListener[i].getCurrentFloor()>requestFloor)//电梯在外部请求楼层之上
                    {
                        distance[i]=20-eleOfListener[i].getCurrentFloor()+20-requestFloor;
                    }
                }

                else if(eleOfListener[i].getState()==elevatorState.DOWN)//电梯下行
                {
                    if(eleOfListener[i].getCurrentFloor()<requestFloor)//电梯在外部请求楼层之下
                    {
                        distance[i]=eleOfListener[i].getCurrentFloor()+requestFloor;
                    }
                    else if(eleOfListener[i].getCurrentFloor()==requestFloor)//电梯与外部请求楼层相同
                    {
                        distance[i]=eleOfListener[i].getCurrentFloor()-requestFloor;
                    }
                    else if(eleOfListener[i].getCurrentFloor()>requestFloor)//电梯在外部请求楼层之上
                    {
                        distance[i]=eleOfListener[i].getCurrentFloor()-requestFloor;
                    }
                }
            }

            int minDistance=distance[0];

            for(int j=0;j<5;j++)
            {
                System.out.println(distance[j]);
                if(distance[j]<minDistance)
                    minDistance=distance[j];
            }//找出最近距离的电梯

            LinkedList<Integer>eleIndex=new LinkedList<>();

            for(int m=0;m<5;m++)
            {
                if(distance[m]==minDistance)
                    eleIndex.add((Integer)m);
            }

            int index=eleIndex.getFirst();//如果两个电梯距离相同则插入序号在前的电梯

            eleOfListener[index].taskScheduler(requestFloor);


        }

    }//外部向下请求按钮监听






}

class internalButtonPanel extends JPanel
{
    private JToggleButton[]internalButton;
    public internalButtonPanel()
    {

        setPreferredSize(new Dimension(150,120));
        setLayout(new GridLayout(6,4));
        setBorder(BorderFactory.createTitledBorder("panel"));

        internalButton=new JToggleButton[23];
        for(int i=1;i<21;i++)
        {

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

    public JToggleButton[] getInternalButton()
    {
        return internalButton;
    }


}


class elevator extends JPanel implements Runnable//整个elevator的显示面板，包括运行面板和楼层面板
{
    public static final double UNIT_MOVE_TIME=1;
    public static final double OPEN_TIME=3;
    public static final double CLOSE_TIME=0.2;
    private elevatorDisplay position;//显示电梯在哪里
    private internalButtonPanel inPanel;//电梯内部请求面板
    private int currentFloor;
    private LinkedList<Integer> upTasks;//上行链表
    private LinkedList<Integer> downTasks;//下行链表
    private elevatorState state;
    private int idNum;

    public elevator(int id)
    {
        idNum=id;
        setPreferredSize(new Dimension(150,1080));
        setBorder(BorderFactory.createTitledBorder("Elevator"+id));
        position=new elevatorDisplay(id);
        inPanel=new internalButtonPanel();
        setLayout(new BorderLayout());
        add(position,BorderLayout.NORTH);
        add(inPanel,BorderLayout.SOUTH);

        upTasks=new LinkedList<Integer>();
        downTasks=new LinkedList<Integer>();

        state=elevatorState.IDLE;//设置电梯初始状态
        currentFloor=1;//电梯初始在1层

        for(int i=0;i<20;i++)
            inPanel.getInternalButton()[i].addActionListener(internalButtonListener);//加入内部楼层按钮监听
        inPanel.getInternalButton()[20].addActionListener(internalOpenDoorButtonListener);//加入内部开门按钮监听
        inPanel.getInternalButton()[21].addActionListener(internalCloseDoorButtonListener);//加入内部关门按钮监听
        inPanel.getInternalButton()[22].addActionListener(internalAlarmButtonListener);//加入内部报警按钮监听

    }


    public elevatorState getState()
    {
        return state;
    }

    public LinkedList<Integer> getUpTasks()
    {
        return upTasks;
    }

    public LinkedList<Integer> getDownTasks()
    {
        return downTasks;
    }

    public int getCurrentFloor()
    {
        return currentFloor;
    }

    public void taskScheduler(int targetFloor)//内部请求调度函数
    {
        if(targetFloor>=currentFloor)//?????????targetfloor=currentfloor
        {
            upTasks.add(targetFloor);
            Collections.sort(upTasks,new upOrder());//上行任务从低到高排列

        }//目标楼层大于当前楼层
        else if(targetFloor<currentFloor)
        {
            downTasks.add(targetFloor);
            Collections.sort(downTasks,new downOrder());//下行任务从高到低排列

        }//目标楼层小于当前楼层
    }


    ActionListener internalButtonListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            String s=e.getActionCommand();
            int targetFloor=Integer.parseInt(s);
            taskScheduler(targetFloor);
        }
    };

    ActionListener internalOpenDoorButtonListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            state=elevatorState.OPEN_DOOR;
        }
    };

    ActionListener internalCloseDoorButtonListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            state=elevatorState.CLOSE_DOOR;
        }
    };

    ActionListener internalAlarmButtonListener=new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            state=elevatorState.ALARM;
        }
    };


    public void setDisplay()//设置电梯当前位置显示
    {
        switch (state)
        {
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
                position.getElevatorPosition()[currentFloor-1].setBackground(Color.RED);
                break;

        }
        for(int i=0;i<20;i++)
        {
            if(i!=currentFloor-1)
            {
                position.getElevatorPosition()[i].setBackground(Color.LIGHT_GRAY);//若不是当前楼层则恢复默认背景色
                position.getElevatorPosition()[i].setLabel((i+1)+"F");//若不是当前楼层则恢复默认楼层标签

            }
        }
    }

    public void checkTasks()
    {

    }


    public void run() {
        while (true) {
            if (state == elevatorState.IDLE)//若电梯静止
            {
                if (upTasks.isEmpty() && downTasks.isEmpty())//如果上下队列都为空则静止
                {
                    setDisplay();
                } else if (!upTasks.isEmpty() && downTasks.isEmpty())//上行队列不为空，下行队列为空，处理上行队列
                {
                    state = elevatorState.UP;
                } else if (upTasks.isEmpty() && !downTasks.isEmpty()) //上行队列为空，下行队列不为空，处理下行队列
                {
                    state = elevatorState.DOWN;
                } else if (!upTasks.isEmpty() && !downTasks.isEmpty())//上下队列都不为空
                {
                    if ((currentFloor - downTasks.getFirst()) - (upTasks.getFirst()) < 0)//离上行队列更近先处理上行请求
                    {
                        state = elevatorState.UP;
                    } else if ((currentFloor - downTasks.getFirst()) - (upTasks.getFirst()) > 0)//离下行队列更近先处理下行请求
                    {
                        state = elevatorState.DOWN;
                    } else//距离相等时先处理上行请求
                    {
                        state = elevatorState.UP;
                    }
                }
            } else if (state == elevatorState.UP)//若电梯上行
            {

                while (!upTasks.isEmpty())//上行队列不为空时
                {
                    for (; currentFloor <= upTasks.getLast(); currentFloor++) {

                        setDisplay();
                        if (upTasks.contains(currentFloor))//每到达一层则检查在不在队列中，若在则出队
                        {
                            inPanel.getInternalButton()[upTasks.poll() - 1].setSelected(false);//到达此层则取消内部选取这层的按钮
                            state = elevatorState.OPEN_DOOR;//电梯开门
                            setDisplay();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                            state = elevatorState.CLOSE_DOOR;//关门
                            setDisplay();

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                        }


                        if (upTasks.isEmpty() && !downTasks.isEmpty())
                        {
                            state = elevatorState.DOWN;//上行队列处理完改为下行
                            break;
                        }
                        else if (upTasks.isEmpty() && downTasks.isEmpty())
                        {
                            state = elevatorState.IDLE;
                            break;
                        }
                        else
                        {
                            state = elevatorState.UP;//继续上行
                            try
                            {
                                Thread.sleep(1000);

                            }
                            catch (InterruptedException e)
                            {
                                System.out.println("Interrupted");
                            }

                        }

                    }
                }
            } else if (state == elevatorState.DOWN)//若电梯下行
            {
                while (!downTasks.isEmpty())//下行队列不为空时
                {
                    for (; currentFloor >= downTasks.getLast(); currentFloor--) {

                        setDisplay();
                        if (downTasks.contains(currentFloor))//每到达一层则检查在不在队列中，若在则出队
                        {
                            inPanel.getInternalButton()[downTasks.poll() - 1].setSelected(false);//到达此层则取消内部选取这层的按钮
                            state = elevatorState.OPEN_DOOR;//电梯开门
                            setDisplay();

                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }


                            state = elevatorState.CLOSE_DOOR;//关门
                            setDisplay();

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }

                        }

                        if (downTasks.isEmpty() && upTasks.isEmpty()) {
                            state = elevatorState.IDLE;
                            break;
                        } else if (downTasks.isEmpty() && !upTasks.isEmpty()) {
                            state = elevatorState.UP;//下行队列处理完改为上行
                            break;
                        } else {
                            state = elevatorState.DOWN;//继续下行
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                System.out.println("Interrupted");
                            }

                        }
                    }
                }
            } else if (state == elevatorState.ALARM) {
                setDisplay();
                /*try
                {
                    Thread.sleep(2000);
                    Thread.interrupted();

                }
                catch (InterruptedException e)
                {
                    System.out.println("elevator"+idNum+"alarm");
                }
                }*/

            }
        }
    }
}

class elevatorDisplay extends JPanel
{
    private JButton[]elevatorPosition;

    public elevatorDisplay(int id)
    {
        setSize(150,500);
        setLayout(new GridLayout(20,1));
        elevatorPosition=new JButton[20];
        for(int i=20;i>0;i--)
        {
            elevatorPosition[i-1]=new JButton(i+"F");
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



