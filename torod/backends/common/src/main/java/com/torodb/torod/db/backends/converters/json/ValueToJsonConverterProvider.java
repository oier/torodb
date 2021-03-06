/*
 *     This file is part of ToroDB.
 *
 *     ToroDB is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     ToroDB is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with ToroDB. If not, see <http://www.gnu.org/licenses/>.
 *
 *     Copyright (c) 2014, 8Kdata Technology
 *     
 */

package com.torodb.torod.db.backends.converters.json;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.torodb.torod.core.subdocument.ScalarType;
import com.torodb.torod.db.backends.converters.ValueConverter;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 *
 */
public class ValueToJsonConverterProvider {

    /**
     * Types that are not supported.
     */
    private static final EnumSet<ScalarType> UNSUPPORTED_TYPES
            = EnumSet.noneOf(ScalarType.class);
    /**
     * Types that must be supported.
     */
    private static final Set<ScalarType> SUPPORTED_TYPES
            = Sets.difference(EnumSet.allOf(ScalarType.class), UNSUPPORTED_TYPES);
    private final Map<ScalarType, ValueConverter> converters;

    private ValueToJsonConverterProvider() {
        converters = Maps.newEnumMap(ScalarType.class);
        converters.put(ScalarType.ARRAY, new ArrayValueToJsonConverter());
        converters.put(ScalarType.BOOLEAN, new BooleanValueToJsonConverter());
        converters.put(ScalarType.DATE, new DateValueToJsonConverter());
        converters.put(ScalarType.INSTANT, new InstantValueToJsonConverter());
        converters.put(ScalarType.DOUBLE, new DoubleValueToJsonConverter());
        converters.put(ScalarType.INTEGER, new IntegerValueToJsonConverter());
        converters.put(ScalarType.LONG, new LongValueToJsonConverter());
        converters.put(ScalarType.NULL, new NullValueToJsonConverter());
        converters.put(ScalarType.STRING, new StringValueToJsonConverter());
        converters.put(ScalarType.TIME, new TimeValueToJsonConverter());
        converters.put(ScalarType.MONGO_OBJECT_ID, new MongoObjectIdValueToJsonConverter());
        converters.put(ScalarType.MONGO_TIMESTAMP, new MongoTimestampValueToJsonConverter());
        converters.put(ScalarType.BINARY, new BinaryValueToJsonConverter());

        SetView<ScalarType> withoutConverter = Sets.difference(converters.keySet(), SUPPORTED_TYPES);
        if (!withoutConverter.isEmpty()) {
            throw new AssertionError("It is not defined how to convert from the following scalar "
                    + "types to json: " + withoutConverter);
        }
    }

    public static ValueToJsonConverterProvider getInstance() {
        return ValueToJsonConverterProviderHolder.INSTANCE;
    }

    @Nonnull
    public ValueConverter getConverter(ScalarType valueType) {
        ValueConverter converter = converters.get(valueType);
        if (converter == null) {
            throw new AssertionError("There is no converter that converts "
                    + "elements of type " + valueType);
        }
        return converter;
    }

    private static class ValueToJsonConverterProviderHolder {

        private static final ValueToJsonConverterProvider INSTANCE
                = new ValueToJsonConverterProvider();
    }

    //@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "UPM_UNCALLED_PRIVATE_METHOD")
    private Object readResolve() {
        return ValueToJsonConverterProvider.getInstance();
    }
}
