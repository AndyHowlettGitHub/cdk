package org.openscience.cdk.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.Bond;

/**
 * Undo/Redo Edit class for the AdjustBondOrdesAction, containing the methods
 * for undoing and redoing the regarding changes
 * 
 * @author tohel
 */
public class AdjustBondOrdersEdit extends AbstractUndoableEdit {

	private HashMap changedBondOrders;

	/**
	 * @param changedBonds
	 *            A HashMap containing the changed atoms as key and an Array
	 *            with the former and the changed bondOrder
	 */
	public AdjustBondOrdersEdit(HashMap changedBonds) {
		this.changedBondOrders = changedBonds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		Set keys = changedBondOrders.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			Bond bond = (Bond) it.next();
			double[] bondOrders = (double[]) changedBondOrders.get(bond);
			bond.setOrder(bondOrders[0]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		Set keys = changedBondOrders.keySet();
		Iterator it = keys.iterator();
		while (it.hasNext()) {
			Bond bond = (Bond) it.next();
			double[] bondOrders = (double[]) changedBondOrders.get(bond);
			bond.setOrder(bondOrders[1]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String getPresentationName() {
		return "AdjustBondOrders";
	}
}
