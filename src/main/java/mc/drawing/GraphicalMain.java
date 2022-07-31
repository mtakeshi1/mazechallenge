package mc.drawing;
/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import mc.Configuration;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static mc.Configuration.MAZES;
import static mc.Configuration.WINDOW_HEIGHT;
import static mc.Configuration.WINDOW_WIDTH;

/**
 * This used libGDX to run a graphical program.
 * from here helpers are created to deal with the challenge part.
 */
public class GraphicalMain extends ApplicationAdapter {
    private Camera cam;
    private SpriteBatch batch;
    private GraphicalHelper graphicalHelper;

    private BitmapFont font;

    int mazecount = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        cam = new OrthographicCamera(Configuration.WINDOW_WIDTH, Configuration.WINDOW_HEIGHT);

        graphicalHelper = new GraphicalHelper(
                batch,
                MAZES.get(mazecount).get()
        );

        font = new BitmapFont();

    }

    @Override
    public void render() {

        if (graphicalHelper.finished() && mazecount < MAZES.size()) {
            graphicalHelper.stop();


            mazecount++;
            if (mazecount < MAZES.size()) {
                graphicalHelper = new GraphicalHelper(
                        batch,
                        MAZES.get(mazecount).get()
                );

            }
        }
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);

        if (Math.abs(cam.position.x - graphicalHelper.getPlayerx()) > 300) {
            cam.position.x = graphicalHelper.getPlayerx();
        }
        if (Math.abs(cam.position.y - graphicalHelper.getPlayery()) > 300) {
            cam.position.y = graphicalHelper.getPlayery();
        }
        if (graphicalHelper.finished()) {
            cam.position.x = WINDOW_WIDTH / 2;
            cam.position.y = WINDOW_HEIGHT / 2;
            cam.position.z = 0;
        }

        cam.update();
        batch.begin();

        if (graphicalHelper.finished()) {
            graphicalHelper.stop();

            int position = 500;
            int smallspace = 20;
            int biggerspace = 30;
            var info = graphicalHelper.getFinishInfo();
            font.draw(batch, "MAZETYPE:", 10, position);
            position -= smallspace;
            font.draw(batch, info.mazename(), 10, position);
            position -= biggerspace;
            font.draw(batch, "DIMENSIONS:", 10, position);
            position -= smallspace;
            font.draw(batch, info.rows() + "x" + info.cols(), 10, position);
            position -= biggerspace;
            font.draw(batch, "TILES EXPLORED:", 10, position);
            position -= smallspace;
            font.draw(batch, "" + info.tilesExplored(), 10, position);
            position -= biggerspace;
            font.draw(batch, "TIME TAKEN MS:", 10, position);
            position -= smallspace;
            font.draw(batch, "" + info.timeTakenMS(), 10, position);
            position -= biggerspace;
            font.draw(batch, "CHALLENGER:", 10, position);
            position -= smallspace;
            font.draw(batch, "" + info.challengeEntry(), 10, position);

        }

        graphicalHelper.draw();
        batch.end();
    }

    @Override
    public void dispose() {
        try {
            var info = graphicalHelper.getFinishInfo();
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, true);
            // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            for (int i = 4; i <= pixels.length; i += 4) {
                pixels[i - 1] = (byte) 255;
            }
            Pixmap pixmap = new Pixmap(WINDOW_WIDTH, WINDOW_HEIGHT, Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

//            int biggestX = 0;
//            int biggestY = 0;
//            int black = pixmap.getPixel(0, 0);
//            for (int x = 0; x < WINDOW_WIDTH; x++) {
//                for (int y = 0; y < WINDOW_HEIGHT; y++) {
//                    var p = pixmap.getPixel(x, y);
//                    if (p != black) {
//                        biggestX = Math.max(biggestX, x);
//                        biggestY = Math.max(biggestY, y);
//                    }
//                }
//
//            }
//
//            var newpm = new Pixmap(biggestX, biggestY, Pixmap.Format.RGBA8888);
//            for (int x = 0; x < biggestX + 200; x++) {
//                for (int y = 0; y < biggestY + 200; y++) {
//                    newpm.drawPixel(x, y, pixmap.getPixel(x, y));
//                }
//
//            }


            FileHandle fh = new FileHandle(Gdx.files.getLocalStoragePath() + "data/latest.png");
            PixmapIO.writePNG(fh, pixmap);
            pixmap.dispose();

        } catch (Exception e) {
            throw new RuntimeException("ohno: " + e.getMessage());
        }


        graphicalHelper.stop();
        batch.dispose();
    }


    static String getFileName() {
        return "";
    }
}
