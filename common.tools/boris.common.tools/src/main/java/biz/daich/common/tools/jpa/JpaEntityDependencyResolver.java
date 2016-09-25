package biz.daich.common.tools.jpa;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.Reflection;

/**
 * <b>USECASE:</b> Spring project has more than one database and hence more than one EntityManagerFactory
 * in this case see the Spring doc http://docs.spring.io/spring-boot/docs/current/reference/html/howto-data-access.html#howto-use-two-entity-managers
 * Let say it a in memory DB that will hold complex entities of a single complex type.
 * Important is that it better to configure the list of managed entities in the EntityManagerFactory that are relevant only.
 * For this you need to know what are the classes and types that
 * <ul>
 * <li>that entity composed of
 * <li>and be able to refactor the code
 * <li>and not to have problems when adding or removing members from this entity
 * </ul>
 * so use the
 *
 * <pre>
 * PersistenceUnitPostProcessor persistenceUnitPostProcessor = new PersistenceUnitPostProcessor()
 * {
 *
 * &#64;Override
 * 			public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui)
 *           {
 *           Collection&lt;String&gt; jpaAnnotatedClassNamesDependedOn = JpaEntityDependencyResolver.getJpaAnnotatedClassNamesDependedOn(BrokerLoad.class);
 *           for (String managedClassName : jpaAnnotatedClassNamesDependedOn)
 *           {
 *           pui.addManagedClassName(managedClassName);
 *           }
 *           }
 *           };
 *
 *           &#64;Bean
 *           public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
 *           EntityManagerFactoryBuilder builder) {
 *           return builder
 *           .setPersistenceUnitPostProcessors(persistenceUnitPostProcessor);
 *           .build();
 *           }
 * </pre>
 *
 * the Idea is that you find all classes that your entity depends on
 * than you filter out those that has nothing to do with JPA
 * <p>
 * Has something to do with JPA means has any JPA annotation not only the @Entity Annotation
 * but things like @MappedSuperclass etc
 * for this we list all classes in javax.persistence package keep only the annotations
 * and each class that is referenced from our top entity check if it has any of the JPA annotations
 *
 * required dependencies are ASM and Apachecommons-io
 *
 */
public class JpaEntityDependencyResolver
{
	private static final Logger l = LogManager.getLogger(JpaEntityDependencyResolver.class.getName());

	private JpaEntityDependencyResolver()
	{
		super();
	}

	/**
	 * @param clazz
	 *            - the top Entity class that you want to have a list of all related and dependent classes
	 * @return list of class names that the clazz depend on and are JPA annotated.may be empty list but never null.
	 *         IMPORTANT the returned list does not include the clazz itself.
	 *
	 */
	public static Collection<String> getJpaAnnotatedClassNamesDependedOn(Class<?> clazz)
	{
		Preconditions.checkNotNull(clazz);
		Set<String> res = Sets.newHashSet();
		final String className = clazz.getCanonicalName();
		List<String> usedClassNames = DependencyResolverUtil.getUsedClasses(className);
		l.debug("===== depend on classes: =====");
		l.debug(usedClassNames);
		for (String usedClassName : usedClassNames)
		{
			if (isJpaAnnotatedClass(usedClassName))
			{
				res.add(usedClassName);
			}
		}
		l.debug("===== depend on JPA classes: =====");
		l.debug(res);
		return res;
	}

	/**
	 * check if the class given by the full name is annotated by any of the JPA annotations.
	 * If not annotated or something went wrong returns false.
	 *
	 * @param className
	 *            - full class name that is on the class path and can be instantiated by Class.forName(className);
	 * @return true only if the class was loaded and does have at least one of the javax.persistance.* annotations
	 */
	static boolean isJpaAnnotatedClass(String className)
	{
		if (Strings.isNullOrEmpty(className))
		{
			l.warn("The className must be a valid class available on the class path");
			return false;
		}
		Class<?> clazz;
		try
		{
			clazz = Class.forName(className);
			for (Class<? extends Annotation> annotationClass : jpaAnnotations)
			{
				if (clazz.isAnnotationPresent(annotationClass))
				{
					if (l.isTraceEnabled())
					{
						l.trace("Class " + className + " is annotated by " + annotationClass.getCanonicalName());
					}
					return true;
				}
			}
		}
		catch (ClassNotFoundException e)
		{
			l.warn("isJPAAnnotatedClass(String)", e); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * static list of all the javax.persistance.* annotations classes.
	 */
	protected static Collection<Class<? extends Annotation>> jpaAnnotations = getAllJPAannotations();

	/**
	 * initializer of the jpaAnnotations list
	 *
	 * @return a collection of classes or empty collection if JPA javax.persistance.* is not on the classpath
	 */

	protected static Collection<Class<? extends Annotation>> getAllJPAannotations()
	{
		Set<Class<? extends Annotation>> res = Sets.newHashSet();
		try
		{
			ClassPath classPath = ClassPath.from(JpaEntityDependencyResolver.class.getClassLoader());
			String packageName = Reflection.getPackageName(Entity.class);
			ImmutableSet<ClassInfo> topLevelClasses = classPath.getTopLevelClasses(packageName);
			for (ClassInfo ci : topLevelClasses)
			{
				Class<?> clazz = ci.load();
				if (clazz != null && clazz.isAnnotation())
				{
					res.add((Class<? extends Annotation>) clazz);
				}
			}
		}
		catch (IOException e)
		{
			l.error("getAllJPAannotations()", e); //$NON-NLS-1$
		}
		l.trace("JPA Annotation list size: " + res.size());
		return res;
	}
}
