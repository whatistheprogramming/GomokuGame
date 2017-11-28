import javax.swing.*;
import java.awt.*;

public class Chess extends JPanel
{
    private boolean isBlack = true;  //黑
    public static int SIZE = 24;  //直径

    public Chess(boolean isBlack)
    {
        this.isBlack = isBlack;
    }

    //绘制
     public void paintComponent(Graphics g)
     {
         setOpaque(false);
        //设置棋子不透明

         if (isBlack)
         {
             g.setColor(Color.BLACK);
         }
         else
         {
             g.setColor(Color.WHITE);
         }

         g.fillOval(0, 0, SIZE, SIZE);

     }
}
