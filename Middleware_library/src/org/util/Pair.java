/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.util;

/**
 *
 * @author marcx87
 */
public class Pair <T, U>
{
   private final T first;
   private final U second;
   private transient final int hash;

   public Pair(T f, U s)
   {
        this.first = f;
        this.second = s;
        hash = (first == null? 0 : first.hashCode() * 31)
              +(second == null? 0 : second.hashCode());
   }

   public T getFirst()
   {
        return first;
   }
   public U getSecond()
   {
        return second;
   }

   @Override
   public int hashCode()
   {
        return hash;
   }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<T, U> other =  (Pair<T, U>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        return true;
    }

  
}
