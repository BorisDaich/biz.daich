/**
 *
 */
package biz.daich.common.tools.jpa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * based on code by
 *
 * @author asraf
 *         asraf344@gmail.com
 *         taken from https://github.com/bmvakili/java-dependency-resolver
 *
 */
public class DependencyResolverUtil
{
	private static final Logger l = LogManager.getLogger(DependencyResolverUtil.class.getName());

	public static List<String> getUsedClasses(String name)
	{
		List<String> classNameList = new ArrayList<String>();

		try
		{
			if (l.isDebugEnabled())
			{
				l.debug("getUsedClasses(String) - {}", "Used classes for  " + name); //$NON-NLS-1$ //$NON-NLS-2$
			}
			final Collection<Class<?>> classes = ClassCollector.getClassesUsedBy(name, null);

			for (final Class<?> cls : classes)
			{
				String className = cls.getName();

				// if its not java own class
				if (!className.startsWith("java"))
				{
					// if its not an array or inner class
					if (!className.startsWith("[L") && !className.contains("$"))
					{
						classNameList.add(className);

					}
					// if its some array
					else
						if (className.startsWith("[L"))
						{
							classNameList.add(className.substring(2, className.length() - 1));
						}
				}

			}
		}
		catch (IOException e)
		{
			l.error("getUsedClasses(String)", e); //$NON-NLS-1$

			// TODO Auto-generated catch block
			l.error("getUsedClasses(String)", e); //$NON-NLS-1$
		}

		return classNameList;
	}

}
