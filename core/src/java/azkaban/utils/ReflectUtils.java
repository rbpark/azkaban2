/*
 * Copyright 2014 LinkedIn Corp.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package azkaban.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility package for common Azkaban reflection
 * 
 */
public class ReflectUtils {
	
	private ReflectUtils() {
	}
	
	/**
	 * Get the Class of all the objects
	 * 
	 * @param args The objects to get the Classes from
	 * @return The classes as an array
	 */
	public static Class<?>[] getTypes(Object... args) {
		Class<?>[] argTypes = new Class<?>[args.length];
		for (int i = 0; i < argTypes.length; i++)
			argTypes[i] = args[i].getClass();
		return argTypes;
	}

	/**
	 * Attempts to call a constructor on a class using the given
	 * arguments.
	 * 
	 * @param c The class
	 * @param args The arguments
	 * @return The constructed object
	 */
	public static <T> T callConstructor(Class<T> c, Object... args) {
		return callConstructor(c, getTypes(args), args);
	}

	/**
	 * Attempts to call the class constructor with the given arguments
	 * 
	 * @param c The class
	 * @param args The arguments
	 * @return The constructed object
	 */
	public static <T> T callConstructor(Class<T> c, Class<?>[] argTypes,
			Object[] args) {
		try {
			Constructor<T> cons = c.getConstructor(argTypes);
			return cons.newInstance(args);
		} 
		catch (InvocationTargetException e) {
			throw getCause(e);
		} 
		catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} 
		catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} 
		catch (InstantiationException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Invokes a static method on a Class
	 * 
	 * @param loader The classloader that contains the class
	 * @param className The name of the class
	 * @param methodName The method name
	 * @param args List of objects used as arguments
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object invokeStaticMethod(
			ClassLoader loader, String className, String methodName, Object... args)
		throws ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		
		Class<?> clazz = loader.loadClass(className);

		Class<?>[] argTypes = new Class[args.length];
		for (int i = 0; i < args.length; ++i) {
			// argTypes[i] = args[i].getClass();
			argTypes[i] = args[i].getClass();
		}

		Method method = clazz.getDeclaredMethod(methodName, argTypes);
		return method.invoke(null, args);
	}

	/**
	 * Get the root cause of the Exception
	 * 
	 * @param e The Exception
	 * @return The root cause of the Exception
	 */
	private static RuntimeException getCause(InvocationTargetException e) {
		Throwable cause = e.getCause();
		if (cause instanceof RuntimeException)
			throw (RuntimeException) cause;
		else
			throw new IllegalStateException(e.getCause());
	}

}
