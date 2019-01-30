package com.buddha.simulation;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.buddha.physics.Particle;

public class Simulation {

	public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVW";
	public static final Random seedGenerator = new Random();
	// describe the rules of this universe
	public Properties properties;
	public float friction;
	public float reactionSpeed;
	public float forceMagnitude;
	public float minRadius;
	public float maxRadius;
	public int numTypes;
	public Interaction[][] interactions;
	public Reaction[][] reactions;
	
	public Array<Container> containers = new Array<Container>();
	public Container mainContainer;
	public Container analyser;
	
	public Array<Particle> selection = new Array<Particle>();
	public Array<Vector2> relativePos = new Array<Vector2>();
	
	public Array<Integer> typesInAnalyser = new Array<Integer>();
	
	public String seed;
	
	public Simulation(Properties properties, String seed, float width, float height) {
		this.seed = seed;
		MathUtils.random = new Random(seed.hashCode());
		this.properties = properties;
		this.numTypes = properties.getI("number of elements");
		updateProperties();
		interactions = new Interaction[numTypes][numTypes];
		reactions = new Reaction[numTypes][numTypes];
		randomUniverse();
		mainContainer = new Container(0, 0, width, height, numTypes);
		containers.add(mainContainer);
		int numPerType = properties.getI("number of particles")/numTypes;
		for (int t = 0; t < numTypes; t++) {
			for (int i = 0; i < numPerType; i++) {
				mainContainer.addParticle(mainContainer.pos.x +MathUtils.random(mainContainer.width), 
						mainContainer.pos.y+MathUtils.random(mainContainer.height), t);
			}
		}
		analyser = new Container(-100, 10, 50, 50, numTypes);
		containers.add(analyser);
	}
	
	public void updateProperties() {
		this.friction = properties.getF("temperature");
		this.forceMagnitude = properties.getF("force magnitude");
		this.reactionSpeed = properties.getF("reaction speed");
		this.minRadius = properties.getF("minimum radius");
		this.maxRadius = properties.getF("maximum radius");
	}

	public void RPSUniverse() {
		numTypes = 3;
		for (int i = 0; i < numTypes; i++) {
			for (int j = 0; j < numTypes; j++) {
				interactions[i][j] = new Interaction(i == j ? -1 : 1, 1);
				Reaction reaction;
				if (i == j) {
					reaction = new Reaction(i, i);
				} else if (j < i) {
					Reaction r =  reactions[j][i];
					reaction = new Reaction(r.particle2To, r.particle1To);
				} else { 
					if ((i + 1) % 3 == j) {
						reaction = new Reaction(j, j);
					} else {
						reaction = new Reaction(i, i);
					}
				}
				reactions[i][j] = reaction;
			}
		}
	}

	public void randomUniverse() {
		for (int i = 0; i < numTypes; i++) {
			for (int j = 0; j < numTypes; j++) {
				Interaction interaction = new Interaction(
						MathUtils.randomBoolean(1) ? MathUtils.random(-1.0f, 1.0f) : 0, MathUtils.random());
				interactions[i][j] = interaction;
				Reaction reaction;
				if (i == j) {
					reaction = new Reaction(i, i);
				} else if (j > i) {
					reaction = MathUtils.randomBoolean(1) ? new Reaction(MathUtils.random(numTypes - 1), MathUtils.random(numTypes - 1)) : 
						new Reaction(i, j);
				} else {
					Reaction r = reactions[j][i];
					reaction = new Reaction(r.particle2To, r.particle1To);
				}
				reactions[i][j] = reaction;
			}
		}
	}
	
	public void addContainer(Container container) {
		containers.add(container);
	}

	public void reaction(Container container, Particle p1, Particle p2) {
		Reaction reaction = reactions[p1.type][p2.type];
		container.changeType(p1, reaction.particle1To);
		container.changeType(p2, reaction.particle2To);
	}

