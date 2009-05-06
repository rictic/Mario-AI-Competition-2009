package ch.idsia.mario.engine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;


public class Art
{
    public static Image[][] mario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    public static Image[][] bg;
    public static Image[][] map;
    public static Image[][] endScene;
    public static Image[][] gameOver;
    public static Image logo;
    public static Image titleScreen;

    public static void init(GraphicsConfiguration gc)
    {
        try
        {
            final String curDir = System.getProperty("user.dir");
            String img = curDir + "/../img/";
            System.out.println("Image Directory: " + img);
            img = "";
            mario = cutImage(gc, img + "mariosheet.png", 32, 32);
            smallMario = cutImage(gc, img + "smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, img + "firemariosheet.png", 32, 32);
            enemies = cutImage(gc, img + "enemysheet.png", 16, 32);
            items = cutImage(gc, img + "itemsheet.png", 16, 16);
            level = cutImage(gc, img + "mapsheet.png", 16, 16);
            map = cutImage(gc, img + "worldmap.png", 16, 16);
            particles = cutImage(gc, img + "particlesheet.png", 8, 8);
            bg = cutImage(gc, img + "bgsheet.png", 32, 32);
            logo = getImage(gc, img + "logo.gif");
            titleScreen = getImage(gc, img + "title.gif");
            font = cutImage(gc, img + "font.gif", 8, 8);
            endScene = cutImage(gc, img + "endscene.gif", 96, 96);
            gameOver = cutImage(gc, img + "gameovergost.gif", 96, 64);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException
    {
        ImageReader pngReader = ImageIO.getImageReadersBySuffix("png").next ();

        System.out.println("trying to get " + imageName);
        File file = new File(imageName);
        System.out.println("File: " + file + ", exists " + file.exists());
        BufferedImage source = ImageIO.read(file);
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException
    {
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++)
        {
            for (int y = 0; y < source.getHeight(null) / ySize; y++)
            {
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }

        return images;
    }

}