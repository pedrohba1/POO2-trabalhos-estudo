// File   : GameModel.java
// Purpose: blah, blah, blah
// Author : Fred Swartz - February 20, 2007 - Placed in public domain.

package freecell.model;

import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import freecell.view.Card;
import freecell.view.UIFreeCell;

//////////////////////////////////////////////////////////////// Class GameModel
public class GameModel implements Iterable<CardPile> {

    private boolean turno = true;

    // =================================================================== fields
    private CardPile[] _freeCells;
    private CardPile[] _tableau;
    private CardPile[] _foundation;

    private ArrayList<CardPile> _allPiles;

    private ArrayList<ChangeListener> _changeListeners;

    // ... Use the new Java Deque to implement a stack.
    // Push the source and destination piles on everytime a move is made.
    // Pop them off to do the undo. Hmmm, must suppress checking?
    private ArrayDeque<CardPile> _undoStack = new ArrayDeque<CardPile>();

    // ============================================================== constructor
    public GameModel() {
        _allPiles = new ArrayList<CardPile>();

        _freeCells = new CardPile[1];
        _tableau = new CardPileTableau[10];
        // _foundation = new CardPile[4];

        // ... Create empty piles to hold "fouindation"
        // for (int pile = 0; pile < _foundation.length; pile++) {
        // _foundation[pile] = new CardPileFoundation();
        // _allPiles.add(_foundation[pile]);
        // }

        // ... Create empty piles of Free Cells.
        for (int pile = 0; pile < _freeCells.length; pile++) {
            _freeCells[pile] = new CardPileFreeCell();
            _allPiles.add(_freeCells[pile]);
        }

        // ... Arrange the cards into piles.
        for (int pile = 0; pile < _tableau.length; pile++) {
            _tableau[pile] = new CardPileTableau();
            _allPiles.add(_tableau[pile]);
        }

        _changeListeners = new ArrayList<ChangeListener>();

        reset();
    }

    // ==================================================================== reset
    public void reset() {
        Deck deck = new Deck();
        deck.shuffle();

        // ... Empty all the piles.
        for (CardPile p : _allPiles) {
            p.clear();
        }

        // ... Deal the cards into the piles.
        int whichPile = 0;
        for (Card crd : deck) {
            if (whichPile == 10) {
                break;
            }
            _tableau[whichPile].pushIgnoreRules(crd);
            whichPile = (whichPile + 1);

        }

        // ... Tell interested parties (eg, the View) that things have changed.
        _notifyEveryoneOfChanges();
    }

    // TODO: This is a little messy right now, having methods that both
    // return a pile by number, and the array of all piles.
    // Needs to be simplified.

    // ================================================================= iterator
    public Iterator<CardPile> iterator() {
        return _allPiles.iterator();
    }

    // =========================================================== getTableauPile
    public CardPile getTableauPile(int i) {
        return _tableau[i];
    }

    // ========================================================== getTableauPiles
    public CardPile[] getTableauPiles() {
        return _tableau;
    }

    // ========================================================= getFreeCellPiles
    public CardPile[] getFreeCellPiles() {
        return _freeCells;
    }

    // ========================================================== getFreeCellPile
    public CardPile getFreeCellPile(int cellNum) {
        return _freeCells[cellNum];
    }

    // ======================================================= getFoundationPiles
    public CardPile[] getFoundationPiles() {
        return _foundation;
    }

    // ======================================================== getFoundationPile
    public CardPile getFoundationPile(int cellNum) {
        return _foundation[cellNum];
    }

    public void ValuetoUI(Card crd) {
        int value = 1 + crd.getFace().ordinal();
        UIFreeCell.incrementaPontos(value);
    }

    public void change_turn() {
        UIFreeCell.Changeturn();
    }

    // ======================================================= moveFromPileToPile
    public boolean moveFromPileToPile(CardPile source, CardPile target) {
        boolean result = false;
        if (source.size() > 0) {
            Card crd = source.peekTop();

            if (target.rulesAllowAddingThisCard(crd)) {
                target.push(crd);
                source.pop();
                crd.turnFaceUp();

                _notifyEveryoneOfChanges();
                // ... Record on undo stack.
                _undoStack.push(source);
                _undoStack.push(target);
                result = true;
                ValuetoUI(crd);

            }
        }
        change_turn();
        return result;
    }

    // ================================================= _forceMoveFromPileToPile
    // No checking. Not recorded in undoStack. Used by undo.
    private void _forceMoveFromPileToPile(CardPile source, CardPile target) {
        if (source.size() > 0) {
            target.push(source.pop());
            _notifyEveryoneOfChanges();
        }
    }

    // change the turn

    // ============================================================= makeAllPlays
    public void makeAllPlays() {

        boolean worthTrying; // Set true if made a move.
        do {
            worthTrying = false; // Assume nothing is going to be moved.
            // ... Try moving each of the free cells to graveyard.
            for (CardPile freePile : _freeCells) {
                for (CardPile gravePile : _foundation) {
                    worthTrying |= moveFromPileToPile(freePile, gravePile);
                }
            }
            // ... Try moving each of the player piles to graveyard.
            for (CardPile cardPile : _tableau) {
                for (CardPile gravePile : _foundation) {
                    worthTrying |= moveFromPileToPile(cardPile, gravePile);
                }
            }

        } while (worthTrying);
    }

    // ======================================================== addChangeListener
    public void addChangeListener(ChangeListener someoneWhoWantsToKnow) {
        _changeListeners.add(someoneWhoWantsToKnow);
    }

    // ================================================= _notifyEveryoneOfChanges
    private void _notifyEveryoneOfChanges() {
        for (ChangeListener interestedParty : _changeListeners) {
            interestedParty.stateChanged(new ChangeEvent("Game state changed."));
        }
    }
}