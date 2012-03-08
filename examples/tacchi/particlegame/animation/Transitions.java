package tacchi.particlegame.animation;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

import tacchi.particlegame.Utils;

/**
 * Not sure entirely how to describe this... a singleton factory maybe?
 * Basically, you call <code>get()</code> with the class of the transition you want
 * and the constructor arguments, and it constructs it for you.  If you ever call
 * <code>get()</code> with the same arguments again, it gives you that original instance again.
 * <br>This greatly reduces the number of transition objects needed, but if you need
 * to modify transitions individually after creation, construct them normally or 
 * you'll end up modifying more than you wanted to.
 * @author Joel
 *
 */
public final class Transitions {
	private static HashMap<Transitions.ArgumentEntry, Transition> transitions = new HashMap<ArgumentEntry, Transition>();

	/**
	 * This would be so much easier if ints were objects...
	 */
	private static final HashMap<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>() {
		private static final long serialVersionUID = -8880587951830695907L;
		{
			put(Byte.class, byte.class);
			put(Short.class, short.class);
			put(Integer.class, int.class);
			put(Long.class, long.class);
			put(Float.class, float.class);
			put(Double.class, double.class);
			put(Character.class, char.class);
			put(Boolean.class, boolean.class);
		}
	};
	
	public static HashMap<String, TransitionArgs> identifiers = 
		new HashMap<String, TransitionArgs>();

	static {
		//Find all the classes that implement ITransition and try to call their initialize() methods
		//Otherwise, we can't load them from files, since we don't know their identifiers or arguments
		//Yay reflection!
		ClassFinder finder = new ClassFinder(false);
		Vector<Class<?>> classes = finder.findSubclasses("tacchi.particlegame.animation.Transition");
		
		for (Class<?> c : classes) {
			try {
				c.getDeclaredMethod("initialize").invoke(null);
			} catch (NoSuchMethodException e) {
				continue;
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static Transition get(Class<? extends Transition> type) {
		return get(type, new Object[0]);
	}
	
	public static Transition get(Class<? extends Transition> type, Object...initArgs) {
		ArgumentEntry key = new ArgumentEntry(type, initArgs);
		Transition t = null;
		
		for (Entry<ArgumentEntry, Transition> entry : transitions.entrySet()) {
			if (entry.getKey().equals(key)) {
				t = entry.getValue();
				break;
			}
		}
		
		if (t == null) {
			try {
				Class<?>[] argTypes = new Class<?>[initArgs.length];
				for (int i = 0; i < initArgs.length; i++) {
					if (initArgs[i] instanceof Enum<?>)
						argTypes[i] = ((Enum<?>)initArgs[i]).getDeclaringClass();
					else {
						Class<?> c = primitives.get(initArgs[i].getClass());
						if (c != null)
							argTypes[i] = c;
						else
							argTypes[i] = initArgs[i].getClass();
					}
				}
				
				t = (Transition) type.getConstructor(argTypes).newInstance(initArgs);
				transitions.put(key, t);
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return t;
	}
	
	/**
	 * ohmygod this worked the first time I compiled it!
	 * @param identifier
	 * @param unparsedArgs
	 * @param reversed
	 * @return
	 */
	protected static Transition getFromFile(String identifier, String[] unparsedArgs, boolean reversed) {
		TransitionArgs type = identifiers.get(identifier);
		if (type == null) {
			System.err.println("Invalid identifier " + identifier + ". Args = " + Utils.join(unparsedArgs));
			return null;
		}
		//match unparsedArgs types to identifier types
		ArrayList<Object> args = new ArrayList<Object>(unparsedArgs.length);
		int a = 0;
		for (int t = 0; t < type.args.length; t++) {
			if (type.args[t] == CurveType.class) {
				args.add(reversed? CurveType.Reversed : CurveType.Normal);
			}
			else {
				Object o = parseArg(unparsedArgs[a], type.args[t]);
				if (o == null)
					throw new IllegalArgumentException("Invalid argument.  Correct type is " + type.args[t]);
				args.add(o);
				a++;
			}
		}
		
		return Transitions.get(type.trans, args.toArray());
	}
	
	private static Object parseArg(String arg, Class<?> type) {
		if (type == String.class)
			return arg;
		if (type == int.class)
			return Integer.parseInt(arg);
		if (type == float.class)
			return Float.parseFloat(arg);
		
		if (type == boolean.class) {
			String s = arg.trim().toLowerCase();
			return (s.equals("1") || s.equals("true") || s.equals("yes"));
		}
		if (type == double.class)
			return Double.parseDouble(arg);
		if (type == long.class)
			return Long.parseLong(arg);
		if (type == short.class)
			return Short.parseShort(arg);
			
		return null;
	}

	public static class TransitionArgs {
		public Class<? extends Transition> trans;
		public Class<?>[] args;
		
		public TransitionArgs(Class<? extends Transition> type, Class<?>[] argTypes) {
			this.trans = type;
			this.args = argTypes;
		}
	}
	
	private static class ArgumentEntry {
		public Class<? extends Transition> type;
		public Object[] args;
		
		public ArgumentEntry(Class<? extends Transition> type, Object...initArgs) {
			this.type = type;
			this.args = initArgs;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			
			if (!(obj instanceof ArgumentEntry))
				return false;
			ArgumentEntry object = (ArgumentEntry)obj;
			
			if (!this.type.equals(object.type))
				return false;
			if (this.args.length != object.args.length)
				return false;
			for (int i = 0; i < this.args.length; i++) {
				if (!this.args[i].equals(object.args[i]))
					return false;
			}
			
			return true;
		}
	}
}
