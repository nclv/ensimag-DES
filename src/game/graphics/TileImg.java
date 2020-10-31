package game.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gui.GraphicalElement;

public class TileImg implements GraphicalElement {
    private static final Logger LOGGER = LoggerFactory.getLogger(TileImg.class);
    
    private BufferedImage tileBackgroundImg = null;
    private ArrayList<BufferedImage> tileForegroundImgsArray = null;

    private final int tileBackgroundImgSize;

    private final int x;
    private final int y;

    private int fireNormalizedIntensity = 0;

    public TileImg(final int x, final int y, final int tileBackgroundImgSize, final BufferedImage tileBackgroundImg) {
        LOGGER.info("Création d'une image représentant une case de taille {} en ({}, {})", tileBackgroundImgSize, x, y);
        this.tileBackgroundImgSize = tileBackgroundImgSize;
        this.tileBackgroundImg = tileBackgroundImg;

        this.x = x;
        this.y = y;
    }

    public void setTileForegroundImgsArray(final ArrayList<BufferedImage> tileForegroundImgsArray) {
        LOGGER.info("Assignation d'une liste d'images (de robots) en ({}, {})", this.x, this.y);
        this.tileForegroundImgsArray = tileForegroundImgsArray;
    }

    public void setFireNormalizedIntensity(final int fireNormalizedIntensity) {
        LOGGER.info("Assignation d'un dessin de feu en ({}, {}) d'intensite normalisée {}", this.x, this.y,
                fireNormalizedIntensity);
        this.fireNormalizedIntensity = fireNormalizedIntensity;
    }

    @Override
    public void paint(final Graphics2D g2d) {
        // L'ordre est IMPORTANT. On superpose des images et dessins.
        // Cette méthode est appelée à chaque interaction avec la fenêtre (update)

        // Affichage de l'image de fond (nature du terrain)
        if (this.tileBackgroundImg != null) {
            g2d.drawImage(this.tileBackgroundImg, this.x, this.y, this.tileBackgroundImgSize,
                    this.tileBackgroundImgSize, null);
        }

        // Affichage de l'incendie
        if (this.fireNormalizedIntensity != 0) {
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