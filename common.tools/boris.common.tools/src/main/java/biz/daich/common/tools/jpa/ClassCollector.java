/**
 *
 */
package biz.daich.common.tools.jpa;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * Implementation of the ASM interface
 *
 * @author asraf asraf344@gmail.com
 *
 *         http://stackoverflow.com/questions/3734825/find-out-which-classes-of-a-given-api-are-used
 */
public class ClassCollector extends Remapper
{

	private final Set<Class<?>>	classNames;
	private final String		prefix;

	/**
	 * @param classNames
	 *            = collection where to put what was found
	 * @param prefix
	 *            - common prefix for all classes that will be retrieved
	 */
	public ClassCollector(final Set<Class<?>> classNames, final String prefix)
	{
		this.classNames = classNames;
		this.prefix = prefix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapDesc(final String desc)
	{
		if (desc.startsWith("L"))
		{
			this.addType(desc.substring(1, desc.length() - 1));
		}
		return super.mapDesc(desc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] mapTypes(final String[] types)
	{
		for (final String type : types)
		{
			this.addType(type);
		}
		return super.mapTypes(types);
	}

	private void addType(final String type)
	{
		final String className = type.replace('/', '.');

		if (this.prefix != null && className.startsWith(this.prefix))
		{
			if (className.startsWith(this.prefix))
			{
				try
				{
					this.classNames.add(Class.forName(className));
				}
				catch (final ClassNotFoundException e)
				{
					//throw new IllegalStateException ( e );
					e.toString();
				}
			}

		}
		else
		{
			try
			{
				if (!className.contains("jxbrowser"))
				{
					//System.out.println ("adding for " +className);
					this.classNames.add(Class.forName(className));
				}

			}

			catch (Exception e)
			{
				//throw new IllegalStateException ( e );
				e.toString();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String mapType(final String type)
	{
		this.addType(type);
		return type;
	}

	/**
	 *
	 * @param name
	 *            class name
	 * @param prefix
	 *            common prefix for all classes that will be retrieved
	 * @return set of the classes that the given className depends on.
	 * @throws IOException
	 *             - if any class related operations go wrong
	 */
	public static Set<Class<?>> getClassesUsedBy(final String name, final String prefix) throws IOException
	{
		final ClassReader reader = new ClassReader(name);
		final Set<Class<?>> classes = new TreeSet<Class<?>>(new Comparator<Class<?>>() {

			@Override
			public int compare(final Class<?> o1, final Class<?> o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		final Remapper remapper = new ClassCollector(classes, prefix);
		final ClassVisitor inner = new EmptyVisitor();
		final ClassRemapper visitor = new ClassRemapper(inner, remapper);
		try
		{
			reader.accept(visitor, ClassReader.EXPAND_FRAMES);
		}
		catch (Exception ex)
		{
			ex.toString();
		}

		return classes;
	}

	/**
	 * empty visitor implementation recommended on the asm mailing list as replacement of the deprecated one used by the original code
	 */
	public static class EmptyVisitor extends ClassVisitor
	{

		/**
		 * does nothing
		 */
		protected final AnnotationVisitor av = new AnnotationVisitor(Opcodes.ASM5) {

			@Override
			public AnnotationVisitor visitAnnotation(String name, String desc)
			{
				return this;
			}

			@Override
			public AnnotationVisitor visitArray(String name)
			{
				return this;
			}
		};

		/**
		 * default c'tor
		 */
		public EmptyVisitor()
		{
			super(Opcodes.ASM5);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible)
		{
			return av;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
		{
			return av;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
		{
			return new FieldVisitor(Opcodes.ASM5) {

				@Override
				public AnnotationVisitor visitAnnotation(String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
				{
					return av;
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			return new MethodVisitor(Opcodes.ASM5) {

				@Override
				public AnnotationVisitor visitAnnotationDefault()
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitAnnotation(String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
				{
					return av;
				}

				@Override
				public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible)
				{
					return av;
				}
			};
		}
	}
}
