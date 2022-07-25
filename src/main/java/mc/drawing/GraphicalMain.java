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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import mc.ChallengeImpl;
import mc.challenge.maze.HeadlessMain;
import mc.challenge.maze.Maze;
import mc.challenge.maze.MazeFactory;

public class GraphicalMain extends ApplicationAdapter {
    private Camera cam;
    private SpriteBatch batch;
    private GraphicalHelper graphicalHelper;
    private HeadlessMain mazeRunner;

    @Override
    public void create() {

        batch = new SpriteBatch();

        cam = new OrthographicCamera(800, 800);


//        Maze maze = MazeFactory.getEmptyMap(200, 200);
        Maze maze = MazeFactory.getScatterMap(200, 200);
        graphicalHelper = new GraphicalHelper(
                batch,
                8,
                100,
                100,
                maze,
                new ChallengeImpl()
        );

//        mazeRunner = new HeadlessMain(
//                new ChallengeImpl(),
//                maze
//        );

    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);

        if (Math.abs(cam.position.x - graphicalHelper.getPlayerx()) > 300) {
            cam.position.x = graphicalHelper.getPlayerx();
        }
        if (Math.abs(cam.position.y - graphicalHelper.getPlayery()) > 300) {
            cam.position.y = graphicalHelper.getPlayery();
        }

        cam.update();

        graphicalHelper.doMove();

        batch.begin();
        graphicalHelper.draw();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}
