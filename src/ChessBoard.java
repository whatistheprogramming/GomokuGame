import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Stack;

public class ChessBoard extends JFrame   implements MouseListener
{

    //右边按钮的面板
    private JPanel jpanelBtn = new JPanel(null);

    private JButton newBtn = new JButton("新游戏");
    private JButton backBtn = new JButton("悔棋");
    private JButton quitBtn = new JButton("退出");

    //显示文本
    private JLabel text = new JLabel();

    private int step = 0; // 步数
    private int[][] b = new int[15][15]; // 棋盘数据表示
    //记录当前坐标的栈
    private Stack<Pos> stack = new Stack<>();

    //标题的高度
    private int menuHeight;

    private Board board; // 棋盘

    public ChessBoard()
    {
        setTitle("五子棋");
        setSize(800, 600);

        //添加按钮
        addBtn();

        //添加菜单
        addMenu();

        //添加棋盘
        addBoard();
        
        //添加鼠标点击事件
        addMouseListener(this);

        //设置主面板背景
        getContentPane().setBackground(Color.ORANGE);

        //清空后退栈
        stack.clear();

        //设置窗体居中显示
        setLocationRelativeTo(null);

        //设置关闭模式
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


    }

    private void addBoard()
    {
        board = new Board();
        add(board);
    }

