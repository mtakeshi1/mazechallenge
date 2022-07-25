package mc.challenge.maze;
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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mc.ChallengeImpl;
import mc.drawing.DrawHelper;

public class GraphicalMain extends ApplicationAdapter {
    private Camera cam;
    private SpriteBatch batch;
    private DrawHelper drawHelper;
    private HeadlessMain mazeRunner;

    @Override
    public void create() {

        batch = new SpriteBatch();

        cam = new OrthographicCamera(800, 800);
        cam.position.set(10f, 10f, 10f);
        cam.update();


        Maze maze = MazeFactory.getEmptyMap(200, 200);
        drawHelper = new DrawHelper(
                batch,
                8,
                100,
                100,
                maze
        );

        mazeRunner = new HeadlessMain(
                new ChallengeImpl(),
                maze
        );

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);

        if (Math.abs(cam.position.x - drawHelper.getPlayerx()) > 300) {
            cam.position.x = drawHelper.getPlayerx();
        }
        if (Math.abs(cam.position.y - drawHelper.getPlayery()) > 300) {
            cam.position.y = drawHelper.getPlayery();
        }

        cam.update();

        mazeRunner.doMove();
        batch.begin();
        drawHelper.draw();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}
