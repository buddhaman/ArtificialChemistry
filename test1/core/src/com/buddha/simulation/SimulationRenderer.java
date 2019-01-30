package com.buddha.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.buddha.physics.Particle;

public class SimulationRenderer {
	
	public SpriteBatch batch;
	public TextureAtlas atlas;
	public AtlasRegion circle;
	public AtlasRegion square;
	public RenderUtils utils;
	public Camera cam;
	public AssetManager assetManager;
	
	public static final Color[] colors = new Color[] {new Color(0,1,0,1),
			new Color(1,0,0,1), new Color(0,0,1,1), new Color(1,1,0,1), new Color(1, 0, 1, 1),
			new Color(0, 1, 1, 1), Color.BROWN, Color.OLIVE, Color.FIREBRICK, Color.CORAL};
	
	public SimulationRenderer() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		assetManager.load("spritesheet.txt", TextureAtlas.class);
		assetManager.finishLoading();
		atlas = assetManager.get("spritesheet.txt", TextureAtlas.class);
		System.out.println(atlas);
		cam = new Camera();
		circle = atlas.findRegion("circle");
		square = atlas.findRegion("blank");
		utils = new RenderUtils(batch, square);
	}
	
	public void render(SimulationScreen screen) {
		cam.update();
		batch.setProjectionMatrix(cam.cam.combined);
		Simulation sim = screen.simulation;
		batch.begin();
		
		for(ChemistryBook book : screen.chemistryBooks) {
			renderChemistryBook(book);
		}
		batch.setColor(Color.BLACK);
		batch.draw(square, ChemistryBook.X_POS-1, ChemistryBook.Y_POS-1+ChemistryBook.HEIGHT, ChemistryBook.WIDTH+2, ChemistryBook.HEIGHT+2);
		batch.setColor(Color.GRAY);
		batch.draw(square, ChemistryBook.X_POS-1, ChemistryBook.Y_POS+ChemistryBook.HEIGHT-0.7f, ChemistryBook.WIDTH+2, 9);
		batch.setColor(Color.DARK_GRAY);
		batch.draw(square, ChemistryBook.X_POS-1, ChemistryBook.Y_POS+ChemistryBook.HEIGHT-0.7f, ChemistryBook.WIDTH+2, 1.2f);
		
		for(WorldButton worldButton : screen.worldButtons) {
			render(worldButton);
		}
		
		for(Container container : sim.containers) {
			renderContainer(container);
		}
		
		renderAnalyser(screen.simulation);
		
		if(screen.selected)
		for(Particle p : sim.selection) {
			renderStretched(p.pos.x, p.pos.y, p.getXV(), p.getYV(), 1f, p.type);
		}
		
		for(SplashParticle p : screen.splashParticles) {
			renderStretched(p.pos.x, p.pos.y, p.vel.x, p.vel.y, p.getRadius(), p.type);
		}
		
		batch.end();
	}
	
	public void drawArrow(float x1, float y1, float x2, float y2, float width) {
		utils.drawLine(x1, y1, x2, y2, width);
		RenderUtils.drawLine(batch, x1, y1, (x1*0.2f+x2*0.8f), (y1*0.2f+y2*0.8f), width*2, 0, square);
	}
	
	public void renderAnalyser(Simulation sim) {
		Container container = sim.analyser;
		Array<Integer> elementTypes = sim.typesInAnalyser;
		for(int i = 0; i < elementTypes.size; i++) {
			batch.setColor(colors[elementTypes.get(i)%colors.length]);
			int idx = elementTypes.get(i);
			batch.draw(circle, 2*idx+container.pos.x, container.pos.y-3, 2, 2);
		}
	}
	
	public void renderContainer(Container container) {
		batch.setColor(Color.WHITE);
		if(container.shape==Container.SQUARE_SHAPE) {
			utils.drawLineRect(container.pos.x, container.pos.y, container.width, container.height, cam.scale);
		} else if (container.shape == Container.CIRCLE_SHAPE) {
			//draw line circle
		}
		for(Particle p : container.particles) {
			renderStretched(p.pos.x, p.pos.y, p.getXV(), p.getYV(), 1, p.type);
		}
	}
	
	public void renderSplashParticle() {
		
	}
	
	public void renderStretched(float x, float y, float dx, float dy, float radius, int type) {
		if(dx==0 && dy==0) {
			renderParticle(x, y, radius, type);
			return;
		}
		float l = (float)(Math.sqrt(dx*dx+dy*dy));
		float factor = Math.min(2, 1+l);
		float w = 1/factor;
		dx*=factor/l;
		dy*=factor/l;
		batch.setColor(colors[type%colors.length]);
		RenderUtils.drawLine(batch, x-dx, y-dy, x+dx, y+dy, w*radius, w*radius, circle);
	}
	
	public void renderChemistryBook(ChemistryBook book) {
		float firstCellSize = 3f;
		float lineWidth = cam.scale;
		float cellW = (book.width-firstCellSize)/(book.types.length);
		float cellH = (book.height-firstCellSize)/(book.types.length);
		int nElements = book.types.length;
		batch.setColor(0.17f, 0.17f, 0.1f, 1);
		batch.draw(square, book.pos.x, book.pos.y, book.width, book.height);
		batch.setColor(Color.WHITE);
		utils.drawLineRect(book.pos.x, book.pos.y, book.width, book.height, lineWidth);
		for(int i = 0; i < nElements; i++) {
			utils.drawLine(book.pos.x, book.pos.y+book.height-firstCellSize-cellH*i, 
					book.width+book.pos.x, book.pos.y+book.height-firstCellSize-cellH*i, lineWidth);
			utils.drawLine(book.pos.x+firstCellSize+cellW*i, book.pos.y, book.pos.x+firstCellSize+cellW*i,book.pos.y+book.height, lineWidth);
		}
		for(int i = 0; i < nElements; i++) {
			batch.setColor(colors[book.types[i]%colors.length]);
			utils.drawCircle(circle, book.pos.x+firstCellSize/2, book.pos.y+book.height-firstCellSize-cellH*0.5f-cellH*i, 1);
			utils.drawCircle(circle, book.pos.x+firstCellSize+cellW*0.5f+cellW*i, book.height+book.pos.y-firstCellSize/2, 1);
		}
		for(int i = 0; i < nElements; i++) {
			for(int j = 0; j < nElements; j++) {
				int t1 = book.types[i];
				int t2 = book.types[j];
				Reaction reaction = book.reactions[t1][t2];
				float cx = book.pos.x+i*cellW+cellW/2+firstCellSize;
				float cy = book.pos.y+book.height-j*cellH-cellH/2-firstCellSize;
				batch.setColor(colors[reaction.particle1To%colors.length]);
				utils.drawCircle(circle, cx-1, cy, 1);
				batch.setColor(colors[reaction.particle2To%colors.length]);
				utils.drawCircle(circle, cx+1, cy, 1);
			}
		}
	}
	
	public void render(WorldButton worldButton) {
		final Color[] buttonColors = new Color[] {
				new Color(.6f, .6f, .8f, 1), 
				new Color(.8f, .8f, 1, 1), 
				new Color(.3f, .3f, .6f, 1)};
		batch.setColor(buttonColors[worldButton.state]);
		batch.draw(square, worldButton.pos.x, worldButton.pos.y, worldButton.width, worldButton.height);
	}
	
	public void renderGraph(Array<Float> points, float yMin, float yMax, float x, float y, float height) {
		for(int i = 0; i < points.size-1; i++) {
			float a = height*(points.get(i)-yMin)/(yMax-yMin)+y;
			float b = height*(points.get(i+1)-yMin)/(yMax-yMin)+y;
			utils.drawLine(x+i, a, x+i+1, b, cam.scale*0.5f);
		}
	}
	
	public void renderParticle(float x, float y, float r, int type) {
		batch.setColor(colors[type%10]);
		batch.draw(circle, x-r, y-r, 2*r, 2*r);
	}
	
	public void resize(float width, float height) {
		cam.resize(width, height);
	}
	
	public void dispose() {
		atlas.dispose();
		batch.dispose();
		assetManager.dispose();
	}
}