    private void addMenu()
    {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu jMenu = new JMenu("菜单");
        JMenuItem preserveItem = new JMenuItem("保存");
        JMenuItem loadItem = new JMenuItem("载入");
        jMenu.add(preserveItem);
        jMenu.add(loadItem);
        jMenuBar.add(jMenu);
        setJMenuBar(jMenuBar);
        //获取窗口的高度
        menuHeight = jMenu.getPreferredSize().height;
//        System.out.println("height=" + menuHeight);

        //保存游戏
        preserveItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
                //打开文件保存框
                saveGame();

            }
        });

        //载入游戏
        loadItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                repaint();
                loadGame();
            }
        });
    }

    private void loadGame()
    {
        FileNameExtensionFilter filter=new FileNameExtensionFilter("*.txt","txt");
        JFileChooser fc=new JFileChooser();
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        int result = fc.showOpenDialog(this);    //showOpenDialog设置为打开方式

        if (result==JFileChooser.APPROVE_OPTION)
        {
            File file=fc.getSelectedFile();
            System.out.println(file);
            
            FileInputStream fis=null;
            try
            {

                fis = new FileInputStream(file);
                StringBuilder builder = new StringBuilder();
                //读取数据
                int len = 0;
                byte[] buf = new byte[1024];
                while ((len = fis.read(buf)) != -1)
                {
                    String s = new String(buf, 0, len);
                    builder.append(s);
//                    System.out.println(new String(buf,0,len));
                }

                //清空数据
                restart();

                setData(builder.toString());
                System.out.println(builder);
                repaint();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (fis != null)
                {
                    try
                    {
                        fis.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private void setData(String s)
    {
        int start = s.indexOf('=') + 1;
        int end = s.indexOf('[');
//        System.out.println("a=" + a + ",b=" + b);
        String str_step = s.substring(start, end).trim(); //trim()截去字符串两端的空格，但对于中间的空格不处理

        System.out.println("str_step=" + str_step+"test");

        try
        {
            step = Integer.valueOf(str_step);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        s = s.substring(s.indexOf("[") + 1);

        s = s.substring(0, s.length() - 1) + ",";
//        System.out.println(s.charAt(s.length()-1));

        System.out.println("now:s=" + s);
        for (int i = 0; i < 15; i++)
        {
            for (int j = 0; j < 15; j++)
            {
                String st = s.substring(0, s.indexOf(","));
                System.out.println("st=" + st);
                b[i][j] = Integer.valueOf(st);
                s = s.substring(s.indexOf(",") + 1);
            }
        }

        //画出棋子
        for (int i = 0; i < 15; i++)
        {
            for (int j = 0; j < 15; j++)
            {
                if (b[i][j] == 0) continue;

                Chess ch = null;
                if (b[i][j] == 1)
                {
                    ch = new Chess(true); // 生成黑棋
                }
                else if (b[i][j] == -1)
                {
                    ch = new Chess(false); // 生成白棋
                }
                // 生成一个棋子，放置在对应的行列交叉点上

                Dimension d = Board.getPosition(i + 1, j + 1);
                ch.setBounds(d.width, d.height, Chess.SIZE, Chess.SIZE);
                board.add(ch);
            }
        }

        if (step % 2 == 0)
        {
            text.setText("黑棋下");
        }
        else
        {
            text.setText("白棋下");
        }
    }

    public String printB()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < 15; i++)
        {
            for(int j=0;j<15;j++)
            {
                builder.append(b[i][j] + ",");
            }
        }
        builder.replace(builder.length() - 1, builder.length(), "");
        builder.append("]");
        System.out.println(builder);
        
        return builder.toString();

    }

    private void saveGame()
    {
        FileNameExtensionFilter filter=new FileNameExtensionFilter("*.txt","txt");
        JFileChooser fc=new JFileChooser();
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        int result=fc.showSaveDialog(this);
        if (result==JFileChooser.APPROVE_OPTION)
        {
            File file=fc.getSelectedFile();
            if (!file.getPath().endsWith(".txt"))
            {
                file=new File(file.getPath()+".txt");
            }
            System.out.println("file path="+file.getPath());
            FileOutputStream fos=null;
            try
            {
                if (!file.exists())
                {//文件不存在 则创建一个
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                String s = "step=" + step + "\r\n";  //这里 "\r\n" 为换行符
                s += printB();
//                s = s + "test";
                fos.write(s.getBytes());

                fos.flush();
            }
            catch (IOException e)
            {
                System.err.println("文件创建失败：");
                e.printStackTrace();
            }
            finally
            {
                if (fos != null)
                {
                    try
                    {
                        fos.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(this, "保存游戏成功");

            }
            
        }

    }

    private void addBtn()
    {
        jpanelBtn.setBackground(Color.ORANGE);
//        jpanelBtn.setOpaque(true);

        jpanelBtn.setPreferredSize(new Dimension(200, 600));
        add(jpanelBtn, BorderLayout.EAST);

//        jpanelBoard.setBackground(Color.BLUE);
//        jpanelBoard.setPreferredSize(new Dimension(600, 600));
//        jpanelBoard.setOpaque(true);
//        add(jpanelBoard, BorderLayout.CENTER);

        newBtn.setBounds(50, 120, 75, 36);
        backBtn.setBounds(50, 180, 75, 36);
        quitBtn.setBounds(50, 240, 75, 36);

        text.setBounds(58, 350, 90, 20);
        text.setText("欢迎进入游戏");

        //新游戏
        newBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //新游戏，抽重新初始化所有数据
                restart();
                text.setText("黑棋下");

            }
        });

        //退出游戏
        quitBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);

            }
        });

        //悔棋
        backBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("click");
                if (!stack.empty()&&step > 0)
                {
                    Pos p = stack.pop();
                    System.out.println("p.r=" + p.r + ",p.c=" + p.c);
                    b[p.r - 1][p.c - 1] = 0;
                    step--;
                    if (step % 2 == 0)
                    {
                        text.setText("黑棋下");
                    }
                    else
                    {
                        text.setText("白棋下");
                    }
                    //更新数据
                    updata();
                    repaint();
                    }
            }
        });
        //添加按钮到面板
        jpanelBtn.add(newBtn);
        jpanelBtn.add(backBtn);
        jpanelBtn.add(quitBtn);
        jpanelBtn.add(text);
    }

    private void updata()
    {
        //移除所有棋子
        board.removeAll();

        //重新画棋子
        updateChess();

    }

    private void updateChess()
    {
        //重新画出棋子
        for (int i = 0; i < 15; i++)
        {
            for (int j = 0; j < 15; j++)
            {
                if (b[i][j] == 0) continue;

                Chess ch = null;
                if (b[i][j] == 1)
                {
                    ch = new Chess(true); // 生成黑棋
                }
                else if (b[i][j] == -1)
                {
                    ch = new Chess(false); // 生成白棋
                }
                // 生成一个棋子，放置在对应的行列交叉点上

                Dimension d = Board.getPosition(i + 1, j + 1);
                ch.setBounds(d.width, d.height, Chess.SIZE, Chess.SIZE);
                board.add(ch);
            }
        }
    }

    /**
     * 重新开始游戏
     */
    private void restart()
    {
        step = 0;
        for (int i = 0; i < 15; i++)
        {
            for (int j = 0; j < 15; j++)
            {
                b[i][j] = 0;
            }
        }
        //移除所有棋子
        board.removeAll();

        stack.clear();

        //重新刷新
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        //获取标题栏的宽度
        Insets set=this.getInsets();
        int titlebarH=set.top;

        int x = e.getX() - 5; //5大约为边界的宽度
        int y = e.getY() - titlebarH - menuHeight;
        System.out.println("x=" + x + ",y=" + y);
        // 求鼠标点击位置所属的行和列
        int c = Math.round((x - Board.MARGIN) * 1.0f / Board.SPAN) + 1;
        int r = Math.round((y - Board.MARGIN) * 1.0f / Board.SPAN) + 1;
        System.out.println("r=" + r + ",c=" + c);
        if (check(r, c))
        {
            // 生成一个棋子，放置在对应的行列交叉点上
            Chess ch;
            String str_text = "";
            if (step % 2 == 0)
            {
                // 黑棋
                ch = new Chess(true); // 生成黑棋
                b[r - 1][c - 1] = 1;
                str_text = "白棋下";
            }
            else
            {
                ch = new Chess(false);
                // 生成白棋
                b[r - 1][c - 1] = -1;
                str_text = "黑棋下";
            }
            Dimension d = Board.getPosition(r, c);
            ch.setBounds(d.width, d.height, Chess.SIZE, Chess.SIZE);
            board.add(ch);
            repaint(); // 重绘

            text.setText(str_text);

            stack.push(new Pos(r, c));

            if (!judge(r, c))
            {
                step++;
            }
            else
            {
                //重新开始游戏
                restart();
            }
            printB();
        }
    }

    private boolean judge(int r, int c)
    {
        System.out.println("judge : r=" + r + ",c=" + c);
        //判断行
        int begin = Math.max(c - 4, 1);
        int end = Math.min(c + 4, 15);
        for (int i = begin; i <= end - 4; i++)
        {
            int s = 0;
            for (int j = 0; j < 5; j++)
            {
                s = s + b[r - 1][i - 1 + j];
            }
            if (s == 5)
            {
                JOptionPane.showMessageDialog(this, "黑方玩家赢了");
                return true;
            }
            else if (s == -5)
            {
                JOptionPane.showMessageDialog(this, "白方玩家赢了");
                return true;
            }
        }

        //判断列
        begin = Math.max(r - 4, 1);
        end = Math.min(r + 4, 15);
        for (int i = begin; i <= end - 4; i++)
        {
            int s = 0;
            for (int j = 0; j < 5; j++)
            {
                s = s + b[i - 1 + j][c - 1];
            }
            if (s == 5)
            {
                JOptionPane.showMessageDialog(this, "黑方玩家赢了");
                return true;
            }
            else if (s == -5)
            {
                JOptionPane.showMessageDialog(this, "白方玩家赢了");
                return true;
            }
        }



        //判断左对角线
        int mind = Math.min(r - 1, c - 1);    //mind为棋子(r,c)距离上边或左边边界最近的距离
        if (mind>4)
        {
            mind = Math.min(mind, 4);
        }

//        System.out.println("mind=" + mind);

        int beginR = Math.max(r - mind, 1);
        int beginC = Math.max(c - mind, 1);

        int mind2 = Math.min(15-r, 15 - c);        //mind2为棋子(r,c)距离下边或右边边界最近的距离
        if (mind2>4)
        {
            mind2 = Math.min(mind2, 4);
        }
//        System.out.println("mind2=" + mind2);


        int endR = Math.min(r + mind2, 15);
        int endC = Math.min(c + mind2, 15);

//        System.out.println("begin : r=" + beginR + ",c=" + beginC);
//        System.out.println("end : r=" + endR + ",c=" + endC);

        for (int i = beginR, j = beginC; i <= endR - 4 && j <= endC - 4; i++, j++)
        {

            int s = 0;
            for (int k = 0; k < 5; k++)
            {
                s = s + b[i - 1 + k][j - 1 + k];
            }
            if (s == 5)
            {
                JOptionPane.showMessageDialog(this, "黑方玩家赢了");
                return true;
            }
            else if (s == -5)
            {
                JOptionPane.showMessageDialog(this, "白方玩家赢了");
                return true;
            }
        }


        //判断右对角线
        mind = Math.min(r - 1, 15 - c);    //mind为棋子(r,c)距离上边或右边边界最近的距离
        if (mind>4)
        {
            mind = Math.min(mind, 4);
        }

//        System.out.println("mind=" + mind);
        beginR = Math.max(r - mind, 1);
        beginC = Math.min(15, c + mind);

         mind2 = Math.min(15-r, c - 1);        //mind2为棋子(r,c)距离下边或左边边界最近的距离
        if (mind2>4)
        {
            mind2 = Math.min(mind2, 4);
        }
//        System.out.println("mind2=" + mind2);


        endR = Math.min(r + mind2, 15);
        endC = Math.max(c - mind2, 1);

        System.out.println("begin : r=" + beginR + ",c=" + beginC);
        System.out.println("end : r=" + endR + ",c=" + endC);
        for (int i = beginR, j = beginC; i <= endR - 4 && j >= endC + 4; i++, j--)
        {

            int s = 0;
            for (int k = 0; k < 5; k++)
            {

//                if (i - 1 + k <0 || i - 1 + k >=15  || j - 1 - k < 0 || j - 1 - k >= 15)
//                {
//                    continue;
//                }
//
                if (i - 1 + k >= 0 && i - 1 + k < 15 && j - 1 + k >= 0 && j - 1 - k < 15)
                {
                    System.out.println("(" + (i - 1 + k) + "," + (j - 1 - k) + ")");
                    s = s + b[i - 1 + k][j - 1 - k];
                }
            }
            if (s == 5)
            {
                JOptionPane.showMessageDialog(this, "黑方玩家赢了");
                return true;
            }
            else if (s == -5)
            {
                JOptionPane.showMessageDialog(this, "白方玩家赢了");
                return true;
            }
        }

        //判断和棋
        if (step == 15 * 15)
        {
            JOptionPane.showMessageDialog(this, "和棋");
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }


    /**
     * 判断下子是否合法
     */
    private boolean check(int r, int c)
    {
        if (r >= 1 && r <= 15 && c >= 1 && c <= 15)
        {
            if (b[r - 1][c - 1] == 0) //未下子
            {
                return true;
            }
        }
        return false;
    }


    //开始游戏
    public static void main(String[] args)
    {
        new ChessBoard();
    }

    //内部坐标类
    static class Pos
    {
        int r;
        int c;
        public Pos()
        {
        }

        public Pos(int r, int c)
        {
            this.r = r;
            this.c = c;
        }
    }
}
