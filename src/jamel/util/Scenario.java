/*
 * (C) Copyright 2013, Yves Quemener.
 * 
 * Project Info <http://p.seppecher.free.fr/jamel/>. 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JAMEL. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package jamel.util;

import jamel.Circuit;
import jamel.util.Timer.JamelPeriod;
import jamel.spheres.monetarySphere.Bank;
import jamel.agents.firms.ProductionType;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import org.jfree.data.time.Month;

public class Scenario {

    public class Event {
        
        private JamelPeriod period;
        private String target;
        private String eventType;
        private HashMap<String, Object> arguments;
        
        public Event(String s, Circuit c) throws java.lang.IllegalArgumentException{
            String ss[] = s.split("\\(")[0].split("\\.");
            if(ss.length<3) {
                throw new IllegalArgumentException(ss.length + " Not a valid event : "+s);
            }
            this.period = c.newJamelPeriod(Month.parseMonth(ss[1]));
            this.target = ss[0];
            this.eventType = ss[2];
            
            String argss[] = s.split("[\\(\\)]");
            if((argss.length<2) || (argss.length>3)){
                throw new IllegalArgumentException("Not a valid event : "+s);
            }
            
            arguments = new HashMap<String, Object>();
            for(String argcouple:argss[1].split(",")) {
                System.out.println("arrgcouple  : "+ argcouple);
                String couple[] = argcouple.split("=");
                if(couple.length!=2){
                    throw new IllegalArgumentException("Not a valid argumene : "+argcouple);
                }
                Object o;

                try{ o = Integer.parseInt(couple[1]);}
                catch(NumberFormatException e) {
                  try{ o = Double.parseDouble(couple[1]);}
                  catch(NumberFormatException e2) {
                    o = couple[1];
                  }
                }
                // Special case for production type, for firms ( TODO : should 
                // actually be a mechanism for any enum type)
                if((this.target.equals("Firms"))&&
                   (couple[0].equals("production")))
                {
                  if(couple[1].equals("intermediateProduction"))
                      o = ProductionType.intermediateProduction;
                  else if(couple[1].equals("finalProduction"))
                      o = ProductionType.finalProduction;
                  else if(couple[1].equals("integratedProduction"))
                      o = ProductionType.integratedProduction;
                  else
                    throw new IllegalArgumentException();
                }
                arguments.put(couple[0], o);
                System.out.println(couple[0] + "=" + o);
            }
        }
        
        public void executeOnCircuit(Circuit circuit) {
            System.out.println("Entering event execution "+ this.toString());
            if(this.eventType.equals("set")) {
                if(this.target.equals("Bank")) {
                    System.out.println("Setting bank parameters "+arguments.size());
                    for(String k : arguments.keySet()) {
                        try {
                          System.out.println("Setting field "+k+" to "+arguments.get(k));
                          circuit.bank.getClass().getField(k).set(circuit.bank, arguments.get(k));
                                                  }
                        catch (IllegalAccessError e) {
                            throw new RuntimeException("Illegal parameter: "+k);
                        }
                        catch (NoSuchFieldException e)
                        {
                          throw new RuntimeException("No such field: "+k);
                        }
                        catch (IllegalAccessException e) {
                          throw new RuntimeException("Illegal access to "+k);
                        }
                          
                    }
                }
            }
            if(this.eventType.equals("new")) {
                if(this.target.equals("Firms")) {
                    System.out.println("Creating new firms "+arguments.size());
                    circuit.firms.newFirms(arguments);
                }
                else if(this.target.equals("Households")) {
                    System.out.println("Creating new households "+arguments.size());
                    Map<String, String> ss = new HashMap<String, String>();
                    for(String k : arguments.keySet()) {
                      ss.put(k, (String)arguments.get(k).toString());
                    }
                    circuit.households.newHouseholds(ss);
                }
            }
        }
        @Override
        public String toString(){
            return this.target+"."+this.period.toString()+"."+this.eventType+"(...)";
        }
                
    }
    
    private final LinkedList<Event> events;
    private final jamel.Circuit circuit;
    
    public Scenario(LinkedList<String> aScenario, Circuit c) {
        this.circuit = c;
        this.events = new LinkedList<Event>();
        for(String s:aScenario) {
            try {
              if(!events.add(new Event(s, c)))
              {
                System.out.println("Could not parse event : " + s);
              }
            }
            catch(IllegalArgumentException e) {
                /*System.out.println("Invalid because :");*/
                e.printStackTrace();
            }
        }
    }
        
    public void runPeriod(Timer.JamelPeriod period) {
        for(Event evt:events){
            if(evt.period.equals(period)) {
                System.out.println("Executing "+evt.toString());
                evt.executeOnCircuit(circuit);
            }
        }
    }
}
