package renderer.util;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;

public class FastIntMap<V> extends AbstractInt2ObjectMap<V>
{
	private Optional<V>[] array = new Optional[1000];
	private int size = 0;

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public ObjectSet<Entry<V>> int2ObjectEntrySet()
	{
		List<Entry<V>> entries = new ArrayList<>();

		for (int i = 0; i < array.length; i++)
		{
			if (array[i] != null)
			{
				entries.add(new BasicEntry<V>(i, array[i].orElse(null)));
			}
		}

		return new BasicEntrySet<V>(this)
		{
			@Override
			public ObjectIterator<Entry<V>> iterator()
			{
				Iterator<Entry<V>> it = entries.iterator();
				return new ObjectIterator<Entry<V>>()
				{
					@Override
					public boolean hasNext()
					{
						return it.hasNext();
					}

					@Override
					public Entry<V> next()
					{
						return it.next();
					}
				};
			}
		};
	}

	@Override
	public V put(int key, V value)
	{
		if (key >= array.length)
		{
			Optional<V>[] newArray = new Optional[key * 2];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;
		}

		Optional<V> oldValue = array[key];
		array[key] = Optional.ofNullable(value);

		if (oldValue == null)
		{
			size++;
		}

		return oldValue == null ? null : oldValue.orElse(null);
	}

	@Override
	public V get(int key)
	{
		if (key >= array.length)
		{
			return null;
		}

		Optional<V> optional = array[key];
		return optional == null ? null : optional.orElse(null);
	}

	@Override
	public boolean containsKey(final int k)
	{
		return array.length >= k && array[k] != null;
	}

	@Override
	public V remove(int key)
	{
		if (key >= array.length)
		{
			return null;
		}

		Optional<V> oldValue = array[key];
		array[key] = null;

		if (oldValue != null)
		{
			size--;
		}

		return oldValue == null ? null : oldValue.orElse(null);
	}

	@Override
	public V computeIfAbsent(final int key, final IntFunction<? extends V> mappingFunction)
	{
		Optional<V> current = key >= array.length ? null : array[key];

		if (current == null)
		{
			V v = mappingFunction.apply(key);
			put(key, v);
			return v;
		}

		return current.orElse(null);
	}
}
