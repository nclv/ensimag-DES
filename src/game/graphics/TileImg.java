package game.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gui.GraphicalElement;

/**
 * Custom tile image class.
 * Une image est la superposition d'une image de type de case et de possibles images d'entités et dessin de feu.
 * 
 * @see BufferedImage
 * @author Nicolas Vincent
 */
public class TileImg implements GraphicalElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TileImg.class);
    
    /** 
     * Image de fond (type de case)
     */
    private BufferedImage tileBackgroundImg = null;
    
    /**
     * Liste des images affichées par dessus l'image de fond (entités)
     */
    private ArrayList<BufferedImage> tileForegroundImgsArray = null;

    private final int tileBackgroundImgSize;

    private final int x;
    private final int y;

    /**
     * Intensité normalisée du feu à afficher
     */
    private int fireNormalizedIntensity = 0;

    /**
     * @param x
     * @param y
     * @param tileBackgroundImgSize
     * @param tileBackgroundImg
     */
    public TileImg(final int x, final int y, final int tileBackgroundImgSize, final BufferedImage tileBackgroundImg) {
        LOGGER.info("Création d'une image représentant une case de taille {} en ({}, {})", tileBackgroundImgSize, x, y);
        this.tileBackgroundImgSize = tileBackgroundImgSize;
        this.tileBackgroundImg = tileBackgroundImg;

        this.x = x;
        this.y = y;
    }

    public void setTileForegroundImgsArray(final ArrayList<BufferedImage> tileForegroundImgsArray) {
        this.tileForegroundImgsArray = tileForegroundImgsArray;
    }

    public void setFireNormalizedIntensity(final int fireNormalizedIntensity) {
        this.fireNormalizedIntensity = fireNormalizedIntensity;
    }

    /**
     * Affichage de l'image finale, superposition de l'image de fond, des dessins et des images du premier plan.
     * L'ordre des instructions est IMPORTANT. On superpose des images et dessins.
     * Cette méthode est appelée à chaque interaction avec la fenêtre (update)
     * 
     * @param g2d
     * @see Graphics2D#drawImage(java.awt.Image, int, int, int, int, java.awt.image.ImageObserver)
     * @see Graphics2D#setColor(Color)
     * @see Graphics2D#fillOval(int, int, int, int)
     */
    @Override
    public void paint(final Graphics2D g2d) {
        // Affichage de l'image de fond (nature du terrain)
        if (this.tileBackgroundImg != null) {
            g2d.drawImage(this.tileBackgroundImg, this.x, this.y, this.tileBackgroundImgSize,
                    this.tileBackgroundImgSize, null);
        }

        // Affichage de l'incendie
        if (this.fireNormalizedIntensity != 0) {
            LOGGER.debug("Assignation d'un dessin de feu en ({}, {}) d'intensite normalisée {}", this.x, this.y,
                this.fireNormalizedIntensity);
            g2d.setColor(Color.RED);
            final int firePadding = (this.tileBackgroundImgSize - this.fireNormalizedIntensity) / 2; // on veut un feu
                                                                                                     // au milieu de la
                                                                                                     // case
            g2d.fillOval(this.x + firePadding, this.y + firePadding, this.fireNormalizedIntensity,
                    this.fireNormalizedIntensity);
        }

        // Affichage du ou des robot(s)
        // TODO: revoir l'affichage de plusieurs robots sur la même case
        // coin en haut à gauche: imgsPadding = this.tileImgSize / 8, width = height =
        // this.tileImgSize / 4
        // au milieu: imgsPadding = this.tileImgSize / 4, width = height =
        // this.tileImgSize / 2
        if (this.tileForegroundImgsArray != null && !this.tileForegroundImgsArray.isEmpty()) {
            LOGGER.debug("Assignation d'une liste d'images (de robots) en ({}, {})", this.x, this.y);
            final int imgsPadding = this.tileBackgroundImgSize / 4;
            for (int index = 0; index < this.tileForegroundImgsArray.size(); index++) {
                final BufferedImage foregroundImg = this.tileForegroundImgsArray.get(index);
                g2d.drawImage(foregroundImg, this.x + index * imgsPadding, this.y, imgsPadding, imgsPadding, null);
            }
        }
    }

    @Override
    public String toString() {
        String res = "Intensité du feu: " + this.fireNormalizedIntensity + "\n";
        res += "Robot présent: " + (this.tileForegroundImgsArray != null);
        return res;
    }
}