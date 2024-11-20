/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 21, 2014
 */

package relations;

public interface Subject {

    public void addObserver(Observer obs);
    
    public void removeObserver(Observer obs);
    
    public void notifyObservers();
    
}
