package gui;

import java.util.Vector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import cz.matejcik.openwig.Engine;
import cz.matejcik.openwig.Thing;
import cz.matejcik.openwig.Action;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;

public class Targets extends ListOfStuff {

	private Action action;
	private Thing thing;

	private Vector validStuff;
	
	public Targets () {
		super("");
		parent = Midlet.actions;
	}

	public Targets reset (String title, Action what, Thing actor) {
		setTitle(title);
		action = what;
		thing = actor;
		makeValidStuff();
		return this;
	}

	public void push () {
		if (validStuff.isEmpty()) {
			Midlet.display.setCurrent(new Alert("no targets", action.notarget, null, AlertType.INFO), parent);
		} else {
			super.push();
		}
	}

	protected void callStuff(Object what) {
		Midlet.push(Midlet.details);
		Thing t = (Thing) what;
		String eventName = "On"+action.getName();
		Engine.callEvent(action.getActor(), eventName, t);
	}

	protected boolean stillValid() {
		return thing.visibleToPlayer();
	}

	private void makeValidStuff() {
		KahluaTable current = Engine.instance.cartridge.currentThings();
		int size = current.len() + Engine.instance.player.inventory.len();
		validStuff = new Vector(size);
		KahluaTableIterator it = current.iterator();
		while (it.advance())
			validStuff.addElement(it.getValue());
		it = Engine.instance.player.inventory.iterator();
		while (it.advance())
			validStuff.addElement(it.getValue());
		
		for (int i = 0; i < validStuff.size(); i++) {
			Thing t = (Thing)validStuff.elementAt(i);
			if (! t.isVisible() || ! action.isTarget(t)) {
				validStuff.removeElementAt(i--);
			}
		}
	}

	protected Vector getValidStuff () {
		return validStuff;
	}

	protected String getStuffName(Object what) {
		Thing t = (Thing)what;
		return t.name;
	}
}