	public void interaction(Container container, Interaction interaction, Particle p1, Particle p2) {
		float radius = this.minRadius+(this.maxRadius-this.minRadius)*interaction.maxRadius;
		float dst = p1.pos.dst(p2.pos);
		float dx = (p2.pos.x - p1.pos.x) / dst;
		float dy = (p2.pos.y - p1.pos.y) / dst;
		if (dst < 2f) {
			if (MathUtils.randomBoolean(reactionSpeed))
				reaction(container, p1, p2);
			float fac = 4*forceMagnitude*(2-dst);
			p1.addImpulse(-dx * fac, -dy * fac);
			p2.addImpulse(dx * fac, dy * fac);
		} else if (dst < radius) {
			float magnitude = forceMagnitude * interaction.force
					* (0.5f - Math.abs((dst - 2f) / (radius - 2f) - 0.5f)) * 2;
			p1.addImpulse(dx * magnitude, dy * magnitude);
		}
	}
	
	public void updateAnalyser() {
		Container container = analyser;
		for(int i = 0; i < container.particleTypes.size; i++) {
			Array<Particle> parts = container.particleTypes.get(i);
			if(parts.size>0) {
				if(!typesInAnalyser.contains(i, true))
					typesInAnalyser.add(i);
			}
		}
	}

	public void update() {
		updateAnalyser();
		for(Container container : containers) {
			container.particles.shuffle();
			for (int i = 0; i < container.particles.size; i++) {
				Particle p1 = container.particles.get(i);
				for (int j = 0; j < container.particles.size; j++) {
					if (i != j) {
						Particle p2 = container.particles.get(j);
						interaction(container, interactions[p1.type][p2.type], p1, p2);
					}
				}
			}
			for (int i = container.particles.size - 1; i >= 0; i--) {
				Particle p = container.particles.get(i);
				float dx = p.pos.x - p.oldPos.x;
				float dy = p.pos.y - p.oldPos.y;
				p.oldPos.set(p.pos);
				p.pos.add(dx * friction, dy * friction);
			}
			float dev = 0.01f;
			for (Particle p : container.particles) {
				if (p.pos.x < container.pos.x) {
					p.pos.x = container.pos.x+MathUtils.random() * dev;
				}
				if (p.pos.y < container.pos.y) {
					p.pos.y = container.pos.y+MathUtils.random() * dev;
				}
				if (p.pos.x > container.pos.x+container.width) {
					p.pos.x = container.width - MathUtils.random() * dev + container.pos.x;
				}
				if (p.pos.y > container.pos.y+container.height) {
					p.pos.y = container.height - MathUtils.random() * dev + container.pos.y;
				}
			}
		}
	}
	
	public void select(float x, float y, float radius) {
		Container container = mainContainer;
		for(Container c : containers) {
			if(c.contains(x, y)) {
				container = c;
			}
		}
		selection = getParticlesInRadius(container, x, y, radius);
		relativePos.clear();
		for(int i = 0; i < selection.size; i++) {
			Particle p = selection.get(i);
			relativePos.add(new Vector2(p.pos.x-x, p.pos.y-y));
			container.removeParticle(p);
		}
	}
	
	public void drag(float x, float y) {
		for(int i = 0; i < selection.size; i++) {
			Particle p = selection.get(i);
			p.oldPos.set(p.pos);
			p.pos.set(x+relativePos.get(i).x, y+relativePos.get(i).y);
		}
	}
	
	public Array<Particle> drop(float x, float y) {
		Array<Particle> noContainer = new Array<Particle>();
		for(Particle p : selection) {
			boolean inContainer = false;
			for(Container c : containers) {
				if(c.contains(p.pos.x, p.pos.y)) {
					c.addParticle(p);
					inContainer = true;
					break;
				}
			}
			if(!inContainer) {
				noContainer.add(p);
			}
		}
		return noContainer;
	}

	public Array<Particle> getParticlesInRadius(Container container, float x, float y, float radius) {
		Array<Particle> particles = new Array<Particle>();
		for(Particle p : container.particles) {
			if(p.pos.dst(x, y) < radius) {
				particles.add(p);
			}
		}
		return particles;
	}
	
	public static String randomString() {
		String rString = "";
		for(int i = 0; i < MathUtils.random(5, 15); i++) {
			rString+=ALLOWED_CHARS.charAt(seedGenerator.nextInt(ALLOWED_CHARS.length()));
		}
		return rString;
	}
}
