package com.buddha.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.buddha.simulation.Properties;
import com.buddha.simulation.Simulation;
import com.buddha.simulation.SimulationScreen;

public class GUI {
	
	public Skin skin;
	public Stage stage;
	public SimulationScreen screen;
	public OrthographicCamera camera;
	public Table optionsTable = new Table();
	public Table mainTable = new Table();
	public TextField seedTextField;
	public boolean seedTextFieldHasFocus = false;
	
	public GUI(SimulationScreen screen) {
		this.screen = screen;
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		camera = new OrthographicCamera();
		stage = new Stage(new ScreenViewport(camera));
		Gdx.input.setInputProcessor(stage);
		mainTable.setFillParent(true);
		buildTable();
		stage.addActor(mainTable);
	}
	
	public void buildTable() {
		final Window optionsWindow = new Window("Universe Options", skin);
		optionsWindow.add(optionsTable).padTop(25);
		optionsWindow.setMovable(false);	
		float pad = 5;
		optionsTable.add(getSlider("temperature",screen.simulationProperties)).width(300).pad(pad).row();
		optionsTable.add(getSlider("reaction speed",screen.simulationProperties)).width(300).pad(pad).row();
		optionsTable.add(getSlider("force magnitude",screen.simulationProperties)).width(300).pad(pad).row();
		optionsTable.add(getSlider("minimum radius",screen.simulationProperties)).width(300).pad(pad).row();
		optionsTable.add(getSlider("maximum radius",screen.simulationProperties)).width(300).pad(pad).row();
		optionsTable.add(getNumberField("number of elements", screen.simulationProperties, 1)).pad(pad).row();
		optionsTable.add(getNumberField("number of particles", screen.simulationProperties, 10)).row();
		//seedLabel
		final Label seedLabel = new Label(screen.simulation.seed, skin);
		//restartbutton
		TextButton restartButton = new TextButton("random universe", skin);
		restartButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				screen.simulation = new Simulation(screen.simulationProperties, Simulation.randomString(), 
						screen.simulation.mainContainer.width, screen.simulation.mainContainer.height);
				seedLabel.setText("Seed: "+ screen.simulation.seed);
			}
		});
		optionsTable.add(restartButton).space(5).row();
		optionsTable.add(seedLabel).space(5).row();
		//set seed option
		Table seedSetTable = new Table();
		TextButton fromSeedButton = new TextButton("From Seed", skin);
		seedTextField = new TextField("seed", skin);
		seedTextField.addListener(new ClickListener() {
			public void clicked(InputEvent e, float x, float y) {
				seedTextFieldHasFocus = true;
			}
		});
		fromSeedButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				screen.simulation = new Simulation(screen.simulationProperties, seedTextField.getText(), 
						screen.simulation.mainContainer.width, screen.simulation.mainContainer.height);
				seedLabel.setText("Seed: "+ screen.simulation.seed);
				stage.unfocus(seedTextField);
				seedTextFieldHasFocus = false;
			}	
		});
		seedSetTable.add(fromSeedButton).space(5);
		seedSetTable.add(seedTextField);
		optionsTable.add(seedSetTable).row();
		optionsTable.setFillParent(true);
		final Table tempTable = new Table();
		tempTable.add(optionsWindow).top().left();
		//closebutton
		final TextButton closeButton = new TextButton("x", skin,"toggle"); 
		closeButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				tempTable.setVisible(!tempTable.isVisible());
			}
		});
		mainTable.top().left().add(closeButton).size(20,20).left().row();
		mainTable.top().left().add(tempTable);
	}
	
	public void render() {
		if(Gdx.input.isTouched()) {
			int mx = Gdx.input.getX();
			int my = stage.getViewport().getScreenHeight()-Gdx.input.getY();
			if (stage.hit(mx, my, true)==null) {
				stage.unfocusAll();
				seedTextFieldHasFocus = false;
			}
		}
		stage.act();
		stage.draw();
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	public Table makeTextField(final String name, Properties properties) {
		Table textFieldTable = new Table();
		
		return textFieldTable;
	}
	
	public Table getNumberField(final String name, final Properties properties, final int stepSize) {
		Table numberFieldTable = new Table();
		int val = properties.getI(name);
		final int min = properties.getIMin(name);
		final int max = properties.getIMax(name);
		numberFieldTable.add(new Label(name, skin));
		final Label valueLabel = new Label(String.format("%d", val), skin);
		final TextButton minus = new TextButton("-", skin);
		final TextButton plus = new TextButton("+", skin);
		ChangeListener listener = new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				int nVal;
				if(actor==minus) {
					nVal = Math.max(min, properties.getI(name)-stepSize);
				} else {
					nVal = Math.min(max,properties.getI(name)+stepSize);
				}
				properties.setIProperty(name, nVal);
				valueLabel.setText(String.format("%d", nVal));
			}
		};
		minus.addListener(listener);
		plus.addListener(listener);
		numberFieldTable.add(minus);
		numberFieldTable.add(valueLabel).width(30);
		numberFieldTable.add(plus);
		return numberFieldTable;
	}
	
	public Table getSlider(final String name, final Properties properties) {
		Table sliderTable = new Table();
		float min = properties.getFMin(name);
		float max = properties.getFMax(name);
		float val = properties.getF(name);
		final Label label = new Label(name + String.format(" %.2f", val), skin);
		sliderTable.left().add(label).left();
		final Slider slider = new Slider(min, max, (max-min)/100f, false, skin);
		slider.setValue(val);
		slider.addListener(new ChangeListener() {
			public void changed(ChangeEvent e, Actor a) {
				label.setText(name + String.format(" %.2f", properties.getF(name)));
				screen.simulationProperties.setFProperty(name, slider.getValue());
				screen.simulation.updateProperties();
			}
		});
		sliderTable.right().add(slider);
		return sliderTable;
	}
	
	public void dispose() {
		stage.dispose();
		skin.dispose();
	}
}
