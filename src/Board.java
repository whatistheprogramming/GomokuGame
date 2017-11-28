import javax.swing.*;
import java.awt.*;

public class Board extends JPanel
{
    public static int MARGIN = 30; //边距
     public static int SPAN = 35;  //行列间距
     public static int ROWS = 15;
     public static int COLS = 15;

    public Board()
    {

        setLayout(null);
//        setBackground(Color.ORANGE);
//
        setBackground(Color.BLUE);
        setPreferredSize(new Dimension(600, 600));
        setOpaque(true);

    }

    //根据x,y的位置返回行列坐标
     public static Dimension getPosition(int i, int j)
     {
         int x = MARGIN + (j - 1) * SPAN - Chess.SIZE / 2;
         int y = MARGIN + (i - 1) * SPAN - Chess.SIZE / 2;
         return new Dimension(x, y);
     }

    //绘制
     public void paintComponent(Graphics g)
     {
         g.setColor(Color.BLACK);

         int len = (ROWS - 1) * SPAN;
         for (int i = 0; i < ROWS; i++)
         {
             int x1 = MARGIN;
             int y1 = MARGIN + i * SPAN;
             int x2 = x1 + len;
             int y2 = y1;
             g.drawLine(x1, y1, x2, y2);
             g.drawLine(y1, x1, y2, x2);
         }
     }



}
