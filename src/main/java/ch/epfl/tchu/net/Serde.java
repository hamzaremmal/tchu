package ch.epfl.tchu.net;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import ch.epfl.tchu.SortedBag;

/**
 * This interface represents a serde that can serialize and deserialize an object
 * It also defines static concrete methods that operates on enumerations 
 * and that can adapt an existing serde
 * @author Hamza REMMAL (310917)
 * @author Mehid ZIAZI (311475)
 */
public interface Serde<T> {
	/**
	 * serialize the parameter T 
	 * @param object (T) - the object to serialize
	 * @return (String) - the  string serialization of the parameters
	 */
	String serialize(T object);
	
	/**
	 * deserialize the given string to an object of class T
	 * @param string (String) - the string to deserialize
	 * @return (T) - the string interpreted as an object of class T
	 */
	T deserialize(String string);
	
	/**
	 * creates a serde that can serialize and deserialize an object of class T using the two functions given in parameters
	 * @param serialize (Function) - the serializing function
	 * @param deserialize (Function) - the deserializing function
     * @param <T> ???
	 * @return (Serde) - a serde that operates on objects of class T
	 */
	public static <T> Serde<T>  of(Function<T, String> serialize ,Function<String, T> deserialize){
	    return new Serde<T>() {
	        @Override
			public String serialize(T object) { 
	            return serialize.apply(object);
	            }
			
			@Override 
			public T deserialize(String string) { 
			    return deserialize.apply(string);
			    }
			};
	}
	/**
	 * creates a serde that can serialize and deserialize an object belonging to an enumeration 
	 * @param list (List) - the list of all objects of the enumeration
     * @param <T> ???
	 * @return (Serde) - a serde that operates on objects of class T
	 */
	public static <T> Serde<T> oneOf(List<T> list){
		assert(!list.isEmpty());
		Function<T,String> serialize   = t -> Integer.toString(list.indexOf(t));
		Function<String,T> deserialize = s -> list.get(Integer.parseInt(s));
		return Serde.of(serialize, deserialize);	
	}
	/**
	 * creates a serde that can serialize and deserialize a list of objects of class T
	 * @param serde (Serde) - a serde that operates on the objects of class T
	 * @param c (String) - the delimitation between the serializations of the objects in the list
     * @param <T> ???
	 * @return (Serde) - a serde that operates on a list of objects of class T
	 */
	public static <T> Serde<List<T>> listOf(Serde<T> serde, String c){
	    Function<List<T>,String> serialize   = l ->  l.isEmpty() ? "" : String.join(c, l.stream().map(o -> serde.serialize(o)).collect(Collectors.toList()));
	    Function<String,List<T>> deserialize = s -> s.equals("") ? List.of() : Arrays.asList(s.split(Pattern.quote(c),-1))
	                                                                                 .stream()
	                                                                                 .map(string -> serde.deserialize(string))
	                                                                                 .collect(Collectors.toList()) ;	
	    return Serde.of(serialize, deserialize);	
	    }
	
	/**
	 * creates a serde that can serialize and deserialize a sorted bag of objects of class T
	 * @param serde (Serde) - a serde that operates on the objects of class T
	 * @param c (String) - the delimitation between the serializations of the objects in the sorted bag
     * @param <T> ???
	 * @return (Serde) - a serde that operates on a sorted bag of objects of class T
	 */
	public static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde,String c){
	    Function<SortedBag<T>,String> serialize   = o ->  Serde.listOf(serde, c).serialize(o.toList()); 
	    Function<String,SortedBag<T>> deserialize = s ->  SortedBag.of(Serde.listOf(serde, c).deserialize(s));
	    return Serde.of(serialize, deserialize);
	    }
	
}