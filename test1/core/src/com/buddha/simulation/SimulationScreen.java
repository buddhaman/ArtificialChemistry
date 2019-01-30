package com.buddha.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.buddha.gui.GUI;
import com.buddha.physics.Particle;
import com.buddha.simulation.WorldButton.ClickListener;

public class SimulationScreen implements Screen {

	public Simulation simulation;
	public SimulationRenderer renderer;
	public int simulationSpeed = 1;
	public GUI gui;
	public Properties simulationProperties;
	public boolean selected;
	public Array<ChemistryBook> chemistryBooks = new Array<ChemistryBook>();
	public Array<WorldButton> worldButtons = new Array<WorldButton>();
	public WorldButton sizeButton;
	public WorldButton draggedButton;
	public Array<SplashParticle> splashParticles = new Array<SplashParticle>();

	public SimulationScreen() {
		simulationProperties = new Properties();
		simulationProperties.setFProperty("temperature", 0.85f, 0f, 1f);
		simulationProperties.setFProperty("reaction speed", 0.01f, 0f, 0.05f);
		simulationProperties.setFProperty("force magnitude", 0.045f, 0.01f, 0.4f);
		simulationProperties.setIProperty("number of elements", 6, 1, 20);
		simulationProperties.setIProperty("number of particles", 700, 10, 2000);
		simulationProperties.setFProperty("minimum radius", 8, 4, 40);
		simulationProperties.setFProperty("maximum radius", 20, 4, 40);

		simulation = new Simulation(simulationProperties, Simulation.randomString(), 200, 120);
		gui = new GUI(this);
		renderer = new SimulationRenderer();

		// scan button
		WorldButton scanButton = new WorldButton(simulation.analyser.pos.x + simulation.analyser.width,
				simulation.analyser.pos.y, 4, 4);
		scanButton.addListener(new ClickListener() {
			public void click() {
				Simulation sim = getSimulation();
				int[] types = new int[sim.typesInAnalyser.size];
				for (int i = 0; i < sim.typesInAnalyser.size; i++) {
					types[i] = sim.typesInAnalyser.get(i);
				}
				ChemistryBook book = new ChemistryBook();
				book.displayTypes(types, simulation.reactions, simulation.numTypes);
				addChemistryBook(book);
			}
		});
		worldButtons.add(scanButton);
		sizeButton = new WorldButton(simulation.mainContainer.width + 2.5f, simulation.mainContainer.height + 2.5f, 5,
				5);
		worldButtons.add(sizeButton);
	}

	public void addChemistryBook(ChemistryBook book) {
		chemistryBooks.add(book);
	}

	public Simulation getSimulation() {
		return simulation;
	}

	public void handleInput() {
		float mx = Gdx.input.getX();
		float my = Gdx.input.getY();
		Vector2 worldPos = renderer.cam.screenToWorld(mx, my);
		float speed = renderer.cam.scale * 5;
		if (Gdx.input.isKeyPressed(Keys.W)) {
			renderer.cam.y += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.A)) {
			renderer.cam.x -= speed;
		}
		if (Gdx.input.isKeyPressed(Keys.S)) {
			renderer.cam.y -= speed;
		}
		if (Gdx.input.isKeyPressed(Keys.D)) {
			renderer.cam.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.Z)) {
			renderer.cam.scale *= 0.985f;
		}
		if (Gdx.input.isKeyPressed(Keys.X)) {
			renderer.cam.scale /= 0.985f;
		}
		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			simulationSpeed = Math.min(128, simulationSpeed * 2);
			System.out.println("SimulationSpeed = " + simulationSpeed + " x");
		}
		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			simulationSpeed = Math.max(1, simulationSpeed / 2);
			System.out.println("SimulationSpeed = " + simulationSpeed + " x");
		}
		if (Gdx.input.isTouched()) {
			if (selected == false) {
				selected = true;
				simulation.select(worldPos.x, worldPos.y, 5);
			}
			if (draggedButton == null) {
				if (sizeButton.contains(worldPos.x, worldPos.y))
					draggedButton = sizeButton;
			}
			simulation.drag(worldPos.x, worldPos.y);
			if (draggedButton != null) {
				draggedButton.pos.set(worldPos.x - 2.5f, worldPos.y - 2.5f);
				simulation.mainContainer.width = draggedButton.pos.x - 2.5f;
				simulation.mainContainer.height = draggedButton.pos.y - 2.5f;
			}
		} else {
			if (selected) { // release
				Array<Particle> noContainer = simulation.drop(worldPos.x, worldPos.y);
				for (Particle p : noContainer) {
					for (int i = 0; i < 2; i++) {
						SplashParticle sp = new SplashParticle(p.pos.x, p.pos.y, p.getXV(), p.getYV(), 1f, 0.3f, p.type);
						addSplashParticle(sp);
					}
				}
				selected = false;
				click(worldPos);
				draggedButton = null;
			}
		}
		updateButtons(worldPos, Gdx.input.isTouched());
	}
	
	@Override
	public void render(float delta) {
		if(!gui.seedTextFieldHasFocus)
			handleInput();
		for (int i = 0; i < simulationSpeed; i++)
			simulation.update();
		for (ChemistryBook book : chemistryBooks) {
			book.update();
		}
		for (int i = splashParticles.size - 1; i >= 0; i--) {
			SplashParticle p = splashParticles.get(i);
			if (p.remove) {
				splashParticles.removeIndex(i);
				continue;
			} else {
				p.update();
			}
		}
		renderer.render(this);
		gui.render();
	}

	public void addSplashParticle(SplashParticle sp) {
		splashParticles.add(sp);
	}

	public void updateButtons(Vector2 worldPosition, boolean down) {
		for (WorldButton worldButton : worldButtons) {
			if (worldButton.contains(worldPosition.x, worldPosition.y)) {
				if (down)
					worldButton.state = WorldButton.PRESSED;
				else
					worldButton.state = WorldButton.HOVER;
			} else {
				worldButton.state = WorldButton.IDLE;
			}
		}
	}

	public void click(Vector2 worldPosition) {
		for (WorldButton worldButton : worldButtons) {
			if (worldButton.contains(worldPosition.x, worldPosition.y)) {
				worldButton.clicked();
			}
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {
		renderer.resize(width, height);
		gui.resize(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		renderer.dispose();
		gui.dispose();
	}

}
