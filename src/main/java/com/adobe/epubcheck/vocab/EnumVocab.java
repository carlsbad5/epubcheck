package com.adobe.epubcheck.vocab;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

/**
 * A {@link Vocab} implementation that is backed by an {@link Enum}.
 * 
 * <p>
 * Property names will be computed from {@link Enum} constant names by applying
 * the following transformation:
 * </p>
 * <ul>
 * <li>The name is converted to lower case</li>
 * <li>The underscore character (<code>'_'</code>) is replaced by the hyphen
 * character (<code>'-'</code>).</li>
 * </ul>
 * 
 * @author Romain Deltour
 *
 */
public final class EnumVocab implements Vocab
{

  public final static Function<Enum<?>, String> ENUM_TO_NAME = new Function<Enum<?>, String>()
  {
    @Override
    public String apply(Enum<?> enumee)
    {
      return enumee.toString().toLowerCase().replace('_', '-');
    }
  };

  private final Map<String, Property> index;

  /**
   * Creates a new vocabulary backed by the given {@link Enum} class and with
   * properties having the common URI stem <code>base</code>. Properties of the
   * created vocabulary will have an empty prefix (in other words, this creates
   * a default vocabulary).
   * 
   * @param clazz
   *          the enumeration backing this vocabulary.
   * @param base
   *          the common stem URI of properties in this vocabulary.
   */
  public <T extends Enum<T>> EnumVocab(final Class<T> clazz, final String base)
  {
    this(clazz, base, null);
  }

  /**
   * Creates a new vocabulary backed by the given {@link Enum} class and with
   * properties having the common URI stem <code>base</code> and prefix
   * <code>prefix</code>
   * 
   * @param clazz
   *          the enumeration backing this vocabulary.
   * @param base
   *          the common stem URI of properties in this vocabulary.
   * @param prefix
   *          the common prefix of properties in this vocabulary.
   */
  public <T extends Enum<T>> EnumVocab(final Class<T> clazz, final String base, final String prefix)
  {
    this.index = ImmutableMap.copyOf(Maps.transformEntries(
        Maps.uniqueIndex(EnumSet.allOf(clazz), ENUM_TO_NAME),
        new EntryTransformer<String, T, Property>()
        {

          @Override
          public Property transformEntry(String name, T enumee)
          {
            return Property.newFrom(name, base, prefix, enumee);
          }

        }));
  }

  @Override
  public Optional<Property> lookup(String name)
  {
    return Optional.fromNullable(index.get(name));
  }
}